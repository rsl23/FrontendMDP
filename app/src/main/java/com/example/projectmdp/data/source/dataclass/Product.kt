package com.example.projectmdp.data.source.dataclass

import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val id: String,
    var description: String,
    var image: String,
    var name: String,
    var user_id: String,
    var created_at: String,
)
