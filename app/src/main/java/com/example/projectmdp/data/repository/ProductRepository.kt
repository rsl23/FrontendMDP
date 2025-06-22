package com.example.projectmdp.data.repository

import javax.inject.Inject
import com.example.projectmdp.data.source.remote.RetrofitInstance
import com.example.projectmdp.data.model.product.ProductDto

class ProductRepository @Inject constructor() {
    open suspend fun getAllProducts(): List<ProductDto> {
        val response = RetrofitInstance.Productapi.getAllProduct()
        return response.products
    }

    open suspend fun createProduct(product: ProductDto): String {
        val response = RetrofitInstance.Productapi.addProduct(product)
        return response.message
    }

    open suspend fun getProductByName(name: String): ProductDto {
        val response = RetrofitInstance.Productapi.getProductByName(name)
        return response.product
    }
}