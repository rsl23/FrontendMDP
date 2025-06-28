package com.example.projectmdp.data.model.product

import com.google.firebase.Timestamp
import java.util.Date

data class Product(
    val product_id: String = "",
    val name: String = "",
    val price: String = "",
    val description: String = "",
    val image: String = "",
    val user: String = "",
    val created_at: Timestamp = Timestamp.now(),
    val updated_at: Timestamp = Timestamp.now(),
    val deleted_at: Timestamp? = null,

    // Additional fields for UI display (derived from user/seller data)
//    val sellerName: String = "",
//    val sellerLocation: String = ""
)