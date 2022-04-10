package co.formaloo.formresponses.reportMaker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import co.formaloo.common.BaseMethod
import co.formaloo.common.BuildConfig
import co.formaloo.common.Constants
import co.formaloo.common.Constants.DROPDOWN
import co.formaloo.common.Constants.Like_Dislike
import co.formaloo.common.Constants.MULTI_SELECT
import co.formaloo.common.Constants.RATING
import co.formaloo.common.Constants.SINGLE_SELECT
import co.formaloo.common.Constants.SLUG
import co.formaloo.common.Constants.YESNO
import co.formaloo.common.Constants.nps
import co.formaloo.common.Constants.star
import co.formaloo.formCommon.vm.BoardsViewModel
import co.formaloo.formresponses.R
import co.formaloo.formresponses.charts.adapter.StatsAdapter
import co.formaloo.model.form.ChoiceItem
import co.formaloo.model.form.Fields
import co.formaloo.model.reportmaker.combinedChart.CombinedReport
import co.formaloo.model.reportmaker.combinedChart.detail.ReportDetailRes
import com.anychart.AnyChart
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.enums.SunburstCalculationMode
import com.anychart.enums.TreeFillingMethod
import com.anychart.graphics.vector.text.HAlign
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.fragment_report.*
import okhttp3.internal.filterList
import org.json.JSONException
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.IOException
import java.io.InputStream


class SunsetFragment : Fragment(), KoinComponent {
    private val baseMethod: BaseMethod by inject()
    val boardVM: BoardsViewModel by viewModel()

    private var stat = false
    lateinit var chartAdapter: StatsAdapter
    private var blockSlug: String? = null


