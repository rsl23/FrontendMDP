package com.example.projectmdp.ui.module.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController


@Composable
fun LoginScreen(viewModel: LoginViewModel = viewModel(),modifier: Modifier = Modifier, navController: NavController) {
    val email = viewModel.email
    val password = viewModel.password
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Login", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = email,
            onValueChange = {viewModel.onEmailChange(it)},
            label = { Text("Email") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = password,
            onValueChange = { viewModel.onPasswordChange(it) },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        Row {
            Text("Don't have an account? ")
            Text("Register here", modifier = Modifier.clickable {
                navController.navigate("register")
            })
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {viewModel.login()}) {
            Text("Login")
        }
        Button(onClick = {viewModel.loginWithGoogle()}){
            Text("Login with Google")
        }
    }
}
