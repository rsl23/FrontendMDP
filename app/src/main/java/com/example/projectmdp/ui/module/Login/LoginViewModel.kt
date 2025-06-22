package com.example.projectmdp.ui.module.login

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.example.projectmdp.data.source.remote.RetrofitInstance
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {
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
                isLoading = false
                if (task.isSuccessful) {
                    Log.d("Login", "Success")
                    auth.currentUser?.getIdToken(true)
                        ?.addOnSuccessListener { result ->
                            idToken = result.token ?: "" //Ini token nya ambil disini
                            Log.d("Token", "ID Token: $idToken")

                            RetrofitInstance.setToken(idToken)
                        }
                        ?.addOnFailureListener { e ->
                            Log.e("Token", "Gagal ambil token: ${e.message}")
                        }
                } else {
                    Log.e("Login", "Failed: ${task.exception?.message}")
                }
            }
    }

    fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("GoogleLogin", "Success")
                } else {
                    Log.e("GoogleLogin", "Failed: ${task.exception?.message}")
                }
            }
    }
}