package com.example.projectmdp.ui.module.login

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectmdp.data.repository.AuthRepository
import com.example.projectmdp.data.source.remote.RetrofitInstance
import com.example.projectmdp.data.source.remote.VerifyTokenRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    var email by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set
    var isLoading by mutableStateOf(false)
        private set

    var idToken by mutableStateOf("")
        private set

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _googleSignInEvent = MutableSharedFlow<Unit>()
    val googleSignInEvent = _googleSignInEvent.asSharedFlow()
    private val _resetPasswordState = MutableStateFlow<Result<String>?>(null)
    val resetPasswordState = _resetPasswordState.asStateFlow()

    fun onEmailChange(newEmail: String) { email = newEmail }
    fun onPasswordChange(newPassword: String) { password = newPassword }

    fun login() {
        isLoading = true
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("Login", "Success")
                    auth.currentUser?.getIdToken(true)
                        ?.addOnSuccessListener { result ->
                            idToken = result.token ?: ""
                            Log.d("Token", "ID Token: $idToken")
                            RetrofitInstance.setToken(idToken)
                            // Call backend login
                            viewModelScope.launch {
                                try {
                                    val response = authRepository.verifyToken(VerifyTokenRequest(idToken))
                                    Log.d("BackendLogin", "Success: $response")
                                } catch (e: Exception) {
                                    Log.e("BackendLogin", "Failed: ${e.message}")
                                } finally {
                                    isLoading = false
                                }
                            }
                        }
                        ?.addOnFailureListener { e ->
                            Log.e("Token", "Gagal ambil token: ${e.message}")
                            isLoading = false
                        }
                } else {
                    Log.e("Login", "Failed: ${task.exception?.message}")
                    isLoading = false
                }
            }
    }

    fun firebaseAuthWithGoogleIdToken(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("GoogleAuth", "signInWithCredential:success")
                    auth.currentUser?.getIdToken(true)
                        ?.addOnSuccessListener { result ->
                            this.idToken = result.token ?: ""
                            Log.d("GoogleAuth", "ID Token: ${this.idToken}")
                            RetrofitInstance.setToken(this.idToken)
                            // You can also call your backend login logic here, if needed
                        }
                } else {
                    Log.e("GoogleAuth", "signInWithCredential:failure", task.exception)
                }
            }
    }

    // Function to build the One Tap sign-in request
    fun buildOneTapSignInRequest(webClientId: String): BeginSignInRequest {
        return BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true) // Wajib true untuk bisa sign-in ke Firebase
                    .setServerClientId(webClientId) // Web client ID Anda dari Firebase
                    .setFilterByAuthorizedAccounts(false) // Set false agar semua akun Google di perangkat ditampilkan
                    .build()
            )
            //tggl diubah true false, kalau true lgsg login otomatis
            .setAutoSelectEnabled(false) // Coba login otomatis jika memungkinkan
            .build()
    }

    fun signInWithGoogle(idToken: String?) {
        if (idToken == null) {
            Log.e("Auth", "ID Token is null")
            return
        }

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("Auth", "Firebase sign-in success")
                    auth.currentUser?.getIdToken(true)
                        ?.addOnSuccessListener { result ->
                            val token = result.token
                            if (token != null) {
                                Log.d("Auth", "Firebase ID Token: $token")
                                RetrofitInstance.setToken(token)

                                viewModelScope.launch {
                                    try {
                                        val response = authRepository.verifyToken(VerifyTokenRequest(token))
                                        Log.d("Auth", "Backend success: $response")
                                    } catch (e: Exception) {
                                        Log.e("Auth", "Backend error: ${e.message}")
                                    }
                                }
                            }
                        }
                } else {
                    Log.e("Auth", "Firebase sign-in failed: ${task.exception?.message}")
                }
            }
    }

    fun sendPasswordResetEmail(email: String) {
        if (email.isBlank()) {
            _resetPasswordState.value = Result.failure(Exception("Email cannot be empty"))
            return
        }

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _resetPasswordState.value = Result.success("Reset email sent to $email")
                } else {
                    val error = task.exception?.localizedMessage ?: "Unknown error"
                    _resetPasswordState.value = Result.failure(Exception(error))
                }
            }
    }

    fun clearResetPasswordState() {
        _resetPasswordState.value = null
    }

}
