package com.example.projectmdp.ui.module.register

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectmdp.data.model.auth.RegisterDto
import com.example.projectmdp.data.repository.AuthRepository
import com.example.projectmdp.data.source.remote.VerifyTokenRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    var email by mutableStateOf("")
//        private set
    var password by mutableStateOf("")
//        private set
    var confirmPassword by mutableStateOf("")
//        private set
    var address by mutableStateOf("")
//        private set
    var phoneNumber by mutableStateOf("")
//        private set
    var isLoading by mutableStateOf(false)
//        private set

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _googleSignInEvent = MutableSharedFlow<Unit>()
    val googleSignInEvent = _googleSignInEvent.asSharedFlow()

    fun onEmailChange(newEmail: String) { email = newEmail }
    fun onPasswordChange(newPassword: String) { password = newPassword }
    fun onConfirmPasswordChange(newConfirmPassword: String) { confirmPassword = newConfirmPassword }
    fun onAddressChange(newAddress: String) { address = newAddress }
    fun onPhoneNumberChange(newPhoneNumber: String) { phoneNumber = newPhoneNumber }

    fun register() {
        if (password != confirmPassword) {
            Log.e("Register", "Passwords do not match")
            return
        }

        isLoading = true
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("Register", "Firebase auth success")

                    // Ambil ID Token dari Firebase
                    auth.currentUser?.getIdToken(true)
                        ?.addOnSuccessListener { result ->
                            val idToken = result.token
                            Log.d("Register", "ID Token: $idToken")

                            if (idToken != null) {
                                viewModelScope.launch {
                                    try {
                                        // Kirim ID Token ke backend sesuai endpoint verifyFirebaseToken
                                        val response = authRepository.verifyToken(VerifyTokenRequest(idToken))
                                        Log.d("BackendRegister", "Success: $response")
                                    } catch (e: Exception) {
                                        Log.e("BackendRegister", "Failed: ${e.message}")
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            } else {
                                Log.e("Register", "Failed to retrieve ID token")
                                isLoading = false
                            }
                        }
                        ?.addOnFailureListener { e ->
                            Log.e("Register", "Get ID token failed: ${e.message}")
                            isLoading = false
                        }
                } else {
                    Log.e("Register", "Firebase auth failed: ${task.exception?.message}")
                    isLoading = false
                }
            }
    }

    fun onGoogleSignInClicked() {
        viewModelScope.launch {
            _googleSignInEvent.emit(Unit)
        }
    }

    //Fungsi ini akan dipanggil oleh UI setelah mendapatkan idToken
    fun onGoogleSignInResult(idToken: String?) {
        if (idToken == null) {
            Log.e("GoogleRegister", "Google Sign-In failed or idToken is null")
            // Mungkin tampilkan pesan error di UI melalui state lain
            return
        }
        firebaseAuthWithGoogle(idToken)
    }

    fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("GoogleRegister", "Success")
                    auth.currentUser?.getIdToken(true)
                        ?.addOnSuccessListener { result ->
                            val idToken = result.token
                            Log.d("Register", "ID Token: $idToken")

                            if (idToken != null) {
                                viewModelScope.launch {
                                    try {
                                        //  Kirim ID Token ke backend sesuai endpoint verifyFirebaseToken
                                        val response = authRepository.verifyToken(VerifyTokenRequest(idToken))
                                        Log.d("Register With Google", "Success: $response")
                                    } catch (e: Exception) {
                                        Log.e("Register With Google", "Failed: ${e.message}")
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            } else {
                                Log.e("Register", "Failed to retrieve ID token")
                                isLoading = false
                            }
                        }
                        ?.addOnFailureListener { e ->
                            Log.e("Register", "Get ID token failed: ${e.message}")
                            isLoading = false
                        }

                } else {
                    Log.e("GoogleRegister", "Failed: ${task.exception?.message}")
                }
            }
    }
}

