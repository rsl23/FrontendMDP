package com.example.projectmdp.data.source.remote

import com.example.projectmdp.data.model.product.ProductDto
import com.example.projectmdp.data.source.response.GetAllProduct
import com.example.projectmdp.data.source.response.GetProduct
import com.example.projectmdp.data.source.response.ProductResponse
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Path

interface ProductApi {
    @POST("/add-product")
    suspend fun addProduct (@Body productDTO: ProductDto): ProductResponse

    @GET("/products")
    suspend fun getAllProduct(): GetAllProduct
    @GET("/product/{id}")
    suspend fun getProductById( @Path("id") id: String) : GetProduct
    @PUT("/product/{id}")
    suspend fun updateProduct(@Path("id") id: String): GetProduct
    @DELETE("/product/{id}")
    suspend fun deleteProduct(@Path("id") id: String): ProductResponse
    @GET("/product/search/{name}")
    suspend fun getProductByName (@Path("name") name:String) : GetProduct


}