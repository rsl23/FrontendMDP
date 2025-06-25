package com.example.projectmdp.data.source.response

import android.net.Uri
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


data class ApiResponse<T>(
    val status: Int,
    val message: String,
    val data: T?
)

// Product Data Class
data class Product(
    val product_id: String,
    val name: String,
    val description: String,
    val price: Double,
    val category: String,
    val image: String?,
    val user_id: String,
    val created_at: String,
    val deleted_at: String?
)

// Response Data Classes
data class AddProductData(
    val product: Product
)

data class GetAllProductsData(
    val products: List<Product>,
    val pagination: PaginationInfo
)

data class PaginationInfo(
    val currentPage: Int,
    val totalPages: Int,
    val totalProducts: Int,
    val hasNext: Boolean,
    val hasPrev: Boolean,
    val limit: Int
)

data class GetProductData(
    val product: Product
)

data class UpdateProductData(
    val product: Product
)

// Delete product tidak return data, hanya success message
data class DeleteProductData(
    val success: Boolean = true
)

data class SearchProductData(
    val product: List<Product>  // Backend return array untuk search
)

// Request DTO untuk form data
data class ProductRequest(
    val name: String,
    val description: String,
    val price: Double,
    val category: String,
    val imageUri: Uri? = null  // Local image URI
)