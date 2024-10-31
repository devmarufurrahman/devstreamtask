package com.maruf.devstream.network

import com.maruf.devstream.model.Product
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET

interface ProductApi {
    @GET("products")
    suspend fun getProducts(): Response<List<Product>>
}