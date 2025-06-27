package com.example.projectmdp.data.source.response

import com.google.gson.annotations.SerializedName

data class MidtransResponse(
    val status: String,
    val message: String,
    val error: String? = null,
    val data: MidtransData? = null
) {
    fun isSuccess(): Boolean {
        return status == "success"
    }
}

data class MidtransData(
    val token: String?,
    val redirect_url: String?,
    val order_id: String? = null,
    val transaction_status: String? = null,
    val payment_type: String? = null,
    val transaction_time: String? = null,
    val gross_amount: String? = null,
    val currency: String? = null,
    val expiry_time: String? = null,
    val fraud_status: String? = null
)
