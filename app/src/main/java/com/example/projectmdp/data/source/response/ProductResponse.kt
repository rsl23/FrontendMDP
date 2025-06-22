package com.example.projectmdp.data.source.response

import com.example.projectmdp.data.model.product.ProductDto

data class ProductResponse(
    val status: Int,
    val message: String,
)
data class GetAllProduct(
    val status : Int,
    val message: String,
    val products : List<ProductDto>
)

data class GetProduct(
    val status: Int,
    val message: String,
    val product: ProductDto
)
