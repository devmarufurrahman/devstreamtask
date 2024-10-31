package com.maruf.devstream.ui.viewmodel

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.maruf.devstream.R
import com.maruf.devstream.ui.fragment.HomeFragment
import com.maruf.devstream.ui.fragment.SearchFragment
import com.maruf.devstream.ui.fragment.WalletFragment

class MainViewModel : ViewModel() {

    private val fragmentMap: Map<Int, Fragment> = mapOf(
        R.id.home_menu to HomeFragment(),
        R.id.search_menu to SearchFragment(),
        R.id.wallet_menu to WalletFragment()
    )

    private val _currentFragment = MutableLiveData<Fragment>()
    val currentFragment: LiveData<Fragment> get() = _currentFragment

    init {
        _currentFragment.value = fragmentMap[R.id.home_menu]
    }

    fun selectFragment(menuItemId: Int) {
        _currentFragment.value = fragmentMap[menuItemId] ?: HomeFragment()
    }

}