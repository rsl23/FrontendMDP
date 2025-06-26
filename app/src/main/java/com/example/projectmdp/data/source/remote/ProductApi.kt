package com.example.projectmdp.data.source.remote

import com.example.projectmdp.data.model.product.ProductDto
import com.example.projectmdp.data.source.response.AddProductData
import com.example.projectmdp.data.source.response.ApiResponse
import com.example.projectmdp.data.source.response.DeleteProductData
import com.example.projectmdp.data.source.response.GetAllProduct
import com.example.projectmdp.data.source.response.GetAllProductsData
import com.example.projectmdp.data.source.response.GetProduct
import com.example.projectmdp.data.source.response.GetProductData
import com.example.projectmdp.data.source.response.ProductResponse
import com.example.projectmdp.data.source.response.SearchProductData
import com.example.projectmdp.data.source.response.UpdateProductData
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductApi {
//    @POST("/add-product")
//    suspend fun addProduct (@Body productDTO: ProductDto): ProductResponse
//
//    @GET("/products")
//    suspend fun getAllProduct(): GetAllProduct
//    @GET("/product/{id}")
//    suspend fun getProductById( @Path("id") id: String) : GetProduct
//    @PUT("/product/{id}")
//    suspend fun updateProduct(@Path("id") id: String): GetProduct
//    @DELETE("/product/{id}")
//    suspend fun deleteProduct(@Path("id") id: String): ProductResponse
//    @GET("/product/search/{name}")
//    suspend fun getProductByName (@Path("name") name:String) : GetProduct


    @Multipart
    @POST("/add-product")
    suspend fun addProduct(
        @Part("name") name: RequestBody,
        @Part("description") description: RequestBody,
        @Part("price") price: RequestBody,
        @Part("category") category: RequestBody,
        @Part image: MultipartBody.Part?
    ): ApiResponse<AddProductData>

    @GET("/products")
    suspend fun getAllProducts(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
    ): ApiResponse<GetAllProductsData>

    @GET("/product/{id}")
    suspend fun getProductById(
        @Path("id") id: String
    ): ApiResponse<GetProductData>

    @Multipart
    @PUT("/product/{id}")
    suspend fun updateProduct(
        @Path("id") id: String,
        @Part("name") name: RequestBody?,
        @Part("description") description: RequestBody?,
        @Part("price") price: RequestBody?,
        @Part("category") category: RequestBody?,
        @Part image: MultipartBody.Part?
    ): ApiResponse<UpdateProductData>

    @DELETE("/product/{id}")
    suspend fun deleteProduct(
        @Path("id") id: String
    ): ApiResponse<DeleteProductData>

    @GET("/product/search/{name}")
    suspend fun getProductByName(
        @Path("name") name: String
    ): ApiResponse<SearchProductData>

}