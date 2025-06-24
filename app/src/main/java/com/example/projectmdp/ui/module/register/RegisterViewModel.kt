package com.example.projectmdp.ui.module.register

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

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    var email by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set
    var confirmPassword by mutableStateOf("")
        private set
    var address by mutableStateOf("")
        private set
    var phoneNumber by mutableStateOf("")
        private set
    var isLoading by mutableStateOf(false)
        private set

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

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
                    Log.d("Register", "Success")
                    // Call backend register
                    viewModelScope.launch {
                        try {
                            val registerDto = RegisterDto(
                                username = email.substringBefore("@"),
                                email = email,
                                password = password,
                                address = address,
                                phone_number = phoneNumber,
                                role = "user"
                            )
                            val response = authRepository.register(registerDto)
                            Log.d("BackendRegister", "Success: $response")
                        } catch (e: Exception) {
                            Log.e("BackendRegister", "Failed: ${e.message}")
                        } finally {
                            isLoading = false
                        }
                    }
                } else {
                    Log.e("Register", "Failed: ${task.exception?.message}")
                    isLoading = false
                }
            }
    }

    // Enhanced firebaseAuthWithGoogle function for RegisterViewModel.kt
    fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        isLoading = true

        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("GoogleRegister", "signInWithCredential:success")
                    // Get the user from Firebase
                    val user = auth.currentUser

                    if (user != null) {
                        // Extract email for registration form
                        email = user.email ?: ""

                        // Get the Firebase token for backend authentication
                        user.getIdToken(true)
                            .addOnSuccessListener { result ->
                                val firebaseToken = result.token ?: ""
                                RetrofitInstance.setToken(firebaseToken)

                                // Verify token with backend
                                viewModelScope.launch {
                                    try {
                                        // Your backend should handle verification through the verify-token endpoint
                                        // and create user if needed

                                        // If additional fields are required (address, phone), we can update them
                                        if (address.isNotEmpty() || phoneNumber.isNotEmpty()) {
                                            // Here you could make another API call to update user profile
                                            // with address and phone information
                                            Log.d("GoogleRegister", "Additional info available for backend")
                                        }

                                        Log.d("GoogleRegister", "Backend authentication complete")
                                    } catch (e: Exception) {
                                        Log.e("GoogleRegister", "Backend auth failed: ${e.message}")
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.e("GoogleRegister", "Failed to get token: ${e.message}")
                                isLoading = false
                            }
                    } else {
                        Log.e("GoogleRegister", "User is null after successful authentication")
                        isLoading = false
                    }
                } else {
                    Log.e("GoogleRegister", "Failed: ${task.exception?.message}")
                    isLoading = false
                }
            }
    }
}

