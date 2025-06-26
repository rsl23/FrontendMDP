package com.example.projectmdp.data.repository

import com.example.projectmdp.data.source.remote.RetrofitInstance
import com.example.projectmdp.data.source.remote.VerifyTokenRequest
import javax.inject.Inject

open class AuthRepository @Inject constructor() {
//    open suspend fun login(email: String, password: String): AuthResponse {
//        return RetrofitInstance.Authapi.login(LoginRequest(email,password))
//    }
//    open suspend fun register(registerDto: RegisterDto): String {
//        return RetrofitInstance.Authapi.register(registerDto)
//    }

    open suspend fun verifyToken(token: VerifyTokenRequest){
        RetrofitInstance.Authapi.verifyToken(token)
    }

    open suspend fun profileUser(){}
}