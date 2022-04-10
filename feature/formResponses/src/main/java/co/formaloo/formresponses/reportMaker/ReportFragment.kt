package co.formaloo.formresponses.reportMaker

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import co.formaloo.common.BuildConfig
import co.formaloo.common.Constants
import co.formaloo.common.Constants.SLUG
import co.formaloo.formCommon.vm.BoardsViewModel
import co.formaloo.formresponses.R
import co.formaloo.model.boards.ChartChoices
import kotlinx.android.synthetic.main.fragment_report.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent


class ReportFragment : Fragment(), KoinComponent {
    val boardVM: BoardsViewModel by viewModel()

    private var blockSlug: String? = null


    companion object {
        fun newInstance(blockItemSlug: String?, blockSlug: String?) = ReportFragment().apply {
            arguments = Bundle().apply {
                putString(SLUG, blockSlug)
            }
        }

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


    private fun initDatas() {
        boardVM.retrieveSharedBlockDetail(BuildConfig.APPUI_ADDRESS, blockSlug ?: "")

        boardVM.block.observe(viewLifecycleOwner) { it ->
            it?.let {block->
                it.report?.let {
                    val reportCharts = TableReportCharts(requireContext())

                    when ( it.chartType) {
                        ChartChoices.BAR_CHART.slug -> {
                            reportCharts.fillBarChart(it, any_chartView)

                        }
                        ChartChoices.PIE_CHART.slug -> {
                            reportCharts.fillPieChart(it, any_chartView)


                        }
                        ChartChoices.GANTT_CHART.slug -> {
                            reportCharts.fillLineChart(it, any_chartView)

                        }
                        ChartChoices.LINE_CHART.slug -> {
                            reportCharts.fillLineChart(it, any_chartView)

                        }
                        else -> {
                            reportCharts.fillLineChart(it, any_chartView)


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

    private fun checkBundle() {
        arguments?.let { it ->
            it.getString(SLUG)?.let {
                blockSlug = it
            }
        }
    }

    private fun initViews() {

    }


}





