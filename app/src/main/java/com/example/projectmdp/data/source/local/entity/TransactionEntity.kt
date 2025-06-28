package com.example.projectmdp.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey
    val transaction_id: String,
    val user_seller_id: String,        // Hanya simpan ID seller
    val email_buyer: String,           // Email buyer
    val product_id: String,            // Hanya simpan ID product
    val quantity: Int,                 // Tambah quantity yang hilang
    val total_price: Double,
    val datetime: String,
    val payment_id: String?,
    val payment_status: String,        // "pending", "completed", "cancelled", "refunded"
    val payment_description: String?,

    // === Tambahan Midtrans Fields ===
    @ColumnInfo(name = "midtrans_order_id")
    val midtrans_order_id: String? = null,
    @ColumnInfo(name = "snap_token")
    val snap_token: String? = null,
    @ColumnInfo(name = "redirect_url")
    val redirect_url: String? = null,
    @ColumnInfo(name = "payment_type")
    val payment_type: String? = null,
    @ColumnInfo(name = "va_number")
    val va_number: String? = null,
    @ColumnInfo(name = "pdf_url")
    val pdf_url: String? = null,
    @ColumnInfo(name = "settlement_time")
    val settlement_time: String? = null,
    @ColumnInfo(name = "expiry_time")
    val expiry_time: String? = null,

    @ColumnInfo(name = "last_updated")
    val lastUpdated: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "is_synced")
    val isSynced: Boolean = true
)