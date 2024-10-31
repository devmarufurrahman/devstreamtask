package com.maruf.devstream.ui.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maruf.devstream.model.Product
import com.maruf.devstream.data.repository.SearchRepository
import kotlinx.coroutines.launch
import org.json.JSONObject

class SearchViewModel(context: Context) : ViewModel() {

    private val repository = SearchRepository(context)

    private val _chartData = MutableLiveData<JSONObject?>()
    val chartData: LiveData<JSONObject?> get() = _chartData

    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> get() = _products

    init {
        loadChartData()
        fetchProducts()
    }

    fun fetchProducts() {
        viewModelScope.launch {
            val productList = repository.fetchProducts()
            _products.value = productList
        }
    }


    private fun loadChartData() {
        _chartData.value = repository.loadChartData()
    }}