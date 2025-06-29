package com.example.projectmdp.data.source.remote

import com.example.projectmdp.data.source.response.ApiResponse
import com.example.projectmdp.data.source.response.CreateTransactionData
import com.example.projectmdp.data.source.response.GetMyTransactionsData
import com.example.projectmdp.data.source.response.GetTransactionByIdData
import com.example.projectmdp.data.source.response.UpdateTransactionStatusData
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface TransactionApi {
    @POST("/create-transaction")
    suspend fun createTransaction(
        @Body request: CreateTransactionRequest
    ): ApiResponse<CreateTransactionData>

    @GET("/my-transactions")
    suspend fun getMyTransactions(
    ): ApiResponse<GetMyTransactionsData>

    @GET("/transaction/{id}")
    suspend fun getTransactionById(
        @Path("id") id: String
    ): ApiResponse<GetTransactionByIdData>

    @PUT("/transaction/{id}/status")
    suspend fun updateTransactionStatus(
        @Path("id") id: String,
        @Body request: UpdateTransactionStatusRequest
    ): ApiResponse<UpdateTransactionStatusData>
}

data class UpdateTransactionStatusRequest(
    val payment_status: String,  // "pending", "completed", "refunded", "cancelled"
    val payment_description: String = ""
)
data class CreateTransactionRequest(
    val product_id: String,
    val quantity: Int,
    val total_price: Double,
)