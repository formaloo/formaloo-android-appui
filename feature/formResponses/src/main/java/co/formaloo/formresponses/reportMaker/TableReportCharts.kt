package co.formaloo.formresponses.reportMaker

import android.content.Context
import android.widget.Toast
import co.formaloo.common.Constants.DROPDOWN
import co.formaloo.common.Constants.Like_Dislike
import co.formaloo.common.Constants.MULTI_SELECT
import co.formaloo.common.Constants.RATING
import co.formaloo.common.Constants.SINGLE_SELECT
import co.formaloo.common.Constants.YESNO
import co.formaloo.common.Constants.nps
import co.formaloo.common.Constants.star
import co.formaloo.model.form.ChoiceItem
import co.formaloo.model.form.Fields
import co.formaloo.model.reportmaker.tableChart.TableChartDatum
import co.formaloo.model.reportmaker.tableChart.TableReport
import co.formaloo.model.reportmaker.tableChart.XFieldValue
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.chart.common.listener.Event
import com.anychart.chart.common.listener.ListenersInterface
import com.anychart.charts.Cartesian
import com.anychart.charts.Pie
import com.anychart.core.cartesian.series.Bar
import com.anychart.core.cartesian.series.Line
import com.anychart.data.Mapping
import com.anychart.data.Set
import com.anychart.enums.*
import com.anychart.graphics.vector.Stroke
import com.anychart.palettes.RangeColors
import com.google.gson.Gson
import timber.log.Timber

class TableReportCharts(private val context: Context) {

    fun fillLineChart(it: TableReport, anyChartView: AnyChartView) {
        val yFields = it.yFields
        if (yFields?.isNotEmpty() == true) {
            val cartesian: Cartesian = AnyChart.line()

            cartesian.animation(true)

            cartesian.padding(10.0, 20.0, 30.0, 20.0)

            cartesian.crosshair().enabled(true)
            cartesian.crosshair()
                .xLabel(true)
                .yLabel(true)
                .yStroke(null as Stroke?, null, null, null as String?, null as String?)

            cartesian.tooltip().positionMode(TooltipPositionMode.POINT)

            cartesian.title(it.title)

            cartesian.yAxis(0).title(" ")
            cartesian.xAxis(0).labels().padding(5.0, 5.0, 5.0, 5.0)


            val chartData = it.chartData
            val seriesData: MutableList<DataEntry> = ArrayList()


            val xValueSlugTitleList = mapXYValSlug(it.xField!!)
            xValueSlugTitleList.sortedBy {
                it.slug
            }

            val xFieldSlug = it.xField?.slug
            xValueSlugTitleList.forEach { xyModel ->
                Timber.e("xValueSlugTitleListItm $xyModel")

                chartData?.filter { hashMap ->
                    val gson = Gson();
                    val value = hashMap[xFieldSlug]?.value
                    Timber.e("value $value")

                    val toJson = gson.toJson(value)
                    val xFieldValue = gson.fromJson(toJson, XFieldValue::class.java)

                    Timber.e("xFieldValue $xFieldValue")
                    hashMap.containsKey(xFieldSlug) && xFieldValue.slug == xyModel.slug

                }?.let {
                    Timber.e("listsize ${it.size}")
                    if (it.isNotEmpty()) {
                        val countList = arrayListOf<Number>()
                        yFields.forEach { yField ->
                            var valueSum: Long = 0

                            val yFieldSlug = yField.slug
                            it.filter { hashMap ->
                                hashMap.containsKey(yFieldSlug)

                            }.forEach { hashMap ->
                                val value = hashMap[yFieldSlug]?.value?.toString() ?: 0
                                Timber.e("value $value")


                                if (value.toString().isNotEmpty()) {
                                    val gson = Gson();

                                    val jsonString = gson.toJson(value)
                                    val yFieldValue =
                                        gson.fromJson(jsonString, Long::class.java)
                                    Timber.e("yFieldValue-> $yFieldValue")

                                    val count = if (yFieldValue is Long) {
                                        yFieldValue
                                    } else {
                                        0
                                    }

                                    valueSum += count

                                }

                            }


                            countList.add(valueSum)

                        }


                        seriesData.add(CustomDataEntry2(xyModel.title, countList.toList()))

                    }

                }


            }

            val set: Set = Set.instantiate()
            set.data(seriesData)

            for (i in 1..yFields.size) {
                val yModel = yFields[i - 1]

                val seriesMapping: Mapping = set.mapAs("{ x: 'x', value: 'value$i' }")

                val series1: Line = cartesian.line(seriesMapping)
                series1.name(yModel.title)
                series1.hovered().markers().enabled(true)
                series1.hovered().markers()
                    .type(MarkerType.CIRCLE)
                    .size(4.0)
                series1.tooltip()
                    .position("right")
                    .anchor(Anchor.LEFT_CENTER)
                    .offsetX(5.0)
                    .offsetY(5.0)
            }


            val legend = cartesian.legend()
            legend.enabled(true)
            legend.fontSize(13.0)
            legend.padding(0.0, 0.0, 10.0, 0.0)

            anyChartView.setChart(cartesian)

        }


    }

