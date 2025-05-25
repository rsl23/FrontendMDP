package com.example.projectmdp.ui.module.login

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel

import androidx.compose.runtime.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel

open class LoginViewModel : ViewModel() {

    private lateinit var auth: FirebaseAuth

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

    open fun login() {
        isLoading = true
        auth = Firebase.auth
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                isLoading = false
                if (task.isSuccessful) {
                    //Kalau Login berhasil mau kemana
                } else {
                    //Kalau gagal
                    Log.e("Login Failed", "Login Failed");
                }
            }
    }
    open fun loginWithGoogle() {

    }
}
