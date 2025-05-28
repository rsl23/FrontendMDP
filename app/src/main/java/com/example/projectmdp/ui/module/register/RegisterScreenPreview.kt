package com.example.projectmdp.ui.module.register

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController

// Create a modified version of the RegisterScreen that accepts our preview ViewModel
@Composable
fun PreviewRegisterScreen(
    viewModel: PreviewRegisterViewModel,
    navController: androidx.navigation.NavController
) {
    // This is essentially the same as RegisterScreen but using our PreviewRegisterViewModel
    RegisterScreenContent(
        email = viewModel.email,
        password = viewModel.password,
        confirmPassword = viewModel.confirmPassword,
        address = viewModel.address,
        phoneNumber = viewModel.phoneNumber,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
        onAddressChange = viewModel::onAddressChange,
        onPhoneNumberChange = viewModel::onPhoneNumberChange,
        onRegister = viewModel::register,
        onGoogleSignIn = viewModel::firebaseAuthWithGoogle,
        navController = navController
    )
}

// Preview ViewModel implementation remains the same
class PreviewRegisterViewModel : androidx.lifecycle.ViewModel() {
    var email = ""
    var password = ""
    var confirmPassword = ""
    var address = ""
    var phoneNumber = ""
    var isLoading = false

    fun onEmailChange(newEmail: String) { email = newEmail }
    fun onPasswordChange(newPassword: String) { password = newPassword }
    fun onConfirmPasswordChange(newConfirmPassword: String) { confirmPassword = newConfirmPassword }
    fun onAddressChange(newAddress: String) { address = newAddress }
    fun onPhoneNumberChange(newPhoneNumber: String) { phoneNumber = newPhoneNumber }
    fun register() { /* No-op for preview */ }
    fun firebaseAuthWithGoogle(idToken: String) { /* No-op for preview */ }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    val previewViewModel = PreviewRegisterViewModel()

    MaterialTheme {
        PreviewRegisterScreen(
            viewModel = previewViewModel,
            navController = rememberNavController()
        )
    }
}