package com.example.projectmdp.data.source.local.datasource

import com.example.projectmdp.data.source.dataclass.Product
import com.example.projectmdp.data.source.dataclass.User

interface LocalDataSource {

    //User
    suspend fun InsertUser(user:User)
    suspend fun UpdateUser(user:User)
    suspend fun deleteUser(user: User)
    suspend fun getUserbyId(id:String) : User?

    //Product
    suspend fun InsertProduct(product: Product)
    suspend fun UpdateProduct(product: Product)
    suspend fun DeleteProduct(product: Product)
    suspend fun getAllProduct() :List<Product>
    suspend fun getProductById(id:String): Product?
}