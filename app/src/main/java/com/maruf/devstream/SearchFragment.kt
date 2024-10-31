package com.maruf.devstream

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.maruf.devstream.databinding.FragmentSearchBinding
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.InputStream


class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var lineChart: LineChart
    private lateinit var recentProductsRecyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var timePeriodButtons: List<TextView>
    private var chartData: JSONObject? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for requireContext() fragment
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
        // Initialize time period buttons
        timePeriodButtons = listOf(
            binding.button1W,
            binding.button1M,
            binding.button3M,
            binding.button6M,
            binding.button1Y,
            binding.buttonALL
        )
        // Load JSON data
        loadJsonData()

        // Set default chart data (e.g., "1W")
        updateChartData("1W", binding.button1W)

        // Set click listeners for each button to update chart dynamically
        timePeriodButtons.forEach { button ->
            button.setOnClickListener {
                val timePeriod = button.text.toString()
                updateChartData(timePeriod, button)
            }
        }



//        product show
        recentProductsRecyclerView = binding.recentProductsRecyclerView
        recentProductsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        // Set up the product
        fetchProducts()

    }

    private fun loadJsonData() {
        try {
            val inputStream: InputStream = resources.openRawResource(R.raw.line_chart)
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            chartData = JSONObject(jsonString).getJSONObject("data")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateChartData(timePeriod: String, button: TextView? = null) {
        if (chartData == null) return
        button?.apply {
            timePeriodButtons.forEach { buttonBg ->
                if (buttonBg == button){
                    buttonBg.background = ContextCompat.getDrawable(requireContext(), R.drawable.time_period_shape)
                    buttonBg.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                }else{
                    buttonBg.background = ContextCompat.getDrawable(requireContext(), R.drawable.oval_shape)
                    buttonBg.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                }

            }
        }

        // Retrieve data for the selected time period
        val periodData = chartData?.getJSONObject(timePeriod) ?: return
        val xAxisData = periodData.getJSONArray("xAxisData")
        val yAxisData = periodData.getJSONArray("yAxisData")

        // Prepare data entries for the LineChart
        val entries = ArrayList<Entry>()
        for (i in 0 until yAxisData.length()) {
            entries.add(Entry(i.toFloat(), yAxisData.getDouble(i).toFloat()))
        }

        // Create a LineDataSet and set its appearance
        val lineDataSet = LineDataSet(entries, "$timePeriod Data")
        lineDataSet.color = ContextCompat.getColor(requireContext(), R.color.purple)
        lineDataSet.valueTextColor = ContextCompat.getColor(requireContext(), R.color.black)
        lineDataSet.lineWidth = 2f
        lineDataSet.setDrawCircles(true)
        lineDataSet.circleRadius = 4f

        // Set the LineData to LineChart
        val lineData = LineData(lineDataSet)
        lineChart.data = lineData

        // Customize X-axis labels
        lineChart.xAxis.apply {
            granularity = 1f
            position = XAxis.XAxisPosition.BOTTOM
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return xAxisData.optString(value.toInt(), "")
                }
            }
        }

        // Show Y-axis values only on the right side
        lineChart.axisLeft.isEnabled = false // Hide the left Y-axis
        lineChart.axisRight.isEnabled = true // Show the right Y-axis
        lineChart.axisRight.apply {
            axisMinimum = 0f // Set the minimum value for the Y-axis
            granularity = 1000f // Set the interval for Y-axis labels
        }

        // Refresh chart
        lineChart.invalidate()
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