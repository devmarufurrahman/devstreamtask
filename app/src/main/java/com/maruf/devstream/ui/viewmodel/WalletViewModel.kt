package com.maruf.devstream.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.maruf.devstream.repository.WalletRepository

class WalletViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WalletRepository(application)
    private val _chartData = MutableLiveData<Pair<String, List<Pair<String, Float>>>>()
    val chartData: LiveData<Pair<String, List<Pair<String, Float>>>> get() = _chartData

    fun loadChartData() {
        _chartData.value = repository.loadChartData()
    }
}