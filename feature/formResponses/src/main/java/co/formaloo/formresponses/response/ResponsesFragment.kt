package co.formaloo.formresponses.response

import android.app.Activity
import android.app.SearchManager
import android.content.Context.SEARCH_SERVICE
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.MutableState
import androidx.compose.ui.platform.ComposeView
import co.formaloo.common.BuildConfig
import co.formaloo.common.Constants
import co.formaloo.common.base.BaseFragment
import co.formaloo.common.base.BaseViewModel
import co.formaloo.common.extension.invisible
import co.formaloo.common.extension.visible
import co.formaloo.common.Constants.ERRORS
import co.formaloo.common.Constants.FORM_ERRORS
import co.formaloo.common.Constants.GENERAL_ERRORS
import co.formaloo.common.theme.ComposeTestTheme
import co.formaloo.formCommon.vm.BoardsViewModel
import co.formaloo.model.submit.Row
import co.formaloo.model.submit.row.RowDeatil
import co.formaloo.formCommon.vm.ResponsesViewModel
import co.formaloo.formresponses.R
import kotlinx.android.synthetic.main.fragment_sumbit.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber


class ResponsesFragment : BaseFragment() {
    private var rowStat: MutableState<Row>? = null
    private var blockSlug: String? = null
    private var stat = false
    val responseVM: ResponsesViewModel by viewModel()
    val boardsVM: BoardsViewModel by viewModel()


    override fun onDestroy() {
        viewModelStore.clear()
        super.onDestroy()
    }

    companion object {
        fun newInstance(blockItemSlug: String?,blockSlug: String?) = ResponsesFragment().apply {
            arguments = Bundle().apply {
                Timber.e("newInstance $blockSlug")
                putSerializable(Constants.SLUG, blockSlug)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkBundle()
        setHasOptionsMenu(true);


    }


    private fun initData() {
        boardsVM.retrieveSharedBlockDetail(BuildConfig.APPUI_ADDRESS,blockSlug?:"")

        responseVM.refreshPosts(BuildConfig.APPUI_ADDRESS, blockSlug ?: "", true)

        boardsVM.block.observe(viewLifecycleOwner) {
            it?.let {
            }
        }

        boardsVM.failure.observe(viewLifecycleOwner){
            it?.let {
                boardsVM.retrieveBlockFromDB(blockSlug ?: "")

            }
        }

    }

    override fun getViewModel(): BaseViewModel = responseVM

    override fun onResume() {
        super.onResume()

    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @ExperimentalAnimationApi
    @ExperimentalMaterialApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(inflater.context).apply {
        val snackbarHostState = SnackbarHostState()

        setContent {
            ComposeTestTheme {
                val scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState)
                // When the Home screen receives data with an error
                HomeScreen(
                    responseVM,
                    openRow = { row, formslug, rowStat ->
//                        openRow(row, formslug, rowStat)
                    },
                    scaffoldState = scaffoldState,
                )
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()

    }

    private fun checkBundle() {
        arguments?.let { it ->
            it.getString(Constants.SLUG)?.let {
                blockSlug = it
                block_slug = it
            }

        }
    }

    private fun renderFailure(message: String?) {
        Timber.e(message)
        err_txt.invisible()
        message?.let {
            try {

                var jObjError = JSONObject(message)
                val errors = baseMethod.getJSONObject(jObjError, ERRORS)
                val formErrors = baseMethod.getJSONObject(errors, FORM_ERRORS)
                val gErrors = baseMethod.getJSONArray(errors, GENERAL_ERRORS)

                Timber.e("formErrors $formErrors")
                Timber.e("gErrors $gErrors")
                var err = "$gErrors"
                if (err.contains("[\"")) {
                    err = err.replace("[\"", "")
                }
                if (err.contains("\"]")) {
                    err = err.replace("\"]", "")
                }

                err_txt.text = err
                err_txt.visible()
            } catch (e: Exception) {
                Timber.e("${e.localizedMessage}")

            }
        }

        if (!stat)
            displayEmptyPAge()
    }

    private fun displayEmptyPAge() {
        error_img.visible()
        empty_submit_list.invisible()
    }

    private var searchView: SearchView? = null
    private var queryTextListener: SearchView.OnQueryTextListener? = null

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.submit_menu, menu)
        val searchItem: MenuItem = menu.findItem(R.id.action_search)
        val searchManager = requireActivity().getSystemService(SEARCH_SERVICE) as SearchManager
        searchView = searchItem.actionView as SearchView?
        if (searchView != null) {
            searchView?.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))
            queryTextListener = object : SearchView.OnQueryTextListener {
                override fun onQueryTextChange(newText: String): Boolean {
                    if (newText.isEmpty()) {
                        responseVM.resetRetreiveResponses()
                        resetData()
                        responseVM.refreshPosts(BuildConfig.APPUI_ADDRESS, blockSlug ?: "", true)
                    } else if (newText.length > 2) {
                        responseVM.resetRetreiveResponses()
                        resetData()
                        responseVM.initRowQuery(newText)
                        responseVM.refreshPosts(BuildConfig.APPUI_ADDRESS, blockSlug ?: "", true)

                    }
                    Timber.i(newText)
                    return true
                }

                override fun onQueryTextSubmit(query: String): Boolean {
                    Timber.i(query)
                    return true
                }
            }
            searchView?.setOnQueryTextListener(queryTextListener)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search -> {               // Not implemented here
                return true
            }

        }
        searchView?.setOnQueryTextListener(queryTextListener)
        return false
    }

    var resultLauncherSetting = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Timber.e("resultLauncherSetting")
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data
            if (data?.hasExtra("row") == true) {
                val row = data.getSerializableExtra("row") as RowDeatil
                lastClickedRow?.row_tags = row.row_tags
                lastClickedRow?.let {
                    rowStat?.value = it
                }
            }
        }
    }


}

