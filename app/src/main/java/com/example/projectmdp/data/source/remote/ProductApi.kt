package com.example.projectmdp.data.source.remote

import com.example.projectmdp.data.model.product.ProductDto
import com.example.projectmdp.data.source.response.GetAllProduct
import com.example.projectmdp.data.source.response.GetProduct
import com.example.projectmdp.data.source.response.ProductResponse
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface ProductApi {
    @POST("/addProduct")
    suspend fun addProduct (@Body productDTO: ProductDto ): ProductResponse

    @GET("/allProduct")
    suspend fun getAllProduct(): GetAllProduct
    @GET("/product/{id}")
    suspend fun getProductById( @Path("id") id: String) : GetProduct

    @GET("/product/search/{name}")
    suspend fun getProductByName (@Path("name") name:String) : GetProduct


}