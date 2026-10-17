package com.example.data.firebase

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.example.R
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FirebaseAuthManager(private val context: Context) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val credentialManager: CredentialManager = CredentialManager.create(context)
    private val scope = CoroutineScope(Dispatchers.Main)

    private val _currentUser = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    private val _isSigningIn = MutableStateFlow(false)
    val isSigningIn: StateFlow<Boolean> = _isSigningIn.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    init {
        auth.addAuthStateListener { firebaseAuth ->
            _currentUser.value = firebaseAuth.currentUser
        }
    }

    suspend fun signInWithGoogle(onSuccess: (() -> Unit)? = null, onError: ((String) -> Unit)? = null) {
        _isSigningIn.value = true
        _authError.value = null

        try {
            val serverClientId = context.getString(R.string.default_web_client_id)
            val signInWithGoogleOption = GetSignInWithGoogleOption.Builder(serverClientId)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(signInWithGoogleOption)
                .build()

            val result = credentialManager.getCredential(
                request = request,
                context = context
            )

            val credential = result.credential
            if (credential is androidx.credentials.CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val firebaseCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                val authResult = auth.signInWithCredential(firebaseCredential).await()
                _currentUser.value = authResult.user
                onSuccess?.invoke()
            } else {
                val err = "Neočekávaný typ přihlašovacích údajů"
                _authError.value = err
                onError?.invoke(err)
            }
        } catch (e: GetCredentialCancellationException) {
            Log.d("FirebaseAuthManager", "Uživatel zrušil výběr účtu Google", e)
            _authError.value = null // Not a hard error, user just cancelled dialog
        } catch (e: GetCredentialException) {
            Log.e("FirebaseAuthManager", "Chyba CredentialManager", e)
            val msg = e.localizedMessage ?: "Chyba přihlášení Google"
            _authError.value = msg
            onError?.invoke(msg)
        } catch (e: Exception) {
            Log.e("FirebaseAuthManager", "Chyba Firebase přihlášení", e)
            val msg = e.localizedMessage ?: "Přihlášení se nezdařilo"
            _authError.value = msg
            onError?.invoke(msg)
        } finally {
            _isSigningIn.value = false
        }
    }

    fun signOut() {
        auth.signOut()
        _currentUser.value = null
    }
}
