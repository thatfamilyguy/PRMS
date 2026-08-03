package com.example.ui.components

import android.graphics.Color
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

@Composable
fun ComposableMpBarChart(
    labels: List<String>,
    values: List<Float>,
    chartTitle: String,
    modifier: Modifier = Modifier.fillMaxWidth().height(220.dp)
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            BarChart(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                description.isEnabled = false
                legend.isEnabled = true
                legend.textColor = Color.parseColor("#1D1B1E")
                setDrawGridBackground(false)
                setDrawBarShadow(false)
                setTouchEnabled(true)
                setPinchZoom(false)

                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    textColor = Color.parseColor("#49454F")
                    valueFormatter = IndexAxisValueFormatter(labels)
                    granularity = 1f
                }

                axisLeft.apply {
                    setDrawGridLines(true)
                    gridColor = Color.parseColor("#E7E0EC")
                    textColor = Color.parseColor("#49454F")
                    axisMinimum = 0f
                }

                axisRight.isEnabled = false
            }
        },
        update = { chart ->
            val entries = values.mapIndexed { index, value ->
                BarEntry(index.toFloat(), value)
            }
            val dataSet = BarDataSet(entries, chartTitle).apply {
                color = Color.parseColor("#6750A4")
                valueTextColor = Color.parseColor("#21005D")
                valueTextSize = 12f
            }
            chart.data = BarData(dataSet)
            chart.animateY(800)
            chart.invalidate()
        }
    )
}

@Composable
fun ComposableMpPieChart(
    entries: List<Pair<String, Float>>,
    colorsHex: List<String>,
    modifier: Modifier = Modifier.fillMaxWidth().height(220.dp)
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            PieChart(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                description.isEnabled = false
                legend.textColor = Color.parseColor("#1D1B1E")
                isDrawHoleEnabled = true
                setHoleColor(Color.parseColor("#FDF8F6"))
                setTransparentCircleColor(Color.TRANSPARENT)
                setEntryLabelColor(Color.parseColor("#1D1B1E"))
                setEntryLabelTextSize(11f)
            }
        },
        update = { chart ->
            val pieEntries = entries.map { PieEntry(it.second, it.first) }
            val dataSet = PieDataSet(pieEntries, "").apply {
                colors = colorsHex.map { Color.parseColor(it) }
                valueTextColor = Color.parseColor("#21005D")
                valueTextSize = 12f
                sliceSpace = 3f
            }
            chart.data = PieData(dataSet)
            chart.animateXY(800, 800)
            chart.invalidate()
        }
    )
}

