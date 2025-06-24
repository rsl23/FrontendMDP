package com.example.projectmdp.ui.module.register

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectmdp.data.model.auth.RegisterDto
import com.example.projectmdp.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    internal var email by mutableStateOf("")
    internal var password by mutableStateOf("")
    internal var confirmPassword by mutableStateOf("")
    internal var address by mutableStateOf("")
    internal var phoneNumber by mutableStateOf("")
    internal var isLoading by mutableStateOf(false)
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

    fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("GoogleRegister", "Success")
                } else {
                    Log.e("GoogleRegister", "Failed: ${task.exception?.message}")
                }
            }
    }
}

