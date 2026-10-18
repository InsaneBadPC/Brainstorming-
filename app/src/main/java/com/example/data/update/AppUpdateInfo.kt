package com.example.data.update

data class AppUpdateInfo(
    val hasUpdate: Boolean = false,
    val latestVersion: String = "",
    val currentVersion: String = "",
    val releaseTitle: String = "",
    val releaseNotes: String = "",
    val publishedAt: String = "",
    val apkDownloadUrl: String? = null,
    val apkFileName: String = "",
    val apkSizeMb: Double? = null,
    val releaseHtmlUrl: String = "",
    val repoSlug: String = "InsaneBadPC/Brainstorming-",
    val errorMessage: String? = null,
    val isChecking: Boolean = false,
    val checkTimestamp: Long = System.currentTimeMillis()
)
