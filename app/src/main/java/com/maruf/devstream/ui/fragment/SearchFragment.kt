package com.maruf.devstream.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.maruf.devstream.model.Product
import com.maruf.devstream.adapter.ProductAdapter
import com.maruf.devstream.network.ProductApi
import com.maruf.devstream.R
import com.maruf.devstream.databinding.FragmentSearchBinding
import com.maruf.devstream.repository.SearchRepository
import com.maruf.devstream.ui.viewmodel.SearchViewModel
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.InputStream


class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var viewModel: SearchViewModel
    private lateinit var productAdapter: ProductAdapter
    private lateinit var timePeriodButtons: List<TextView>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        viewModel = SearchViewModel(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Set up RecyclerView for products
        productAdapter = ProductAdapter(emptyList())
        binding.recentProductsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = productAdapter
        }

        // Observe product list changes
        viewModel.products.observe(viewLifecycleOwner) { products ->
            productAdapter.updateProducts(products)
        }

        // Observe chart data and update the chart when data is available
        viewModel.chartData.observe(viewLifecycleOwner) { chartData ->
            chartData?.let { updateChartData("1W", chartData, binding.button1W) }
        }

        // Set up click listeners for time period buttons
        timePeriodButtons = listOf(
            binding.button1W, binding.button1M, binding.button3M,
            binding.button6M, binding.button1Y, binding.buttonALL
        )
        timePeriodButtons.forEach { button ->
            button.setOnClickListener {
                val timePeriod = button.text.toString()
                updateChartData(timePeriod, viewModel.chartData.value, button)
            }
        }
    }

    private fun updateChartData(timePeriod: String, chartData: JSONObject?, textView: TextView? = null) {
        if (chartData == null) return

        textView?.apply {
            timePeriodButtons.forEach { buttonBg ->
                if (buttonBg == textView) {
                    buttonBg.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.time_period_shape
                    )
                    buttonBg.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                } else {
                    buttonBg.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.oval_shape
                    )
                    buttonBg.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                }
            }
        }


        val periodData = chartData.optJSONObject(timePeriod) ?: return
        val xAxisData = periodData.getJSONArray("xAxisData")
        val yAxisData = periodData.getJSONArray("yAxisData")

        val entries = ArrayList<Entry>()
        for (i in 0 until yAxisData.length()) {
            entries.add(Entry(i.toFloat(), yAxisData.getDouble(i).toFloat()))
        }

        val lineDataSet = LineDataSet(entries, "$timePeriod Data").apply {
            color = ContextCompat.getColor(requireContext(), R.color.purple)
            valueTextColor = ContextCompat.getColor(requireContext(), R.color.black)
            lineWidth = 2f
            setDrawCircles(true)
            circleRadius = 4f
        }

        val lineData = LineData(lineDataSet)
        binding.lineChart.data = lineData
        binding.lineChart.xAxis.apply {
            granularity = 1f
            position = XAxis.XAxisPosition.BOTTOM
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return xAxisData.optString(value.toInt(), "")
                }
            }
        }
        binding.lineChart.invalidate()
    }

}