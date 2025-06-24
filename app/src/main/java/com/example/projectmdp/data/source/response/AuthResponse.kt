package com.example.projectmdp.data.source.response

import com.example.projectmdp.data.model.auth.LoginDto

data class AuthResponse(
    val status: Int,
    val message: String,
    val token: String,
)

data class VerifyResponse(
    val status: Int,
    val message: String,
    val user: LoginDto
)