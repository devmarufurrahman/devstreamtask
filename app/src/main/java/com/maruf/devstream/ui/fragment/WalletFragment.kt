package com.maruf.devstream.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.maruf.devstream.R
import com.maruf.devstream.databinding.FragmentWalletBinding
import com.maruf.devstream.ui.viewmodel.WalletViewModel


class WalletFragment : Fragment() {
    private lateinit var binding: FragmentWalletBinding
    private lateinit var chart: BarChart
    private val viewModel: WalletViewModel by viewModels()
    private val labels = ArrayList<String>()
    private var maxVisibleValueCount = 0

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

        chart = binding.spendChart
        chart.apply {
            setDrawBarShadow(false)
            setDrawValueAboveBar(true)
            description.isEnabled = false
            setPinchZoom(false)
            setDrawGridBackground(false)
            maxVisibleValueCount = 60
        }

        viewModel.chartData.observe(viewLifecycleOwner) { (currency, dataPoints) ->
            updateChart(currency, dataPoints)
        }

        viewModel.loadChartData()
    }

    private fun updateChart(currency: String, dataPoints: List<Pair<String, Float>>) {
        val entries = dataPoints.mapIndexed { index, data -> BarEntry(index.toFloat(), data.second) }
        labels.clear()
        labels.addAll(dataPoints.map { "${currency}${it.second}" })

        val dataSet = BarDataSet(entries, "Spending Data")
        dataSet.color = ContextCompat.getColor(requireContext(), R.color.purple)

        val barData = BarData(dataSet).apply { barWidth = 0.8f }
        chart.data = barData

        chart.xAxis.apply {
            granularity = 1f
            position = XAxis.XAxisPosition.BOTTOM
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String = labels.getOrNull(value.toInt()) ?: ""
            }
        }

        chart.axisLeft.apply {
            axisMinimum = 0f
            granularity = 200f
        }
        chart.axisRight.isEnabled = false
        chart.invalidate()
    }
}