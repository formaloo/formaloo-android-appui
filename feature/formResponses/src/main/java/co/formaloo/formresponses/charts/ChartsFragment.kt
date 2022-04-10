package co.formaloo.formresponses.charts

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import co.formaloo.common.BaseMethod
import co.formaloo.common.BuildConfig
import co.formaloo.common.Constants
import co.formaloo.formresponses.R
import co.formaloo.common.Constants.SLUG
import co.formaloo.common.extension.invisible
import co.formaloo.formCommon.vm.BoardsViewModel
import co.formaloo.formresponses.charts.adapter.StatsAdapter
import co.formaloo.model.form.stat.StatCount
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.charts.Cartesian
import com.anychart.core.cartesian.series.Line
import com.anychart.data.Mapping
import com.anychart.data.Set
import com.anychart.enums.*
import com.anychart.graphics.vector.Stroke
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import kotlinx.android.synthetic.main.fragment_charts.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChartsFragment : Fragment(), KoinComponent {
    private val baseMethod: BaseMethod by inject()
    val boardVM: BoardsViewModel by viewModel()

    private var stat = false
    lateinit var chartAdapter: StatsAdapter
    private var blockSlug: String? = null


    companion object {
        fun newInstance(blockItemSlug: String?, blockSlug: String?) = ChartsFragment().apply {
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
        return inflater.inflate(R.layout.fragment_charts, container, false).also {

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkBundle()
        initViews()
        initDatas()
        setUpListeners()
    }

    private fun setUpListeners() {


    }

    private fun initDatas() {
        boardVM.retrieveSharedBlockDetail(BuildConfig.APPUI_ADDRESS, blockSlug ?: "")

        boardVM.block.observe(viewLifecycleOwner) {
            it?.let {
                hideProgress()
                it.form?.let { it ->
                    it.stats?.let { stats ->
//                        stats.track_data?.let { trackData ->
//                            trackData.total?.let { total ->
//                                fillStatViews(
//                                    total,
//                                    visit_line_chart,
//                                    "Views over time"
//                                    ,"View"
//
//                                )
//
//                            }
//                        }
//
//                        stats.submits?.let { submits ->
//                            Timber.e("submits $submits")
//                            fillStatViews(
//                                submits,
//                                submit_line_chart,
//                                "Responses over time"
//                                ,"Responses"
//                            )
//                        }

                        stats.fields?.let {
                            chartAdapter.setCollection(it)

                        }


                    }

                }

            }
        }

        boardVM.failure.observe(viewLifecycleOwner) {
            it?.let {
                boardVM.retrieveBlockFromDB(blockSlug ?: "")

            }

        }

    }

    private fun initViews() {
        chartAdapter = StatsAdapter()

        chart_frag.rotationY = 0f

        chart_rv.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = chartAdapter
        }

    }

    private fun checkBundle() {
        arguments?.let { it ->
            it.getString(SLUG)?.let {
                blockSlug = it
            }
        }
    }


    private fun fillStatViews(
        submits: List<StatCount>,
        lineChart: AnyChartView,
        title: String,
        label: String
    ) {
        Timber.e("fillStatViews $title")
        val cartesian: Cartesian = AnyChart.line()

        cartesian.animation(true)

        cartesian.padding(10.0, 20.0, 5.0, 20.0)

        cartesian.crosshair().enabled(true)
        cartesian.crosshair()
            .xLabel(true)
            .yLabel(true)
            .yStroke(null as Stroke?, null, null, null as String?, null as String?)

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT)
            .displayMode(TooltipDisplayMode.SINGLE)

        cartesian.title(title)

        cartesian.yAxis(0).title(label)
        cartesian.xAxis(0).labels().padding(5.0, 5.0, 5.0, 5.0)

        val seriesData: MutableList<DataEntry> = ArrayList()

        for (i in submits.indices) {
            val statCount = submits[i]
            val date = statCount.date ?: ""
            val count = statCount.count
            val stringDate = getStringDate(date)
            seriesData.add(ValueDataEntry(stringDate, count))

        }


        val set: Set = Set.instantiate()
        set.data(seriesData)

        val seriesMapping: Mapping = set.mapAs("{ x: 'x', value: 'value' }")
        val line: Line = cartesian.line(seriesMapping)

        line.name(label)
        line.hovered().markers().enabled(true)
        line.hovered().markers()
            .type(MarkerType.CIRCLE)
            .size(4.0)

        line.tooltip()
            .position("right")
            .anchor(Anchor.LEFT_CENTER)
            .offsetX(5.0)
            .offsetY(5.0)

        lineChart.setChart(cartesian)


    }


    private fun getStringDate(local_created_at: String): String {
        var d = ""

        val locale = Locale("fa_IR@calendar=persian")

        val format = SimpleDateFormat("yyyy-MM-dd", locale)
        try {
            val date = format.parse(local_created_at)

            if (date != null) {

            }

        } catch (e: ParseException) {
            Log.e("", "onBindViewHolder: $e")
        }

        return d
    }

    private fun removeYear(s: String): String {
        return if (s.isNotEmpty() && s.length > 4) {
            s.replace(s.substring(0, 5), "")
        } else {
            s
        }
    }

    private fun setChartLineDateLabels(
        labels: List<String>,
        lineChart: LineChart
    ) {

        val formatter = IAxisValueFormatter { value, axis ->
            var s = " "
            try {
                if (labels.size > value.toInt())
                    s = labels[value.toInt()]

            } catch (e: Exception) {
            }

            s
        }

        val xAxis = lineChart.xAxis
        xAxis.granularity = 1f // minimum axis-step (interval) is 1
        xAxis.valueFormatter = formatter
        xAxis.position = XAxis.XAxisPosition.BOTTOM

    }

    private fun checkMapInstanceOfMap(itemsMap: Map<String, Any>): Boolean {

        val keyList = ArrayList((itemsMap as Map<*, *>).keys)

        val iterator = keyList.iterator()
        while (iterator.hasNext()) {
            val value = iterator.next()
            if (value == "type") {
                iterator.remove()
            }
        }

        return keyList.indices
            .filter { itemsMap.containsKey(keyList[it]) }
            .map { itemsMap[keyList[it]] }
            .any { it is Map<*, *> }
    }

    private fun hideProgress() {
        progress_wheel.invisible()
    }

}
