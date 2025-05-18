package com.example.projectmdp.data.repository

import com.example.projectmdp.data.model.auth.RegisterDto
import com.example.projectmdp.data.source.remote.RetrofitInstance

class AuthRepository {
    suspend fun login(email: String, password: String): String {
        return RetrofitInstance.api.login(email, password)
    }
    suspend fun register(registerDto: RegisterDto): String {
        return RetrofitInstance.api.register(registerDto)
    }
}