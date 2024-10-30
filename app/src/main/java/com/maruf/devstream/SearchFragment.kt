package com.maruf.devstream

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.maruf.devstream.databinding.FragmentSearchBinding


class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var lineChart: LineChart
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

}