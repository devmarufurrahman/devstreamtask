package com.maruf.devstream.data.repository

import android.content.Context
import com.maruf.devstream.R
import com.maruf.devstream.data.local.ProductDatabase
import com.maruf.devstream.model.Product
import com.maruf.devstream.data.remote.ProductApi
import com.maruf.devstream.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchRepository(private val context: Context) {


    private val productApi: ProductApi
    private val productDao = ProductDatabase.getInstance(context).productDao()

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://fakestoreapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        productApi = retrofit.create(ProductApi::class.java)
    }

    // Fetch products based on connectivity
    suspend fun fetchProducts(): List<Product> {
        return withContext(Dispatchers.IO) {
            if (NetworkUtils.isOnline(context)) {
                // Fetch from API if online
                val response = productApi.getProducts()
                if (response.isSuccessful) {
                    response.body()?.let { products ->
                        productDao.clearProducts()  // Clear old data
                        productDao.insertAll(products)  // Cache new data
                    }
                }
            }
            // Load from database, whether online or offline
            productDao.getAllProducts()
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