    fun fillBarChart(tableReport: TableReport, anyChartView: AnyChartView) {
        val yFields = tableReport.yFields
        if (yFields?.isNotEmpty() == true) {
            val barChart = AnyChart.bar()

            barChart.animation(true)

            barChart.padding(10.0, 20.0, 30.0, 20.0)

            barChart.yScale().stackMode(ScaleStackMode.VALUE)

            barChart.yAxis(0).labels().format(
                "function() {\n" +
                        "    return Math.abs(this.value).toLocaleString();\n" +
                        "  }"
            )

            barChart.yAxis(0.0).title(" ")

            barChart.xAxis(0.0).overlapMode(LabelsOverlapMode.NO_OVERLAP)

//            val xAxis1: Linear = barChart.xAxis(1.0)
//            xAxis1.enabled(true)
//            xAxis1.orientation(Orientation.RIGHT)
//            xAxis1.overlapMode(LabelsOverlapMode.ALLOW_OVERLAP)

            barChart.title(tableReport.title)

            barChart.interactivity().hoverMode(HoverMode.BY_X)

            barChart.tooltip()
                .title(true)
                .separator(true)
                .displayMode(TooltipDisplayMode.UNION)
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
            val seriesData: MutableList<DataEntry> = ArrayList()
            val chartData = tableReport.chartData


            val xValueSlugTitleList = mapXYValSlug(tableReport.xField!!)
            xValueSlugTitleList.sortedBy {
                it.slug
            }

            val xFieldSlug = tableReport.xField?.slug
            xValueSlugTitleList.forEach { xyModel ->
                Timber.e("xValueSlugTitleListItm $xyModel")

                chartData?.filter { hashMap ->
                    val gson = Gson();
                    val value = hashMap[xFieldSlug]?.value
                    Timber.e("value $value")

                    val toJson = gson.toJson(value)
                    val xFieldValue = gson.fromJson(toJson, XFieldValue::class.java)

                    Timber.e("xFieldValue $xFieldValue")
                    hashMap.containsKey(xFieldSlug) && xFieldValue.slug == xyModel.slug

                }?.let {
                    Timber.e("listsize ${it.size}")
                    if (it.isNotEmpty()) {
                        val countList = arrayListOf<Number>()
                        yFields.forEach { yField ->
                            var valueSum: Long = 0

                            val yFieldSlug = yField.slug
                            it.filter { hashMap ->
                                hashMap.containsKey(yFieldSlug)

                            }.forEach { hashMap ->
                                val value = hashMap[yFieldSlug]?.value?.toString() ?: 0
                                Timber.e("value $value")


                                if (value.toString().isNotEmpty()) {
                                    val gson = Gson();

                                    val jsonString = gson.toJson(value)
                                    val yFieldValue =
                                        gson.fromJson(jsonString, Long::class.java)
                                    Timber.e("yFieldValue-> $yFieldValue")

                                    val count = if (yFieldValue is Long) {
                                        yFieldValue
                                    } else {
                                        0
                                    }

                                    valueSum += count

                                }

                            }


                            countList.add(valueSum)

                        }


                        seriesData.add(CustomDataEntry2(xyModel.title, countList.toList()))

                    }

                }


            }


            val set: Set = Set.instantiate()
            set.data(seriesData)


            for (i in 1..yFields.size) {
                val yModel = yFields[i - 1]
                val seriesMapping: Mapping = set.mapAs("{ x: 'x', value: 'value$i' }")

                val series1: Bar = barChart.bar(seriesMapping)
                series1.name(yModel.title)
                series1.tooltip()
                    .position("left")
                    .anchor(Anchor.RIGHT_CENTER)

            }


            barChart.legend().enabled(true)
            barChart.legend().inverted(true)
            barChart.legend().fontSize(13.0)
            barChart.legend().padding(0.0, 0.0, 20.0, 0.0)

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
            barChart.palette(rangeColors)

            anyChartView.setChart(barChart)

        }


    }

