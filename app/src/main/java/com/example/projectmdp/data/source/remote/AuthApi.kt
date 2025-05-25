package com.example.projectmdp.data.source.remote

import com.example.projectmdp.data.model.auth.RegisterDto
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET

interface AuthApi {
    @POST("auth/login")
    suspend fun login(email: String, password: String): String
    @POST("auth/signup")
    suspend fun register(@Body registerDto: RegisterDto): String
    @GET("auth/logout")
    suspend fun logout()
}