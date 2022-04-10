package co.formaloo.formresponses.charts.adapter

import android.view.View
import android.widget.Toast
import co.formaloo.formresponses.databinding.LayoutBarBinding
import co.formaloo.model.form.Fields
import com.anychart.AnyChart
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.chart.common.listener.Event
import com.anychart.chart.common.listener.ListenersInterface
import com.anychart.charts.Pie
import com.anychart.enums.Align
import com.anychart.enums.LegendLayout
import com.anychart.palettes.RangeColors
import timber.log.Timber
import java.lang.Exception
import java.util.ArrayList

class PieHolder(itemView: View) : StatsAdapter.BaseChartViewHolder(itemView) {
    val binding = LayoutBarBinding.bind(itemView)


    fun bindItems(item: Fields) {

        val pie = AnyChart.pie()

        pie.padding(10.0, 20.0, 30.0, 20.0)

        pie.setOnClickListener(object : ListenersInterface.OnClickListener(arrayOf("x", "value")) {
            override fun onClick(event: Event) {
                Toast.makeText(
                    itemView.context,
                    event.data["x"].toString() + ":" + event.data["value"],
                    Toast.LENGTH_SHORT
                ).show()
            }
        })


        val data: MutableList<DataEntry> = ArrayList()
        when (val readableStats = item.readable_stats) {
            is Map<*, *> -> {
                val keys = ArrayList(readableStats.keys)
                keys.forEach { key ->
                    try {
                        val label = key as String
                        when (val value = readableStats[label]) {
                            is Double, is Float -> {
                                val totalCount = value.toString().toFloat()
                                data.add(ValueDataEntry(label, totalCount))

                            }
                        }
                    } catch (e: Exception) {
                        Timber.e("Key is not String?? $e")
                    }
                }

            }
        }



        pie.data(data)
        pie.title(item.title)
        pieSetting(pie)
        binding.layoutBarChartsChart.setChart(pie)

    }

    private fun pieSetting(pie: Pie) {
        val colors=arrayOf("#F82B60",
        "#CFDFFF",
        "#EDE3FE",
        "#72DDC3",
        "#FF9EB7",
        "#D1F7C4",
        "#FF6F2C",
        "#E08D00",
        "#FF08C2",
        "#7C39ED",
        "#2750AE",
        "#11AF22",
        "#06A09B",
        "#CDB0FF",
        "#9CC7FF",
        "#FFDAF6" ,
        "#20C933",
        "#FFEAB6",
        "#FFA981",
        "#D74D26")
        colors.shuffle()
        val rangeColors= RangeColors.instantiate()
        rangeColors.items(colors, "")

        pie.palette(rangeColors)

        pie.labels().position("inside")
        pie.legend().title().enabled(true)
        pie.legend().title()
            .text(" ")
            .padding(0.0, 0.0, 10.0, 0.0)

        pie.legend()
            .position("bottom")
            .itemsLayout(LegendLayout.VERTICAL)
            .align(Align.LEFT)

    }

}
