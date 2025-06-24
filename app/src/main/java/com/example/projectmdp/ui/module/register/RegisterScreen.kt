package com.example.projectmdp.ui.module.register

import android.app.Activity
import android.content.IntentSender
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val email = viewModel.email
    val password = viewModel.password
    val confirmPassword = viewModel.confirmPassword
    val address = viewModel.address
    val phoneNumber = viewModel.phoneNumber
    val isLoading = viewModel.isLoading

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Create Account",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Sign up to get started",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { viewModel.onEmailChange(it) },
                label = { Text("Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email"
                    )
                },
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = password,
                onValueChange = { viewModel.onPasswordChange(it) },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Password"
                    )
                },
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { viewModel.onConfirmPasswordChange(it) },
                label = { Text("Confirm Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Confirm Password"
                    )
                },
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = address,
                onValueChange = { viewModel.onAddressChange(it) },
                label = { Text("Address") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Address"
                    )
                },
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { viewModel.onPhoneNumberChange(it) },
                label = { Text("Phone Number") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "Phone Number"
                    )
                },
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Button(
                onClick = { viewModel.register() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        "Register",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val context = LocalContext.current
            val activity = context as Activity
            val webClientId = "114051112134470433010"

            val oneTapClient = remember { Identity.getSignInClient(context) }
            val launcher = rememberLauncherForActivityResult(
                ActivityResultContracts.StartIntentSenderForResult()
            ) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
                    val idToken = credential.googleIdToken
                    if (idToken != null) {
                        // Call the existing firebaseAuthWithGoogle method
                        viewModel.firebaseAuthWithGoogle(idToken)

                        // Extract user info for registration
                        val email = credential.id
                        // If address and phone are required, show a dialog to collect them
                        viewModel.onEmailChange(email)
                    } else {
                        Log.e("OneTap", "No ID token!")
                    }
                } else {
                    Log.e("OneTap", "Sign-in failed or canceled")
                }
            }

            OutlinedButton(
                onClick = {
                    val signInRequest = BeginSignInRequest.builder()
                        .setGoogleIdTokenRequestOptions(
                            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                                .setSupported(true)
                                .setServerClientId(webClientId)
                                .setFilterByAuthorizedAccounts(false)
                                .build()
                        )
                        .setAutoSelectEnabled(true)
                        .build()

                    oneTapClient.beginSignIn(signInRequest)
                        .addOnSuccessListener { result ->
                            try {
                                val intentSenderRequest = IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                                launcher.launch(intentSenderRequest)
                            } catch (e: IntentSender.SendIntentException) {
                                Log.e("OneTap", "Couldn't start One Tap UI: ${e.localizedMessage}")
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("OneTap", "One Tap Sign-in failed: ${e.localizedMessage}")
                        }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    "Register with Google",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Row(
                modifier = Modifier.padding(vertical = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Already have an account? ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    "Login here",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable {
                        navController.navigate("login")
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

private fun RegisterViewModel.registerWithGoogle() {
    Log.d("GoogleRegister", "Google registration initiated")
}
