package com.example.projectmdp.data.source.remote

import com.example.projectmdp.data.source.response.MidtransResponse
import retrofit2.http.*

interface TransactionApi {
    @POST("api/transaction/create")
    suspend fun createTransaction(@Body orderDetails: Map<String, Any>): MidtransResponse

    @GET("api/transaction/status/{orderId}")
    suspend fun checkTransactionStatus(@Path("orderId") orderId: String): MidtransResponse

    @GET("api/transaction/history")
    suspend fun getTransactionHistory(): MidtransResponse

    @GET("api/transaction/{id}")
    suspend fun getTransactionById(@Path("id") id: String): MidtransResponse

    @POST("api/transaction/cancel/{orderId}")
    suspend fun cancelTransaction(@Path("orderId") orderId: String): MidtransResponse
}
