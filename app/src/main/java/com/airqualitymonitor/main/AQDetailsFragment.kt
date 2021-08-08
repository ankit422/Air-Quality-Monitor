package com.airqualitymonitor.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.airqualitymonitor.databinding.DetailFragmentBinding
import com.anychart.AnyChart
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.charts.Cartesian
import com.anychart.core.cartesian.series.Column
import com.anychart.enums.Position
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AQDetailsFragment : Fragment() {

    private var binding: DetailFragmentBinding by autoCleared()
    private val viewModel: AQMViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DetailFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        viewModel.getCity(arguments?.getInt("index"))
        Toast.makeText(activity, "Wait for few seconds to update graph.", Toast.LENGTH_SHORT).show()
    }

    private fun setGraph(city: Cities) {
        val cartesian: Cartesian = AnyChart.column()
        cartesian.yScale().minimum((city.aqi - 10).toInt())
        cartesian.xAxis(0).title("Air Quality")

        binding.anyChartView.setChart(cartesian)

        val firstItem = listOf(ValueDataEntry(0, city.aqi.toInt()))
        val column: Column = cartesian.column(firstItem)
        column.fill(viewModel.getPair(city).first)
        column.stroke(viewModel.getPair(city).first)
        column.tooltip().position(Position.CENTER_BOTTOM).format("\${%Value}{groupsSeparator: }")

        viewModel.cityData.observe(viewLifecycleOwner, {
            column.data(it)

        })
    }

    private fun setupObservers() {
        viewModel.cityDetails.observe(viewLifecycleOwner, {
            if (it.city.isNotEmpty()) {
                setData(it)
                setGraph(it)
            }
        })
    }

    private fun setData(dataItem: Cities?) {
        dataItem?.let {
            (activity as AppCompatActivity?)!!.supportActionBar?.title = it.city
            (activity as AppCompatActivity?)!!.supportActionBar?.subtitle =
                "AQI ${it.aqi} (${it.status.second})"
        }
    }
}
