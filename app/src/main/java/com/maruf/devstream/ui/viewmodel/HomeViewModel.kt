package com.maruf.devstream.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _greetingText = MutableLiveData<String>()
    val greetingText: LiveData<String> get() = _greetingText

    private val _cardBalance = MutableLiveData<String>()
    val cardBalance: LiveData<String> get() = _cardBalance

    private val _dueDateText = MutableLiveData<String>()
    val dueDateText: LiveData<String> get() = _dueDateText

    init {
        // Initialize with default values
        _greetingText.value = "✌️ Hey George!"
        _cardBalance.value = "$5,001.86"
        _dueDateText.value = "Due Date 10th Oct"
    }
}