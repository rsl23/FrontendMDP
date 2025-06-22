package com.example.projectmdp.ui.module.login

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController

// Create a modified version of the LoginScreen that accepts our preview ViewModel
@Composable
fun PreviewLoginScreen(
    viewModel: PreviewLoginViewModel,
    navController: androidx.navigation.NavController
) {
    // This is essentially the same as LoginScreen but using our PreviewLoginViewModel
//    LoginScreenContent(
//        email = viewModel.email,
//        password = viewModel.password,
//        onEmailChange = viewModel::onEmailChange,
//        onPasswordChange = viewModel::onPasswordChange,
//        onLogin = viewModel::login,
//        onGoogleSignIn = viewModel::firebaseAuthWithGoogle,
//        navController = navController
//    )
}

// Preview ViewModel implementation remains the same
class PreviewLoginViewModel : androidx.lifecycle.ViewModel() {
    var email = ""
    var password = ""
    var isLoading = false

    fun onEmailChange(newEmail: String) { email = newEmail }
    fun onPasswordChange(newPassword: String) { password = newPassword }
    fun login() { /* No-op for preview */ }
    fun firebaseAuthWithGoogle(idToken: String) { /* No-op for preview */ }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    val previewViewModel = PreviewLoginViewModel()

    MaterialTheme {
        PreviewLoginScreen(
            viewModel = previewViewModel,
            navController = rememberNavController()
        )
    }
}