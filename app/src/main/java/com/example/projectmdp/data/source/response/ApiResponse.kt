package com.example.projectmdp.data.source.response

data class ApiResponse<T>(
    val status: Int,
    val message: String,
    val data: T? = null,
    val error: String? = null
)

// Extension functions untuk handling response
fun <T> ApiResponse<T>.isSuccess(): Boolean = status == 200 || status == 201
fun <T> ApiResponse<T>.isError(): Boolean = status == 400 || status == 500 || status == 401 || status == 404 || status == 405
