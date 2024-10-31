package com.maruf.devstream.ui.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.maruf.devstream.ui.fragment.HomeFragment
import com.maruf.devstream.R
import com.maruf.devstream.ui.fragment.SearchFragment
import com.maruf.devstream.ui.fragment.WalletFragment
import com.maruf.devstream.databinding.ActivityMainBinding
import com.maruf.devstream.ui.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        // Observe currentFragment LiveData to handle fragment changes
        viewModel.currentFragment.observe(this) { fragment ->
            loadFragment(fragment)
        }

        // Set up item selection listener for BottomNavigationView
        binding.bottomNavView.setOnItemSelectedListener { menuItem ->
            viewModel.selectFragment(menuItem.itemId)
            true
        }
    }

    // Helper function to load fragments
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, fragment)
            .commit()
    }
}