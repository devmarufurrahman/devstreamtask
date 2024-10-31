package com.maruf.devstream

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.maruf.devstream.databinding.FragmentSearchBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var lineChart: LineChart
    private lateinit var recentProductsRecyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Set up the Toolbar as ActionBar
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

//        set up line chart
        lineChart = binding.lineChart
        // Set up the chart data
        setLineChartData()

//        product show
        recentProductsRecyclerView = binding.recentProductsRecyclerView
        recentProductsRecyclerView.layoutManager = LinearLayoutManager(requireContext())


    }
    private fun setLineChartData() {
        // Create a list of entries (data points)
        val entries = ArrayList<Entry>()
        entries.add(Entry(1f, 400f))
        entries.add(Entry(2f, 600f))
        entries.add(Entry(3f, 1200f))
        entries.add(Entry(4f, 1800f))
        entries.add(Entry(5f, 2200f))

        // Create a LineDataSet and customize its appearance
        val lineDataSet = LineDataSet(entries, "Spending Data")
        lineDataSet.color = ContextCompat.getColor(requireContext(), R.color.purple)
        lineDataSet.valueTextColor = ContextCompat.getColor(requireContext(), R.color.black)
        lineDataSet.lineWidth = 2f
        lineDataSet.setDrawCircles(true)
        lineDataSet.setDrawCircleHole(false)
        lineDataSet.circleRadius = 4f
        lineDataSet.setCircleColor(ContextCompat.getColor(requireContext(), R.color.purple))

        // Create LineData and set it to the chart
        val lineData = LineData(lineDataSet)
        lineChart.data = lineData

        // Customize the chart
        lineChart.apply {
            description.isEnabled = false
            setPinchZoom(true)
            axisRight.isEnabled = false // Hide right Y-axis
            invalidate() // Refresh chart with data
        }
    }


    private fun fetchProducts() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://fakestoreapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val productApi = retrofit.create(ProductApi::class.java)
        productApi.getProducts().enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if (response.isSuccessful) {
                    val products = response.body()?.map { it.toProduct() } ?: emptyList()
                    productAdapter = ProductAdapter(products)
                    recentProductsRecyclerView.adapter = productAdapter
                    productAdapter.notifyDataSetChanged()
                } else {
                    Log.e("MainActivity", "Failed to load products")
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                Log.e("MainActivity", "Error: ${t.message}")
            }
        })
    }

}