package com.example.projectmdp.ui.module.login

import android.util.Log
import androidx.lifecycle.ViewModel

import androidx.compose.runtime.*

class LoginViewModel : ViewModel() {

    var email by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var isLoading by mutableStateOf(false)
        private set
    fun onEmailChange(newEmail: String) {
        email = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        password = newPassword
    }

    fun login() {
        isLoading = true
        Log.d("LoginViewModel", "Email: $email, Password: $password")
    }
}
