package com.example.projectmdp.data.model.product

import com.squareup.moshi.Json
import java.io.Serializable


data class ProductDto(
    val name: String,
    val description: String,
    val price: Double,
    val category: String,
    val image: String,
    val userId: String
) : Serializable