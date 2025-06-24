package com.example.projectmdp.data.source.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val name: String,
    val price: String? = null,
    val description: String,
    val category: String? = null,
    val image: String,
    val user_id: String,
    val created_at: Long = Date().time,
    val updated_at: Long = Date().time,
    val deleted_at: Long? = null
)