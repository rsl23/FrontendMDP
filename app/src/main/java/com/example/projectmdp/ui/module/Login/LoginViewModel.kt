package com.example.projectmdp.ui.module.Login
import android.util.Log
import androidx.lifecycle.ViewModel

import androidx.compose.runtime.*
import androidx.lifecycle.viewModelScope
import com.example.projectmdp.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

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
        viewModelScope.launch {
            try {
                val response = repository.login(email, password)
                Log.d("LoginViewModel", "Login response: $response")
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Login error: ${e.message}", e)
            }
        }
        isLoading = false
    }
}
