package com.example.data.update

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class AppUpdateRepository(
    private val context: Context? = null
) {
    private val prefs = context?.getSharedPreferences("craftmind_update_prefs", Context.MODE_PRIVATE)

    var repoSlug: String
        get() = prefs?.getString("github_repo_slug", "InsaneBadPC/Craftmind") ?: "InsaneBadPC/Craftmind"
        set(value) {
            prefs?.edit()?.putString("github_repo_slug", value.trim())?.apply()
        }

    var githubToken: String
        get() = prefs?.getString("github_token", "") ?: ""
        set(value) {
            prefs?.edit()?.putString("github_token", value.trim())?.apply()
        }

    suspend fun checkLatestRelease(): Result<AppUpdateInfo> = withContext(Dispatchers.IO) {
        val currentSlug = repoSlug.ifBlank { "InsaneBadPC/Craftmind" }
        val token = githubToken.trim()

        try {
            // First attempt: /releases/latest
            val latestUrl = "https://api.github.com/repos/$currentSlug/releases/latest"
            val latestResult = fetchReleaseJson(latestUrl, token)

            val releaseJson: JSONObject? = if (latestResult.isSuccess) {
                latestResult.getOrNull()
            } else {
                // Fallback attempt: /releases (list all releases and take the first one)
                val allReleasesUrl = "https://api.github.com/repos/$currentSlug/releases?per_page=5"
                val allResult = fetchReleasesArray(allReleasesUrl, token)
                if (allResult.isSuccess && (allResult.getOrNull()?.length() ?: 0) > 0) {
                    allResult.getOrNull()?.getJSONObject(0)
                } else {
                    null
                }
            }

            if (releaseJson == null) {
                val errorMsg = latestResult.exceptionOrNull()?.message
                    ?: "V repozitáři $currentSlug zatím nebyl publikován žádný release nebo je repozitář soukromý."

                return@withContext Result.success(
                    AppUpdateInfo(
                        hasUpdate = false,
                        currentVersion = BuildConfig.VERSION_NAME,
                        latestVersion = BuildConfig.VERSION_NAME,
                        releaseTitle = "Zatím žádné vydání",
                        releaseNotes = "GitHub Actions buď ještě nedokončil publikování, repozitář je soukromý, nebo zatím nebylo vydáno žádné release.",
                        repoSlug = currentSlug,
                        errorMessage = errorMsg,
                        releaseHtmlUrl = "https://github.com/$currentSlug/releases"
                    )
                )
            }

            val rawTagName = releaseJson.optString("tag_name", "")
            val cleanLatestVersion = rawTagName.removePrefix("v").removePrefix("V").trim()
            val cleanCurrentVersion = BuildConfig.VERSION_NAME.removePrefix("v").removePrefix("V").trim()

            val releaseTitle = releaseJson.optString("name", "Vydání $rawTagName")
            val releaseNotes = releaseJson.optString("body", "Automatické vydání z GitHub Action.")
            val publishedAt = releaseJson.optString("published_at", "")
            val htmlUrl = releaseJson.optString("html_url", "https://github.com/$currentSlug/releases")

            // Look for .apk asset
            var apkUrl: String? = null
            var apkName = ""
            var apkSizeMb: Double? = null

            val assets = releaseJson.optJSONArray("assets")
            if (assets != null) {
                for (i in 0 until assets.length()) {
                    val asset = assets.getJSONObject(i)
                    val name = asset.optString("name", "")
                    if (name.endsWith(".apk", ignoreCase = true)) {
                        apkUrl = asset.optString("browser_download_url", "")
                        apkName = name
                        val sizeBytes = asset.optLong("size", 0L)
                        if (sizeBytes > 0) {
                            apkSizeMb = sizeBytes / (1024.0 * 1024.0)
                        }
                        break
                    }
                }
            }

            val isNewer = isVersionNewer(cleanLatestVersion, cleanCurrentVersion)

            Result.success(
                AppUpdateInfo(
                    hasUpdate = isNewer,
                    latestVersion = cleanLatestVersion.ifBlank { cleanCurrentVersion },
                    currentVersion = cleanCurrentVersion,
                    releaseTitle = releaseTitle,
                    releaseNotes = releaseNotes,
                    publishedAt = publishedAt,
                    apkDownloadUrl = apkUrl,
                    apkFileName = apkName.ifBlank { "Craftmind-v$cleanLatestVersion.apk" },
                    apkSizeMb = apkSizeMb,
                    releaseHtmlUrl = htmlUrl,
                    repoSlug = currentSlug,
                    errorMessage = null
                )
            )
        } catch (e: Exception) {
            Log.e("AppUpdateRepository", "Error checking for updates", e)
            Result.failure(e)
        }
    }

    private fun fetchReleaseJson(urlStr: String, token: String): Result<JSONObject> {
        return try {
            val url = URL(urlStr)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 8000
                readTimeout = 8000
                setRequestProperty("Accept", "application/vnd.github.v3+json")
                setRequestProperty("User-Agent", "Craftmind-Android-App")
                if (token.isNotBlank()) {
                    setRequestProperty("Authorization", "Bearer $token")
                }
            }

            val code = connection.responseCode
            if (code == 200) {
                val text = BufferedReader(InputStreamReader(connection.inputStream)).use { it.readText() }
                Result.success(JSONObject(text))
            } else {
                Result.failure(Exception("HTTP $code: ${connection.responseMessage}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun fetchReleasesArray(urlStr: String, token: String): Result<JSONArray> {
        return try {
            val url = URL(urlStr)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 8000
                readTimeout = 8000
                setRequestProperty("Accept", "application/vnd.github.v3+json")
                setRequestProperty("User-Agent", "Craftmind-Android-App")
                if (token.isNotBlank()) {
                    setRequestProperty("Authorization", "Bearer $token")
                }
            }

            val code = connection.responseCode
            if (code == 200) {
                val text = BufferedReader(InputStreamReader(connection.inputStream)).use { it.readText() }
                Result.success(JSONArray(text))
            } else {
                Result.failure(Exception("HTTP $code: ${connection.responseMessage}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun isVersionNewer(latest: String, current: String): Boolean {
        if (latest.isBlank() || current.isBlank()) return false
        if (latest == current) return false

        try {
            val latestParts = latest.split(".", "-", "_").mapNotNull { it.toIntOrNull() }
            val currentParts = current.split(".", "-", "_").mapNotNull { it.toIntOrNull() }

            val maxLen = maxOf(latestParts.size, currentParts.size)
            for (i in 0 until maxLen) {
                val l = latestParts.getOrElse(i) { 0 }
                val c = currentParts.getOrElse(i) { 0 }
                if (l > c) return true
                if (l < c) return false
            }
        } catch (e: Exception) {
            return latest.compareTo(current) > 0
        }

        return false
    }

    fun startDownload(context: Context, downloadUrl: String, fileName: String) {
        try {
            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager
            if (downloadManager != null && downloadUrl.startsWith("http")) {
                val uri = Uri.parse(downloadUrl)
                val request = DownloadManager.Request(uri).apply {
                    setTitle("Stahování aktualizace Craftmind")
                    setDescription(fileName)
                    setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                    setMimeType("application/vnd.android.package-archive")
                }
                downloadManager.enqueue(request)
                Toast.makeText(context, "Stahování zahájeno: $fileName (viz stavová lišta)", Toast.LENGTH_LONG).show()
            } else {
                openUrlInBrowser(context, downloadUrl)
            }
        } catch (e: Exception) {
            Log.e("AppUpdateRepository", "DownloadManager failed, opening browser", e)
            openUrlInBrowser(context, downloadUrl)
        }
    }

    fun openUrlInBrowser(context: Context, url: String) {
        try {
            val cleanUrl = if (!url.startsWith("http://") && !url.startsWith("https://")) {
                "https://$url"
            } else url
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(cleanUrl)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Nelze otevřít odkaz: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
