package com.example.projectmdp.data.source.response

data class Transaction(
    val transaction_id: String,
    val user_seller: UserSeller,
    val email_buyer: String,
    val product: ProductInfo,
    val quantity: Int,
    val total_price: Double,
    val datetime: String,
    val payment_id: String?,
    val payment_status: String, // "pending", "completed", "cancelled", "refunded"
    val payment_description: String?,
    val user_role: String? = null // "buyer" or "seller" - hanya ada di response getTransactionById
)

data class UserSeller(
    val id: String,
    val name: String,
    val email: String,
    val phone: String?,
    val profile_picture: String?
)

data class ProductInfo(
    val product_id: String,
    val name: String,
    val description: String,
    val price: Double,
    val category: String,
    val image_url: String?
)

// Response DTOs
data class CreateTransactionResponse(
    val status: String,
    val message: String,
    val data: CreateTransactionData
)

data class CreateTransactionData(
    val transaction: Transaction
)

data class GetMyTransactionsResponse(
    val status: String,
    val message: String,
    val data: GetMyTransactionsData
)

data class GetMyTransactionsData(
    val transactions: List<Transaction>,
    val count: Int,
    val filter: String // "buyer", "seller", "both"
)

data class GetTransactionByIdResponse(
    val status: String,
    val message: String,
    val data: GetTransactionByIdData
)

data class GetTransactionByIdData(
    val transaction: Transaction
)

data class UpdateTransactionStatusResponse(
    val status: String,
    val message: String,
    val data: UpdateTransactionStatusData
)

data class UpdateTransactionStatusData(
    val transaction: Transaction
)