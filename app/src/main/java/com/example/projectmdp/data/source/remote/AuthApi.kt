package com.example.projectmdp.data.source.remote

import com.example.projectmdp.data.model.auth.RegisterDto
import com.example.projectmdp.data.model.request.LoginRequest
import com.example.projectmdp.data.source.response.AuthResponse
import com.example.projectmdp.data.source.response.VerifyResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Header

interface AuthApi {
    @POST("/login")
    suspend fun login( @Body request: LoginRequest): AuthResponse
    @POST("/signup")
    suspend fun register(@Body registerDto: RegisterDto): String
    @GET("/logout")
    suspend fun logout(@Header("Authorization") authHeader: String)
    @POST("/verify-token")
    suspend fun verifyToken(@Body token: String): VerifyResponse
}