    fun fillPieChart(it: TableReport, pieChart: AnyChartView) {
        val pie = AnyChart.pie()
        pie.padding(10.0, 20.0, 30.0, 20.0)

        pie.setOnClickListener(object : ListenersInterface.OnClickListener(arrayOf("x", "value")) {
            override fun onClick(event: Event) {
                Toast.makeText(
                    context,
                    event.data["x"].toString() + ":" + event.data["value"],
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        val chartData = it.chartData

        val data: MutableList<DataEntry> = ArrayList()

        val yFields = it.yFields

        yFields?.forEach { field ->
            val fieldTitle = field.title
            val totalCount = calcTotalCount(null, field, chartData)
            data.add(ValueDataEntry(fieldTitle, totalCount))

        }

        val xValueSlugTitleList = mapXYValSlug(it.xField!!)
        xValueSlugTitleList.sortedBy {
            it.slug
        }


//
        yFields?.forEach { yField ->
            var valueSum: Long = 0

            val yFieldSlug = yField.slug

            chartData?.filter { hashMap ->
                hashMap.containsKey(yFieldSlug)

            }?.forEach { hashMap ->
                val value = hashMap[yFieldSlug]?.value?.toString() ?: 0

                if (value.toString().isNotEmpty()) {
                    val gson = Gson();

                    val jsonString = gson.toJson(value)
                    val yFieldValue =
                        gson.fromJson(jsonString, Long::class.java)

                    val count = if (yFieldValue is Long) {
                        yFieldValue
                    } else {
                        0
                    }

                    valueSum += count

                }

            }



            data.add(ValueDataEntry(yField.title, valueSum))

        }




        pie.data(data)
        pie.title(it.title)
        pieSetting(pie)

        pieChart.setChart(pie)

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


        pie.labels().position("outside")

        pie.legend().title().enabled(true)
        pie.legend().title()
            .text(" ")
            .padding(0.0, 0.0, 10.0, 0.0)

        pie.legend()
            .position("center-bottom")
            .itemsLayout(LegendLayout.HORIZONTAL)
            .align(Align.CENTER)

    }

     fun calcTotalCount(
        xValue: String?,
        field: Fields,
        chartData: List<HashMap<String, TableChartDatum>>?
    ): Int? {
        var totalCount = 0
        when (field.type) {

            YESNO -> {
                totalCount = findYCount("no", chartData, xValue) + findYCount(
                    "yes",
                    chartData,
                    xValue
                )

            }
            MULTI_SELECT -> {

                field.choice_items?.forEach { choice ->
                    totalCount += findYCount(choice.slug, chartData, xValue)
                }


            }
            DROPDOWN -> {

                field.choice_items?.forEach { choice ->
                    totalCount += findYCount(choice.slug, chartData, xValue)

                }


            }
            SINGLE_SELECT -> {

                field.choice_items?.forEach { choice ->
                    totalCount += findYCount(choice.slug, chartData, xValue)

                }


            }
            RATING -> {
                when (field.sub_type) {
                    nps -> {
                        for (item in 0..10) {
                            totalCount += findYCount(item.toString(), chartData, xValue)

                        }
                    }
                    star -> {
                        for (item in 1..5) {
                            totalCount += findYCount(item.toString(), chartData, xValue)

                        }
                    }
                    Like_Dislike -> {
                        totalCount = findYCount("-1", chartData, xValue) + findYCount(
                            "1",
                            chartData,
                            xValue
                        )
                    }
                }

            }
            else -> {
                totalCount += findYCount(field.slug, chartData, xValue)
            }
        }

        return totalCount

    }

    private fun findYCount(
        slug: String?,
        chartData: List<HashMap<String, TableChartDatum>>?,
        _xValue: String?
    ): Int {
        var count = 0
//        chartData?.filterList {
//            yValue == slug && (_xValue == null || xValue == _xValue)
//        }?.forEach {
//            count += it.count ?: 0
//        }

        return count
    }

    private fun mapXYValSlug(field: Fields): ArrayList<XYModel> {
        val xyList = arrayListOf<XYModel>()

        val choicesList = field.choice_items ?: arrayListOf()
        when (field.type) {

            YESNO -> {
                xyList.add(XYModel("yes", "Yes"))
                xyList.add(XYModel("no", "No"))
            }
            MULTI_SELECT -> {
                xyList.addAll(fillXAxisListByChartItems(choicesList))
            }
            DROPDOWN -> {
                xyList.addAll(fillXAxisListByChartItems(choicesList))

            }
            SINGLE_SELECT -> {
                xyList.addAll(fillXAxisListByChartItems(choicesList))

            }
            RATING -> {
                when (field.sub_type) {
                    nps -> {
                        fillXAxisListByNPS()

                    }
                    star -> {
                        fillXAxisListByCSAT()

                    }
                    Like_Dislike -> {
                        xyList.add(XYModel("1", "Like"))
                        xyList.add(XYModel("-1", "Dislike"))
                    }
                }

            }
            else -> {
                xyList.add(XYModel(field.slug ?: "", field.title ?: ""))

            }
        }


        return xyList
    }

    private fun fillXAxisListByChartItems(choiceItems: java.util.ArrayList<ChoiceItem>): ArrayList<XYModel> {
        val xAxisList = arrayListOf<XYModel>()
        choiceItems.forEach { item ->
            xAxisList.add(XYModel(item.slug ?: "", item.title ?: ""))

        }
        return xAxisList
    }

    private fun fillXAxisListByNPS(): ArrayList<XYModel> {
        val xAxisList = arrayListOf<XYModel>()

        for (i in 0 until 11) {
            xAxisList.add(XYModel("$i", "NPS($i)"))
        }
        return xAxisList

    }

    private fun fillXAxisListByCSAT(): ArrayList<XYModel> {
        val xAxisList = arrayListOf<XYModel>()

        for (i in 0 until 5) {
            xAxisList.add(XYModel("$i", "NPS($i)"))
        }
        return xAxisList

    }

}

val colors= arrayListOf<String>(
    "#F82B60",
    "#FF9EB7",
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
    "#D74D26",
)
val colorsArray= arrayOf("#F82B60",
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


