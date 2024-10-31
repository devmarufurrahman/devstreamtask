package com.maruf.devstream.repository

import android.content.Context
import com.maruf.devstream.R
import com.maruf.devstream.model.Product
import com.maruf.devstream.network.ProductApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchRepository(private val context: Context) {

    private val productApi: ProductApi

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://fakestoreapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        productApi = retrofit.create(ProductApi::class.java)
    }

    suspend fun fetchProducts(): List<Product>? {
        return withContext(Dispatchers.IO) {
            val response = productApi.getProducts()
            if (response.isSuccessful) response.body() else null
        }
    }

    fun loadChartData(): JSONObject? {
        return try {
            val inputStream = context.resources.openRawResource(R.raw.line_chart)
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            JSONObject(jsonString).getJSONObject("data")
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}