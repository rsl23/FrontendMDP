package com.example.projectmdp.data.source

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.projectmdp.data.source.dataclass.Transaction

object MidtransUtils {
    fun openPaymentPage(context: Context, redirectUrl: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(redirectUrl))
        context.startActivity(intent)
    }

    fun isTransactionExpired(transaction: Transaction): Boolean {
        return transaction.expiry_time?.let { expiryTime ->
            try {
                val expiry = expiryTime.toLong()
                System.currentTimeMillis() > expiry
            } catch (e: NumberFormatException) {
                false
            }
        } ?: false
    }

    fun getPaymentInstructions(transaction: Transaction): String {
        return when (transaction.payment_type) {
            "bank_transfer" -> "Transfer ke Virtual Account: ${transaction.va_number}"
            "credit_card" -> "Pembayaran dengan kartu kredit"
            "gopay" -> "Pembayaran dengan GoPay"
            "shopeepay" -> "Pembayaran dengan ShopeePay"
            else -> "Lihat detail pembayaran"
        }
    }
}