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
    @ColumnInfo(name = "last_updated")
    val lastUpdated: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "is_synced")
    val isSynced: Boolean = true
)