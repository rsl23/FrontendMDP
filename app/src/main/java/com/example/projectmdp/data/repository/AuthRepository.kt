package com.example.projectmdp.data.repository

import com.example.projectmdp.data.model.auth.RegisterDto
import com.example.projectmdp.data.source.remote.RetrofitInstance
import javax.inject.Inject

open class AuthRepository @Inject constructor() {
    open suspend fun login(email: String, password: String): String {
        return RetrofitInstance.api.login(email, password)
    }
    open suspend fun register(registerDto: RegisterDto): String {
        return RetrofitInstance.api.register(registerDto)
    }
}