package com.example.projectmdp.data.source.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.projectmdp.data.source.local.entity.ProductEntity

@Dao
interface ProductDao {
    @Insert
    suspend fun insert (product: ProductEntity)
    @Update
    suspend fun update (product: ProductEntity)
    @Delete
    suspend fun delete (product: ProductEntity)

    @Query("Select * from products")
    suspend fun products() : List<ProductEntity>

    @Query("Select * from products where id = :id")
    suspend fun getProductById(id:String) : ProductEntity
}