package com.example.projectmdp.data.source.remote

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


object RetrofitInstance {

    private var token: String? = null

    fun setToken(newToken: String){
        token = newToken
    }

    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val builder = originalRequest.newBuilder()

        token?.let {
            builder.addHeader("Authorization", "Bearer $it")
        }

        val newRequest = builder.build()
        chain.proceed(newRequest)
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .build()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    val Authapi: AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }

    val Productapi: ProductApi by lazy {
        retrofit.create(ProductApi::class.java)
    }
}
