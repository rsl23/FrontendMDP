package com.example.projectmdp.ui.module.register

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor() : ViewModel() {
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
                isLoading = false
                if (task.isSuccessful) {
                    Log.d("Register", "Success")
                } else {
                    Log.e("Register", "Failed: ${task.exception?.message}")
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