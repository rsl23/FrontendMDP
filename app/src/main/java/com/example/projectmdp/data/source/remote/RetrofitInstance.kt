package com.example.projectmdp.data.source.remote

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


object RetrofitInstance {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://your-api-url.com/") // 🔁 Change this
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    val api: AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }
}
