package com.example.projectmdp.data.repository

import android.net.Uri
import com.example.projectmdp.data.source.dataclass.Product
import com.example.projectmdp.data.source.local.dao.ProductDao
import com.example.projectmdp.data.source.remote.ProductApi
import com.example.projectmdp.data.source.remote.RetrofitInstance
import com.example.projectmdp.data.source.response.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

// Data class untuk pagination
data class ProductWithPagination(
    val products: List<Product>,
    val pagination: PaginationInfo
)

// Extension functions untuk mapping
fun com.example.projectmdp.data.source.response.Product.toProduct(): Product {
    return Product(
        product_id = this.product_id,
        name = this.name,
        price = this.price,
        description = this.description,
        category = this.category,
        image = this.image ?: "",
        user_id = this.user_id,
        created_at = this.created_at,
        deleted_at = this.deleted_at
    )
}

@Singleton
class ProductRepository @Inject constructor(
    private val productDao: ProductDao,
    private val productApi: ProductApi
) {

    // CRUD Operations with Remote + Local

    suspend fun getAllProducts(
        page: Int = 1,
        limit: Int = 10,
        forceRefresh: Boolean = false
    ): Flow<Result<ProductWithPagination>> = flow {
        try {
            // Emit cached data first for better UX
            if (!forceRefresh) {
                val cachedProducts = productDao.getAllProducts().map { Product.fromProductEntity(it) }
                if (cachedProducts.isNotEmpty()) {
                    val pagination = PaginationInfo(
                        currentPage = 1,
                        totalPages = 1,
                        totalProducts = cachedProducts.size,
                        hasNext = false,
                        hasPrev = false,
                        limit = cachedProducts.size
                    )
                    emit(Result.success(ProductWithPagination(cachedProducts, pagination)))
                }
            }

            // Fetch from remote
            val response = RetrofitInstance.Productapi.getAllProducts(page, limit)
            if (response.isSuccess()) {
                response.data?.let { responseData ->
                    val products = responseData.products.map { it.toProduct() }
                    
                    // Cache to local database
                    val productEntities = products.map { it.toProductEntity() }
                    if (page == 1) {
                        productDao.clearAllProducts() // Clear cache for first page
                    }
                    productDao.insertAll(productEntities)
                    
                    val productWithPagination = ProductWithPagination(products, responseData.pagination)
                    emit(Result.success(productWithPagination))
                } ?: emit(Result.failure(Exception("No data received")))
            } else {
                // If remote fails, return cached data if available
                val cachedProducts = productDao.getAllProducts().map { Product.fromProductEntity(it) }
                if (cachedProducts.isNotEmpty()) {
                    val pagination = PaginationInfo(1, 1, cachedProducts.size, false, false, cachedProducts.size)
                    emit(Result.success(ProductWithPagination(cachedProducts, pagination)))
                } else {
                    emit(Result.failure(Exception(response.error ?: "Failed to fetch products")))
                }
            }
        } catch (e: Exception) {
            // On error, try to return cached data
            try {
                val cachedProducts = productDao.getAllProducts().map { Product.fromProductEntity(it) }
                if (cachedProducts.isNotEmpty()) {
                    val pagination = PaginationInfo(1, 1, cachedProducts.size, false, false, cachedProducts.size)
                    emit(Result.success(ProductWithPagination(cachedProducts, pagination)))
                } else {
                    emit(Result.failure(e))
                }
            } catch (cacheError: Exception) {
                emit(Result.failure(e))
            }
        }
    }

    suspend fun getProductById(id: String): Flow<Result<Product>> = flow {
        try {
            // Try to get from cache first
            productDao.getProductById(id)?.let { cachedProduct ->
                emit(Result.success(Product.fromProductEntity(cachedProduct)))
            }

            // Fetch from remote
            val response = RetrofitInstance.Productapi.getProductById(id)
            if (response.isSuccess()) {
                response.data?.let { responseData ->
                    val product = responseData.product.toProduct()
                    
                    // Update cache
                    productDao.insert(product.toProductEntity())
                    
                    emit(Result.success(product))
                } ?: emit(Result.failure(Exception("Product not found")))
            } else {
                // If remote fails but we have cache, that's OK
                productDao.getProductById(id)?.let { cachedProduct ->
                    // Already emitted above, no need to emit again
                } ?: emit(Result.failure(Exception(response.error ?: "Product not found")))
            }
        } catch (e: Exception) {
            // Try cache on error
            productDao.getProductById(id)?.let { cachedProduct ->
                emit(Result.success(Product.fromProductEntity(cachedProduct)))
            } ?: emit(Result.failure(e))
        }
    }

    suspend fun addProduct(
        name: String,
        description: String,
        price: Double,
        category: String,
        imageUri: Uri?
    ): Flow<Result<Product>> = flow {
        try {
            val nameBody = name.toRequestBody("text/plain".toMediaTypeOrNull())
            val descriptionBody = description.toRequestBody("text/plain".toMediaTypeOrNull())
            val priceBody = price.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val categoryBody = category.toRequestBody("text/plain".toMediaTypeOrNull())
            
            var imagePart: MultipartBody.Part? = null
            imageUri?.let { uri ->
                val file = File(uri.path ?: "")
                if (file.exists()) {
                    val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                    imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)
                }
            }

            val response = RetrofitInstance.Productapi.addProduct(
                nameBody, descriptionBody, priceBody, categoryBody, imagePart
            )
            
            if (response.isSuccess()) {
                response.data?.let { responseData ->
                    val product = responseData.product.toProduct()
                    
                    // Cache to local
                    productDao.insert(product.toProductEntity())
                    
                    emit(Result.success(product))
                } ?: emit(Result.failure(Exception("Failed to create product")))
            } else {
                emit(Result.failure(Exception(response.error ?: "Failed to create product")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    suspend fun updateProduct(
        id: String,
        name: String? = null,
        description: String? = null,
        price: Double? = null,
        category: String? = null,
        imageUri: Uri? = null
    ): Flow<Result<Product>> = flow {
        try {
            val nameBody = name?.toRequestBody("text/plain".toMediaTypeOrNull())
            val descriptionBody = description?.toRequestBody("text/plain".toMediaTypeOrNull())
            val priceBody = price?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
            val categoryBody = category?.toRequestBody("text/plain".toMediaTypeOrNull())
            
            var imagePart: MultipartBody.Part? = null
            imageUri?.let { uri ->
                val file = File(uri.path ?: "")
                if (file.exists()) {
                    val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                    imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)
                }
            }

            val response = RetrofitInstance.Productapi.updateProduct(
                id, nameBody, descriptionBody, priceBody, categoryBody, imagePart
            )
            
            if (response.isSuccess()) {
                response.data?.let { responseData ->
                    val product = responseData.product.toProduct()
                    
                    // Update cache
                    productDao.update(product.toProductEntity())
                    
                    emit(Result.success(product))
                } ?: emit(Result.failure(Exception("Failed to update product")))
            } else {
                emit(Result.failure(Exception(response.error ?: "Failed to update product")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    suspend fun deleteProduct(id: String): Flow<Result<Boolean>> = flow {
        try {
            val response = RetrofitInstance.Productapi.deleteProduct(id)
            if (response.isSuccess()) {
                // Remove from cache
                productDao.markAsDeleted(id, System.currentTimeMillis().toString())
                emit(Result.success(true))
            } else {
                emit(Result.failure(Exception(response.error ?: "Failed to delete product")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    suspend fun searchProducts(name: String): Flow<Result<List<Product>>> = flow {
        try {
            // Search in cache first
            val cachedResults = productDao.searchProductsByName(name).map { Product.fromProductEntity(it) }
            if (cachedResults.isNotEmpty()) {
                emit(Result.success(cachedResults))
            }

            // Search in remote
            val response = RetrofitInstance.Productapi.getProductByName(name)
            if (response.isSuccess()) {
                response.data?.let { responseData ->
                    val products = responseData.product.map { it.toProduct() }
                    
                    // Cache search results
                    val productEntities = products.map { it.toProductEntity() }
                    productDao.insertAll(productEntities)
                    
                    emit(Result.success(products))
                } ?: emit(Result.failure(Exception("No products found")))
            } else {
                // Return cached results if remote fails
                if (cachedResults.isEmpty()) {
                    emit(Result.failure(Exception(response.error ?: "No products found")))
                }
            }
        } catch (e: Exception) {
            // Return cached results on error
            try {
                val cachedResults = productDao.searchProductsByName(name).map { Product.fromProductEntity(it) }
                if (cachedResults.isNotEmpty()) {
                    emit(Result.success(cachedResults))
                } else {
                    emit(Result.failure(e))
                }
            } catch (cacheError: Exception) {
                emit(Result.failure(e))
            }
        }
    }

    // Local-only operations for offline support
    fun getAllProductsLocal(): Flow<List<Product>> {
        return productDao.getAllProductsFlow().map { entities ->
            entities.map { Product.fromProductEntity(it) }
        }
    }

    suspend fun getProductsByCategory(category: String): List<Product> {
        return productDao.getProductsByCategory(category).map { Product.fromProductEntity(it) }
    }

    suspend fun getProductsByUser(userId: String): List<Product> {
        return productDao.getProductsByUser(userId).map { Product.fromProductEntity(it) }
    }

    suspend fun clearCache() {
        productDao.clearAllProducts()
    }
}