    companion object {
        fun newInstance(blockItemSlug: String?, blockSlug: String?) = SunsetFragment().apply {
            arguments = Bundle().apply {
                putString(SLUG, blockSlug)
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_report, container, false).also {

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkBundle()
        initViews()
        initDatas()
//        setUpListeners()
    }

    private fun setUpListeners() {

//        anyChartView.setProgressBar(findViewById(R.id.progress_bar))

        val sunburst = AnyChart.sunburst()

        val data: MutableList<DataEntry> = ArrayList()
        data.add(SunsetCustomDataEntry("USA", "usa"))
        data.add(SunsetCustomDataEntry("sales", "usa-sales", "usa"))
        data.add(SunsetCustomDataEntry("Executive", "usa-executive", "usa-sales", 10))
        data.add(SunsetCustomDataEntry("Analyst", "usa-analyst", "usa-sales", 15))
        data.add(SunsetCustomDataEntry("Technical", "usa-technical", "usa"))
        data.add(SunsetCustomDataEntry("Testers", "usa-testers", "usa-technical", 15))
        data.add(SunsetCustomDataEntry("Developers", "usa-developers", "usa-technical"))
        data.add(SunsetCustomDataEntry("Frontend", "usa-frontend", "usa-developers"))
        data.add(SunsetCustomDataEntry("iOS", "usa-ios", "usa-developers", 12))
        data.add(SunsetCustomDataEntry("Vue", "vue", "usa-frontend", 7))
        data.add(SunsetCustomDataEntry("Angular", "angular", "usa-frontend", 12))
        data.add(SunsetCustomDataEntry("React", "react", "usa-frontend", 23))
        data.add(SunsetCustomDataEntry("Ember", "ember", "usa-frontend", 5))
        data.add(SunsetCustomDataEntry("Management", "usa-management", "usa", 37))
        data.add(SunsetCustomDataEntry("Germany", "germany"))
        data.add(SunsetCustomDataEntry("Technical", "germany-technical", "germany"))
        data.add(SunsetCustomDataEntry("Testers", "germany-testers", "germany-technical", 15))
        data.add(SunsetCustomDataEntry("Developers", "germany-developers", "germany-technical"))
        data.add(SunsetCustomDataEntry("Android", "germany-android", "germany-developers", 8))
        data.add(SunsetCustomDataEntry("Backend", "germany-backend", "germany-developers", 8))
        data.add(SunsetCustomDataEntry("Go", "go", "germany-backend", 8))
        data.add(SunsetCustomDataEntry("PHP", "php", "germany-backend", 12))
        data.add(SunsetCustomDataEntry("Python", "python", "germany-backend", 18))
        data.add(SunsetCustomDataEntry("HR", "germany-hr", "germany", 37))
        data.add(SunsetCustomDataEntry("Russia", "russia"))
        data.add(SunsetCustomDataEntry("Sales", "russia-sales", "russia"))
        data.add(SunsetCustomDataEntry("Technical", "russia-technical", "russia"))
        data.add(SunsetCustomDataEntry("Testers", "russia-testers", "russia-technical", 15))
        data.add(SunsetCustomDataEntry("Developers", "russia-developers", "russia-technical"))
        data.add(SunsetCustomDataEntry("Android", "russia-android", "russia-developers", 12))
        data.add(SunsetCustomDataEntry("Backend", "russia-backend", "russia-developers"))
        data.add(SunsetCustomDataEntry("Go", "go", "russia-backend", 4))
        data.add(SunsetCustomDataEntry("PHP", "php", "russia-backend", 7))
        data.add(SunsetCustomDataEntry("Python", "python", "russia-backend", 12))
        data.add(SunsetCustomDataEntry("Executive", "russia-executive", "russia-sales", 8))
        data.add(SunsetCustomDataEntry("Analyst", "russia-analyst", "russia-sales", 12))
        data.add(SunsetCustomDataEntry("China", "china"))
        data.add(SunsetCustomDataEntry("Sales", "china-sales", "china"))
        data.add(SunsetCustomDataEntry("Technical", "china-technical", "china"))
        data.add(SunsetCustomDataEntry("Testers", "china-testers", "china-technical", 39))
        data.add(SunsetCustomDataEntry("Developers", "china-developers", "china-technical"))
        data.add(SunsetCustomDataEntry("iOS", "china-ios", "china-developers", 17))
        data.add(SunsetCustomDataEntry("Android", "china-android", "china-developers", 25))
        data.add(SunsetCustomDataEntry("Backend", "china-backend", "china-developers"))
        data.add(SunsetCustomDataEntry("Go", "go", "china-backend", 14))
        data.add(SunsetCustomDataEntry("PHP", "php", "china-backend", 6))
        data.add(SunsetCustomDataEntry("Python", "python", "china-backend", 17))
        data.add(SunsetCustomDataEntry("Executive", "china-executive", "china-sales", 35))
        data.add(SunsetCustomDataEntry("Analyst", "china-analyst", "china-sales", 15))
        data.add(SunsetCustomDataEntry("Frontend", "china-frontend", "china-developers"))
        data.add(SunsetCustomDataEntry("Vue", "vue", "china-frontend", 27))
        data.add(SunsetCustomDataEntry("Angular", "angular", "china-frontend", 21))
        data.add(SunsetCustomDataEntry("React", "react", "china-frontend", 29))
        data.add(SunsetCustomDataEntry("Ember", "ember", "china-frontend", 15))
        data.add(SunsetCustomDataEntry("Meteor", "meteor", "china-frontend", 12))


        // TODO data
        sunburst.data(data, TreeFillingMethod.AS_TABLE)

        sunburst.calculationMode(SunburstCalculationMode.ORDINAL_FROM_LEAVES)

        sunburst.labels().hAlign(HAlign.CENTER)

        sunburst.palette(arrayOf("#0288d1", "#d4e157", "#ff6e40", "#f8bbd0"))

        sunburst.fill(
            "function () {" +
                    "return this.parent ? anychart.color.darken(this.parentColor, 0.15) : this.mainColor" +
                    "}"
        )

        sunburst.tooltip().format("Employee: {%leavesSum}")

        any_chartView.setChart(sunburst)

    }

    private fun initDatas() {
        boardVM.retrieveSharedBlockDetail(BuildConfig.APPUI_ADDRESS, blockSlug ?: "")

        boardVM.block.observe(viewLifecycleOwner) {
            it?.let {
                it.form?.let { it ->

                }

            }
        }
        boardVM.failure.observe(viewLifecycleOwner) {
            it?.let {
                boardVM.retrieveBlockFromDB(blockSlug ?: "")

            }

        }

    }

    private fun loadJSONFromAsset(): String? {

        return null
    }

    private fun checkBundle() {
        arguments?.let { it ->
            it.getString(SLUG)?.let {
                blockSlug = it
            }
        }
    }

    private fun initViews() {
        loadJSONFromAsset()?.let { loadJSONFromAsset ->


            try {

                val mJson: JsonElement = JsonParser.parseString(loadJSONFromAsset)
                val gson = Gson()
                val reportRes: ReportDetailRes = gson.fromJson(mJson, ReportDetailRes::class.java)

                (reportRes as ReportDetailRes).data?.report?.let {
                    fillPieChart(it)

                }


            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

    }

    private fun fillPieChart(it: CombinedReport) {
        val sunburst = AnyChart.sunburst()

        val chartData = it.chartData

        val data: MutableList<DataEntry> = ArrayList()
        it.yFields?.forEach { field ->
            val fieldSlug = field.slug


            var totalCount = 0

            when (field.type) {

                YESNO -> {
                    var noCount = 0
                    chartData?.filterList {
                        yValue == "no"
                    }?.forEach {
                        noCount += it.count ?: 0
                    }
                    data.add(SunsetCustomDataEntry("NO", "no", fieldSlug, noCount))

                    var yesCount = 0
                    chartData?.filterList {
                        yValue == "yes"
                    }?.forEach {
                        yesCount += it.count ?: 0
                    }
                    data.add(SunsetCustomDataEntry("YES", "yes", fieldSlug, yesCount))

                    totalCount = noCount + yesCount
                }
                MULTI_SELECT -> {

                    field.choice_items?.forEach { choice ->
                        var cCount = 0
                        val slug = choice.slug
                        val title = choice.title

                        chartData?.filterList {
                            yValue == slug
                        }?.forEach {
                            cCount += it.count ?: 0
                        }
                        data.add(SunsetCustomDataEntry(title, slug, fieldSlug, cCount))


                        totalCount += cCount
                    }


                }
                DROPDOWN -> {

                    field.choice_items?.forEach { choice ->
                        var cCount = 0
                        val slug = choice.slug
                        val title = choice.title

                        chartData?.filterList {
                            yValue == slug
                        }?.forEach {
                            cCount += it.count ?: 0
                        }
                        data.add(SunsetCustomDataEntry(title, slug, fieldSlug, cCount))

                        totalCount += cCount
                    }


                }
                SINGLE_SELECT -> {

                    field.choice_items?.forEach { choice ->
                        var cCount = 0
                        val slug = choice.slug
                        val title = choice.title

                        chartData?.filterList {
                            yValue == slug
                        }?.forEach {
                            cCount += it.count ?: 0
                        }
                        data.add(SunsetCustomDataEntry(title, slug, fieldSlug, cCount))

                        totalCount += cCount
                    }


                }
                RATING -> {
                    when (field.sub_type) {
                        nps -> {

                        }
                        star -> {

                        }
                        Like_Dislike -> {
                            var disCount = 0
                            chartData?.filterList {
                                yValue == "-1"
                            }?.forEach {
                                disCount += it.count ?: 0
                            }
                            data.add(SunsetCustomDataEntry("DisLike", "-1", fieldSlug, disCount))

                            var likeCount = 0
                            chartData?.filterList {
                                yValue == "1"
                            }?.forEach {
                                likeCount += it.count ?: 0
                            }
                            data.add(SunsetCustomDataEntry("Like", "1", fieldSlug, likeCount))

                            totalCount = disCount + likeCount
                        }
                    }

                }
                else -> {
                    var count = 0
                    chartData?.filterList {
                        yValue == fieldSlug
                    }?.forEach {
                        count += it.count ?: 0
                    }

                    data.add(SunsetCustomDataEntry(field.title, fieldSlug, null, count))

                    totalCount += count
                }
            }

            data.add(SunsetCustomDataEntry(field.title, fieldSlug, null, totalCount))

        }


        sunburst.data(data, TreeFillingMethod.AS_TABLE)

        sunburst.calculationMode(SunburstCalculationMode.ORDINAL_FROM_LEAVES)

        sunburst.labels().hAlign(HAlign.CENTER)

        sunburst.palette(arrayOf("#0288d1", "#d4e157", "#ff6e40", "#f8bbd0"))

        sunburst.fill(
            "function () {" +
                    "return this.parent ? anychart.color.darken(this.parentColor, 0.15) : this.mainColor" +
                    "}"
        )

        sunburst.tooltip().format("Employee: {%leavesSum}")

        any_chartView.setChart(sunburst)
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

    private fun fillChart(report: CombinedReport) {

        val totalYModel = arrayListOf<XYModel>()
        report.yFields?.forEach { item ->
            val model = mapXYValSlug(item)
            totalYModel.addAll(model)

        }

        val data: MutableList<DataEntry> = ArrayList()

        totalYModel.forEach { item ->
            data.add(SunsetCustomDataEntry("USA", "usa"))

        }

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

private class SunsetCustomDataEntry : DataEntry {
    constructor(name: String?, id: String?) {
        setValue("name", name)
        setValue("id", id)
    }

    constructor(name: String?, id: String?, parent: String?) {
        setValue("name", name)
        setValue("id", id)
        setValue("parent", parent)
    }

    constructor(name: String?, id: String?, parent: String?, value: Int) {
        setValue("name", name)
        setValue("id", id)
        setValue("parent", parent)
        setValue("value", value)
    }
}
