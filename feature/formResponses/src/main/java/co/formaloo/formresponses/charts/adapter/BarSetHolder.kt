package co.formaloo.formresponses.charts.adapter

import android.view.View
import co.formaloo.formresponses.databinding.LayoutBarBinding
import co.formaloo.formresponses.reportMaker.CustomDataEntry2
import co.formaloo.model.form.Fields
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.core.axes.Linear
import com.anychart.core.cartesian.series.Bar
import com.anychart.data.Mapping
import com.anychart.data.Set
import com.anychart.enums.*


class BarSetHolder(itemView: View) : StatsAdapter.BaseChartViewHolder(itemView) {
    val binding = LayoutBarBinding.bind(itemView)

    fun bindItems(item: Fields) {

        setChartData(binding.layoutBarChartsChart, item.readable_stats,item.title)
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

        val xAxis1: Linear = barChart.xAxis(1.0)
        xAxis1.enabled(true)
        xAxis1.orientation(Orientation.RIGHT)
        xAxis1.overlapMode(LabelsOverlapMode.NO_OVERLAP)

        barChart.title(title)

        barChart.interactivity().hoverMode(HoverMode.SINGLE)

        barChart.tooltip()
            .title(false)
            .separator(true)
            .displayMode(TooltipDisplayMode.SEPARATED)
            .positionMode(TooltipPositionMode.CHART)
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
        val startGroup = 0
        var endGroup = 0

        val readableData = mapData as Map<*, *>
        var keys = ArrayList(readableData.keys)

        keys = removeExtraData(keys)


        groupCount = keys.size
        endGroup = startGroup + groupCount

        for (j in startGroup until endGroup) {

            val groupKey = keys[j]

            val groupItemList = readableData[groupKey]

            val values= arrayListOf<Number>()
            if (groupItemList is Map<*, *>) {

                val choiceKeyList = ArrayList((groupItemList).keys)
               val dataSetCount = choiceKeyList.size

                for (k in 0 until dataSetCount) {
                    val choiceKey = choiceKeyList[k] as String
                    val choiceValue = groupItemList[choiceKeyList[k]] as Double

                    values.add(choiceValue)
                    groupKey.toString()



                }
                seriesData.add(CustomDataEntry2(groupKey?.toString() ?: "", values))


            }


        }

        val set: Set = Set.instantiate()
        set.data(seriesData)

        for (i in 1..groupCount) {
            val yModel = keys[i - 1]
            val seriesMapping: Mapping = set.mapAs("{ x: 'x', value: 'value$i' }")

            val series1: Bar = barChart.bar(seriesMapping)
            series1.name(yModel?.toString()?:"")
            series1.tooltip()
                .position("left")
                .anchor(Anchor.RIGHT_CENTER)

        }

        barChart.data(seriesData)



        barChart.legend().enabled(true)
        barChart.legend().inverted(false)
        barChart.legend().fontSize(13.0)
        barChart.legend().padding(0.0, 0.0, 20.0, 0.0)

        anyChartView.setChart(barChart)

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
