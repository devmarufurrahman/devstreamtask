package com.maruf.devstream

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.maruf.devstream.databinding.FragmentWalletBinding
import org.json.JSONObject
import java.io.InputStream


class WalletFragment : Fragment() {
    private lateinit var binding: FragmentWalletBinding
    private lateinit var chart: BarChart
    private var maxVisibleValueCount : Int = 0
    private val labels = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentWalletBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Set up the Toolbar as ActionBar
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Set up the Spend Chart
        chart = binding.spendChart
        // Set up the chart properties
        chart.apply {
            setDrawBarShadow(false)
            setDrawValueAboveBar(true)
            description.isEnabled = false
            setPinchZoom(false)
            setDrawGridBackground(false)
            maxVisibleValueCount = 60
        }

        // Load and set data
        loadDataFromJson()
    }


    private fun loadDataFromJson() {
        val entries = ArrayList<BarEntry>()

        try {
            // Read the JSON file from the raw folder
            val inputStream: InputStream = resources.openRawResource(R.raw.wallet_chart)
            val jsonString = inputStream.bufferedReader().use { it.readText() }

            // Parse JSON data
            val jsonObject = JSONObject(jsonString)
            val currency = jsonObject.getString("currency") // Retrieve currency symbol (e.g., "$")

            // Access "spend" and then "data" array
            val spendObject = jsonObject.getJSONObject("spend")
            val dataArray = spendObject.getJSONArray("data")

            if (dataArray.length() > 0) {
                val scheduleObject = dataArray.getJSONObject(0)

                var xValue = 1f
                for (key in scheduleObject.keys()) {
                    val value = scheduleObject.getDouble(key).toFloat()
                    entries.add(BarEntry(xValue, value))
                    labels.add("$currency$value") // Add label with currency symbol
                    xValue += 1f
                }
            }

            // Create and set the BarDataSet
            val dataSet = BarDataSet(entries, "Spending Data")
            dataSet.color = ContextCompat.getColor(requireContext(), R.color.purple) // Customize color if needed

            // Create and set the BarData
            val data = BarData(dataSet)
            data.barWidth = 0.8f
            chart.data = data

            // Customize X-Axis labels dynamically
            chart.xAxis.apply {
                granularity = 1f
                position = XAxis.XAxisPosition.BOTTOM
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return labels.getOrNull(value.toInt() - 1) ?: "" // Retrieve label based on index
                    }
                }
            }

            // Customize Y-Axis
            chart.axisLeft.apply {
                axisMinimum = 0f // Start Y-axis at zero
                granularity = 200f // Adjust this based on your data range
            }
            chart.axisRight.isEnabled = false // Hide right Y-axis

            // Refresh chart
            chart.invalidate()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}