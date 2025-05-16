package com.example.projectmdp.data.source.remote

interface AuthRemoteDataSource {
    suspend fun login(email: String, password: String): String

}