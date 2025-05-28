package com.example.projectmdp.ui.module.register

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.projectmdp.navigation.Routes
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = hiltViewModel(),
    navController: NavController
) {
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

@Composable
fun RegisterScreenContent(
    email: String,
    password: String,
    confirmPassword: String,
    address: String,
    phoneNumber: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onPhoneNumberChange: (String) -> Unit,
    onRegister: () -> Unit,
    onGoogleSignIn: (String) -> Unit,
    navController: NavController
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        val account = task.result
        val idToken = account?.idToken
        if (idToken != null) {
            onGoogleSignIn(idToken)
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Register New User", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = address,
            onValueChange = onAddressChange,
            label = { Text("Address") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = phoneNumber,
            onValueChange = onPhoneNumberChange,
            label = { Text("Phone Number") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            Text("Already have an account? ")
            Text("Login here", modifier = Modifier.clickable {
                navController.navigate(Routes.LOGIN)
            })
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRegister) {
            Text("Register")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("YOUR_WEB_CLIENT_ID")
                .requestEmail()
                .build()
            val googleSignInClient = GoogleSignIn.getClient(context, gso)
            launcher.launch(googleSignInClient.signInIntent)
        }) {
            Text("Register with Google")
        }
    }
}