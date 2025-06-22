package com.example.projectmdp.ui.module.login

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectmdp.data.model.auth.RegisterDto
import com.example.projectmdp.data.repository.AuthRepository
import com.example.projectmdp.data.source.remote.RetrofitInstance
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.google.android.gms.auth.api.identity.BeginSignInRequest

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
                                    val response = authRepository.login(email, password)
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
                    .setSupported(true)
                    .setServerClientId(webClientId)
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }
}
