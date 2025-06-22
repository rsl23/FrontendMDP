package com.example.projectmdp.data.repository

import com.example.projectmdp.data.model.auth.RegisterDto
import com.example.projectmdp.data.model.request.LoginRequest
import com.example.projectmdp.data.source.remote.RetrofitInstance
import com.example.projectmdp.data.source.response.AuthResponse
import javax.inject.Inject

open class AuthRepository @Inject constructor() {
    open suspend fun login(email: String, password: String): AuthResponse {
        return RetrofitInstance.Authapi.login(LoginRequest(email,password))
    }
    open suspend fun register(registerDto: RegisterDto): String {
        return RetrofitInstance.Authapi.register(registerDto)
    }

    open suspend fun profileUser(){}
}