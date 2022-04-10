package co.formaloo.formresponses.charts.adapter

import android.view.View
import co.formaloo.formresponses.databinding.LayoutBarBinding
import co.formaloo.model.form.Fields
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.core.cartesian.series.Bar
import com.anychart.data.Mapping
import com.anychart.data.Set
import com.anychart.enums.*
import com.anychart.palettes.RangeColors
import kotlin.random.Random


class BarVHolder(itemView: View) : StatsAdapter.BaseChartViewHolder(itemView) {
    val binding = LayoutBarBinding.bind(itemView)

    fun bindItems(item: Fields) {

        setChartData(binding.layoutBarChartsChart, item.readable_stats, item.title)
    }

    private fun setChartData(anyChartView: AnyChartView, mapData: Any?, title: String?) {
        val seriesData: MutableList<DataEntry> = ArrayList()
        val barChart = AnyChart.bar()

        barChart.animation(true)

        barChart.padding(10.0, 20.0, 30.0, 20.0)

        barChart.yScale().stackMode(ScaleStackMode.VALUE)

        barChart.yAxis(0).labels().format(
            "function() {\n" +
                    "    return Math.abs(this.value).toLocaleString();\n" +
                    "  }"
        )

        barChart.yAxis(0.0)

        barChart.xAxis(0.0).overlapMode(LabelsOverlapMode.NO_OVERLAP)

//        val xAxis1: Linear = barChart.xAxis(1.0)
//        xAxis1.enabled(true)
//        xAxis1.orientation(Orientation.RIGHT)
//        xAxis1.overlapMode(LabelsOverlapMode.NO_OVERLAP)

        barChart.title(title)

        barChart.interactivity().hoverMode(HoverMode.BY_X)

        barChart.tooltip()
            .title(true)
            .separator(true)
            .displayMode(TooltipDisplayMode.SINGLE)
            .positionMode(TooltipPositionMode.POINT)
            .useHtml(true)
            .fontSize(12.0)
            .offsetX(5.0)
            .offsetY(5.0)
            .format(
                ("function() {\n" +
                        "      return '<span style=\"color: #D9D9D9\"></span>' + Math.abs(this.value).toLocaleString();\n" +
                        "    }")
            )


        var groupCount = 1


        val readableData = mapData as Map<*, *>
        var keys = ArrayList(readableData.keys)

        keys = removeExtraData(keys)

        groupCount = keys.size

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

        for (i in 0 until groupCount) {

            val groupKey = keys[i]
            val groupValue = readableData[groupKey]
            val choiceValue = groupValue as Double
            val title = groupKey?.toString() ?: ""

            seriesData.add(ValueDataEntry(title, choiceValue.toFloat()))
            val set: Set = Set.instantiate()
            set.data(seriesData)
            val seriesMapping: Mapping = set.mapAs("{ x: 'x', value: 'value$i' }")

            val series1: Bar = barChart.bar(seriesMapping)

            val colorSize = colors.size

            val x = if (i <= colorSize) {
                i
            } else {
                Random.nextInt(0, colorSize - 1)
            }

//            series1.color(colors[x])
            series1.name(title)
            series1.tooltip()
                .title(title)
                .position("left")
                .anchor(Anchor.RIGHT_CENTER)

        }


        barChart.data(seriesData)

        val rangeColors= RangeColors.instantiate()
        rangeColors.items(colors, "")

        barChart.palette(rangeColors)

        anyChartView.setChart(barChart)
        anyChartView.invalidate()
    }

    private fun removeExtraData(keys: java.util.ArrayList<Any?>): java.util.ArrayList<Any?> {
        for (i in 0 until (keys.size) - 1) {
            if (keys[i] == "type") {
                keys.removeAt(i)
            } else if (keys[i] == "sub_type") {
                keys.removeAt(i)
            }
        }

        return keys
    }


}
