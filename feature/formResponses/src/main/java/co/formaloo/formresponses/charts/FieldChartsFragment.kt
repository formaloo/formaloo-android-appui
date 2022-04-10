package co.formaloo.formresponses.charts

import android.os.Bundle
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
import kotlinx.android.synthetic.main.fragment_charts.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber
import java.util.*

class FieldChartsFragment : Fragment(), KoinComponent {
    private val baseMethod: BaseMethod by inject()
    val boardVM: BoardsViewModel by viewModel()

    private var stat = false
    lateinit var chartAdapter: StatsAdapter
    private var blockSlug: String? = null


    companion object {
        fun newInstance(blockItemSlug: String?,blockSlug:String?) = FieldChartsFragment().apply {
            arguments = Bundle().apply {
                putString(SLUG, blockSlug)
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.e("onCreate")

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_fields_charts, container, false).also {


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
        boardVM.retrieveSharedBlockDetail(BuildConfig.APPUI_ADDRESS,blockSlug?:"")

        boardVM.block.observe(viewLifecycleOwner) {
            it?.let {
                hideProgress()

                it.form?.let { it ->
                    it.stats?.let { stats ->
                        stats.fields?.let {
                            chartAdapter.setCollection(it)

                        }



                    }

                }

            }
        }
        boardVM.failure.observe(viewLifecycleOwner){
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
    private fun hideProgress() {
        progress_wheel.invisible()
    }

}
