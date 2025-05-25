package com.example.projectmdp.ui.module.register

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
    navController: NavController
){
    val email = viewModel.email
    val password = viewModel.password
    val confirmPassword = viewModel.confirmPassword
    val address = viewModel.address
    val phoneNumber = viewModel.phoneNumber
    Column(
        Modifier.fillMaxSize()
    ){
        Text("Register New User")
        TextField(
            value = email,
            onValueChange = {viewModel.onEmailChange(it)},
            label = { Text("Email") }
        )
        TextField(
            value = password,
            onValueChange = {viewModel.onPasswordChange(it)},
            label = { Text("Password") }
        )
        TextField(
            value = confirmPassword,
            onValueChange = {viewModel.onConfirmPasswordChange(it)},
            label = { Text("Confirm Password") }
        )
        TextField(
            value = address,
            onValueChange = {viewModel.onAddressChange(it)},
            label = { Text("Address") }
        )
        TextField(
            value = phoneNumber,
            onValueChange = {viewModel.onPhoneNumberChange(it)},
            label = { Text("Phone Number") }
        )
        Row {
            Text("Already have an account? ")
            Text("Login here", modifier = Modifier.clickable {
                navController.navigate("login")
            }
            )
        }
        Button(onClick = {viewModel.register()}) {
            Text("Register")
        }
        Button(onClick = {viewModel.registerWithGoogle()}) {
            Text("Register with Google")
        }
    }
}
