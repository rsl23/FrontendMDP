package com.example.projectmdp.ui.module.EditProfile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.projectmdp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    viewModel: EditProfileViewModel = hiltViewModel(),
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()
    var showPasswordDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    text = "Edit Profile",
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Picture Section
            ProfilePictureSection(
                profilePictureUrl = uiState.profilePictureUrl,
                onEditProfilePicture = { viewModel.updateProfilePicture() }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Form Fields
            EditProfileForm(
                uiState = uiState,
                onUsernameChange = viewModel::updateUsername,
                onAddressChange = viewModel::updateAddress,
                onPhoneNumberChange = viewModel::updatePhoneNumber,
                onPasswordChangeClick = { showPasswordDialog = true }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Save Button
            Button(
                onClick = { viewModel.saveProfile() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        text = "Save Changes",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    // Password Change Dialog
    if (showPasswordDialog) {
        PasswordChangeDialog(
            onDismiss = { showPasswordDialog = false },
            onPasswordChange = { oldPassword, newPassword ->
                viewModel.changePassword(oldPassword, newPassword)
                showPasswordDialog = false
            }
        )
    }

    // Handle UI effects
    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            navController.popBackStack()
        }
    }

    // Show error messages
    uiState.errorMessage?.let { _ ->
        // You can show a snackbar or toast here
    }
}

@Composable
private fun ProfilePictureSection(
    profilePictureUrl: String?,
    onEditProfilePicture: () -> Unit
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier.size(120.dp),
        contentAlignment = Alignment.Center
    ) {
        if (profilePictureUrl.isNullOrEmpty()) {
            // Default profile picture
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile Picture",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(60.dp)
                )
            }
        } else {
            // User's profile picture
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(profilePictureUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Profile Picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape),
                error = painterResource(id = R.drawable.alert_error),
                placeholder = painterResource(id = R.drawable.landscape_placeholder)
            )
        }

        // Edit button
        FloatingActionButton(
            onClick = onEditProfilePicture,
            modifier = Modifier.align(Alignment.BottomEnd),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit Profile Picture",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun EditProfileForm(
    uiState: EditProfileUiState,
    onUsernameChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onPhoneNumberChange: (String) -> Unit,
    onPasswordChangeClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Username
        OutlinedTextField(
            value = uiState.username,
            onValueChange = onUsernameChange,
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        // Address
        OutlinedTextField(
            value = uiState.address,
            onValueChange = onAddressChange,
            label = { Text("Address") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            minLines = 2,
            maxLines = 3
        )

        // Phone Number
        OutlinedTextField(
            value = uiState.phoneNumber,
            onValueChange = onPhoneNumberChange,
            label = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        // Password Change Button
        OutlinedButton(
            onClick = onPasswordChangeClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Change Password")
        }
    }
}

@Composable
private fun PasswordChangeDialog(
    onDismiss: () -> Unit,
    onPasswordChange: (String, String) -> Unit
) {
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showOldPassword by remember { mutableStateOf(false) }
    var showNewPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Change Password") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Old Password
                OutlinedTextField(
                    value = oldPassword,
                    onValueChange = { oldPassword = it },
                    label = { Text("Current Password") },
                    visualTransformation = if (showOldPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        TextButton(onClick = { showOldPassword = !showOldPassword }) {
                            Text(if (showOldPassword) "Hide" else "Show")
                        }
                    },
                    singleLine = true
                )

                // New Password
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("New Password") },
                    visualTransformation = if (showNewPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        TextButton(onClick = { showNewPassword = !showNewPassword }) {
                            Text(if (showNewPassword) "Hide" else "Show")
                        }
                    },
                    singleLine = true
                )

                // Confirm Password
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm New Password") },
                    visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        TextButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                            Text(if (showConfirmPassword) "Hide" else "Show")
                        }
                    },
                    singleLine = true,
                    isError = newPassword.isNotEmpty() && confirmPassword.isNotEmpty() && newPassword != confirmPassword
                )

                if (newPassword.isNotEmpty() && confirmPassword.isNotEmpty() && newPassword != confirmPassword) {
                    Text(
                        text = "Passwords do not match",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (newPassword == confirmPassword && oldPassword.isNotEmpty() && newPassword.isNotEmpty()) {
                        onPasswordChange(oldPassword, newPassword)
                    }
                },
                enabled = newPassword == confirmPassword && oldPassword.isNotEmpty() && newPassword.isNotEmpty()
            ) {
                Text("Change")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
