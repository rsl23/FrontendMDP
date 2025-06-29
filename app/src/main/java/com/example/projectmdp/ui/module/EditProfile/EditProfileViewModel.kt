package com.example.projectmdp.ui.module.EditProfile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class EditProfileUiState(
    val username: String = "",
    val address: String = "",
    val phoneNumber: String = "",
    val profilePictureUrl: String? = null,
    val isLoading: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class EditProfileViewModel @Inject constructor() : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            viewModelScope.launch {
                try {
                    _uiState.value = _uiState.value.copy(isLoading = true)

                    val userDoc = firestore.collection("users")
                        .document(currentUser.uid)
                        .get()
                        .await()

                    if (userDoc.exists()) {
                        val userData = userDoc.data
                        _uiState.value = _uiState.value.copy(
                            username = userData?.get("username") as? String ?: "",
                            address = userData?.get("address") as? String ?: "",
                            phoneNumber = userData?.get("phone_number") as? String ?: "",
                            profilePictureUrl = userData?.get("profile_picture") as? String,
                            isLoading = false
                        )
                    } else {
                        // If user document doesn't exist, create it with default values
                        _uiState.value = _uiState.value.copy(
                            username = currentUser.displayName ?: "",
                            isLoading = false
                        )
                    }
                } catch (e: Exception) {
                    Log.e("EditProfileViewModel", "Error loading user profile", e)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Failed to load profile: ${e.message}"
                    )
                }
            }
        }
    }

    fun updateUsername(username: String) {
        _uiState.value = _uiState.value.copy(username = username)
    }

    fun updateAddress(address: String) {
        _uiState.value = _uiState.value.copy(address = address)
    }

    fun updatePhoneNumber(phoneNumber: String) {
        _uiState.value = _uiState.value.copy(phoneNumber = phoneNumber)
    }

    fun updateProfilePicture() {
        // This would typically open an image picker
        // For now, we'll just log the action
        Log.d("EditProfileViewModel", "Update profile picture clicked")
        // You can implement image picker functionality here
    }

    fun saveProfile() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            viewModelScope.launch {
                try {
                    _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

                    val userData = hashMapOf(
                        "username" to _uiState.value.username,
                        "address" to _uiState.value.address,
                        "phone_number" to _uiState.value.phoneNumber,
                        "profile_picture" to _uiState.value.profilePictureUrl,
                        "updated_at" to System.currentTimeMillis()
                    )

                    firestore.collection("users")
                        .document(currentUser.uid)
                        .set(userData)
                        .await()

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        saveSuccess = true
                    )

                    Log.d("EditProfileViewModel", "Profile saved successfully")
                } catch (e: Exception) {
                    Log.e("EditProfileViewModel", "Error saving profile", e)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Failed to save profile: ${e.message}"
                    )
                }
            }
        }
    }

    fun changePassword(oldPassword: String, newPassword: String) {
        val currentUser = auth.currentUser
        if (currentUser != null && currentUser.email != null) {
            viewModelScope.launch {
                try {
                    _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

                    // Re-authenticate user with old password
                    val credential = EmailAuthProvider.getCredential(currentUser.email!!, oldPassword)
                    currentUser.reauthenticate(credential).await()

                    // Update password
                    currentUser.updatePassword(newPassword).await()

                    _uiState.value = _uiState.value.copy(isLoading = false)
                    Log.d("EditProfileViewModel", "Password changed successfully")
                } catch (e: Exception) {
                    Log.e("EditProfileViewModel", "Error changing password", e)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Failed to change password: ${e.message}"
                    )
                }
            }
        }
    }
}
