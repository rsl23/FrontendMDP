package com.example.projectmdp.data.model.auth

data class LoginDto(
    val id: String,
    val email: String,
    val username: String,
    val address: String,
    val phone_number: String,
    val role: String,
    val firebase_uid: String,
    val profile_picture: String,
    val auth_provider: String,
    val created_at: String,
    val deleted_at: String? // <- HARUS nullable
)