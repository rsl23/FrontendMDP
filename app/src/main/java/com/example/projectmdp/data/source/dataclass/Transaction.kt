package com.example.projectmdp.data.source.dataclass

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Transaction(
    val transaction_id: String,
    val seller: User,                       // Full User object
    val buyer_email: String,
    val product: Product,                   // Full Product object
    val quantity: Int,                      // Extracted from product in transaction
    val total_price: Double,                // Extracted from product in transaction
    val datetime: String,
    val payment_id: String?,
    val payment_status: String,
    val payment_description: String?,
    val user_role: String? = null,

    // Tambahan Midtrans Fields
    val midtrans_order_id: String? = null,
    val snap_token: String? = null,
    val redirect_url: String? = null,
    val payment_type: String? = null,
    val va_number: String? = null,
    val pdf_url: String? = null,
    val settlement_time: String? = null,
    val expiry_time: String? = null
): Parcelable {

    companion object {
        fun empty() = Transaction(
            transaction_id = "",
            seller = User.empty(),
            buyer_email = "",
            product = Product.empty(),
            quantity = 0,
            total_price = 0.0,
            datetime = "",
            payment_id = null,
            payment_status = "pending",
            payment_description = null,
            user_role = null,
            midtrans_order_id = null,
            snap_token = null,
            redirect_url = null,
            payment_type = null,
            va_number = null,
            pdf_url = null,
            settlement_time = null,
            expiry_time = null
        )
    }

    //helper untuk midtrans
    fun hasMidtransToken() = !snap_token.isNullOrEmpty()
    fun hasRedirectUrl() = !redirect_url.isNullOrEmpty()
    fun hasVirtualAccount() = !va_number.isNullOrEmpty()
    fun hasPdfUrl() = !pdf_url.isNullOrEmpty()
    fun isExpired() = expiry_time?.let {
        // Implementasi check expiry time
        System.currentTimeMillis() > it.toLongOrNull() ?: 0L
    } ?: false

    // Helper methods
    fun isPending() = payment_status == "pending"
    fun isCompleted() = payment_status == "completed"
    fun isCancelled() = payment_status == "cancelled"
    fun isRefunded() = payment_status == "refunded"

    fun isUserSeller(userId: String) = seller.id == userId
    fun isUserBuyer(userEmail: String) = buyer_email == userEmail
}