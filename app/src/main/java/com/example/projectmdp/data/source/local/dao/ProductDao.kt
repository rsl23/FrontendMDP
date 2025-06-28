package com.example.projectmdp.data.source.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.projectmdp.data.source.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    // Insert with conflict strategy untuk handle duplicate dari remote
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: ProductEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<ProductEntity>)
    
    @Update
    suspend fun update(product: ProductEntity)
    
    @Delete
    suspend fun delete(product: ProductEntity)

    // Basic queries
    @Query("SELECT * FROM products WHERE deleted_at IS NULL ORDER BY created_at DESC")
    suspend fun getAllProducts(): List<ProductEntity>
    
    @Query("SELECT * FROM products WHERE deleted_at IS NULL ORDER BY created_at DESC")
    fun getAllProductsFlow(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE product_id = :id AND deleted_at IS NULL")
    suspend fun getProductById(id: String): ProductEntity?
    
    @Query("SELECT * FROM products WHERE product_id = :id AND deleted_at IS NULL")
    fun getProductByIdFlow(id: String): Flow<ProductEntity?>

    // Search queries
    @Query("SELECT * FROM products WHERE name LIKE '%' || :name || '%' AND deleted_at IS NULL ORDER BY created_at DESC")
    suspend fun searchProductsByName(name: String): List<ProductEntity>
    
    @Query("SELECT * FROM products WHERE category = :category AND deleted_at IS NULL ORDER BY created_at DESC")
    suspend fun getProductsByCategory(category: String): List<ProductEntity>
    
    @Query("SELECT * FROM products WHERE user_id = :userId AND deleted_at IS NULL ORDER BY created_at DESC")
    suspend fun getProductsByUser(userId: String): List<ProductEntity>

    // Cache management queries
    @Query("DELETE FROM products")
    suspend fun clearAllProducts()
    
    @Query("DELETE FROM products WHERE user_id = :userId")
    suspend fun clearProductsByUser(userId: String)
    
    @Query("SELECT COUNT(*) FROM products WHERE deleted_at IS NULL")
    suspend fun getProductCount(): Int
    
    // Queries untuk offline support
    @Query("SELECT * FROM products WHERE created_at > :timestamp AND deleted_at IS NULL")
    suspend fun getProductsUpdatedAfter(timestamp: Long): List<ProductEntity>
    
    @Query("UPDATE products SET deleted_at = :deletedAt WHERE product_id = :id")
    suspend fun markAsDeleted(id: String, deletedAt: String)
    
    // Get categories untuk filter
    @Query("SELECT DISTINCT category FROM products WHERE deleted_at IS NULL AND category != ''")
    suspend fun getAllCategories(): List<String>
}