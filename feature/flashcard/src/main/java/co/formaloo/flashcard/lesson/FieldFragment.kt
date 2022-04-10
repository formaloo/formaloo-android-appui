package co.formaloo.flashcard.lesson

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.formaloo.common.BuildConfig
import co.formaloo.common.Constants
import co.formaloo.common.base.BaseFragment
import co.formaloo.common.base.BaseViewModel
import co.formaloo.common.openIntent
import co.formaloo.flashcard.databinding.LayoutSingleFieldBinding
import co.formaloo.flashcard.lesson.adapter.LessonFieldsAdapter
import co.formaloo.flashcard.lesson.listener.LessonListener
import co.formaloo.flashcard.lesson.listener.SwipeStackListener
import co.formaloo.formCommon.vm.SharedViewModel
import co.formaloo.flashcard.viewmodel.UIViewModel
import co.formaloo.formCommon.FullScreenWebViewActivity
import co.formaloo.formCommon.vm.BoardsViewModel
import timber.log.Timber
import org.koin.androidx.viewmodel.ext.android.viewModel
import co.formaloo.common.extension.invisible
import co.formaloo.common.extension.visible
class FieldFragment : BaseFragment(), LessonListener {

    private lateinit var binding: LayoutSingleFieldBinding
    private val shardedVM: SharedViewModel by viewModel()
    private val viewModel: UIViewModel by viewModel()
    private val boardVM: BoardsViewModel by viewModel()
    private var fieldsFlashAdapter: LessonFieldsAdapter? = null
    private var form: co.formaloo.model.form.Form? = null
    private var fields: ArrayList<co.formaloo.model.form.Fields> = arrayListOf()

    private var formSlug: String? = null
    private var blockSlug: String? = null

    companion object {
        fun newInstance(blockItemSlug: String?,blockSlug: String?) = FieldFragment().apply {
            arguments = Bundle().apply {
                Timber.e("newInstance $blockSlug")
                putSerializable(Constants.SLUG, blockSlug)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkBundle()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutSingleFieldBinding.inflate(inflater, container, false)
        binding.flashcardListener = this
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()

    }

    override fun getViewModel(): BaseViewModel = viewModel


    private fun checkBundle() {
        arguments?.let {
            it.getString("slug")?.let {
                blockSlug = it
            }
        }
    }

    private fun initView() {
        fieldsFlashAdapter = LessonFieldsAdapter(
            this@FieldFragment,
            object : SwipeStackListener {
                override fun onSwipeEnd(position: Int) {
                    next()
                }
            },
            form!!, viewModel,
        )

        binding.flashcardFieldsRec.apply {
            adapter = fieldsFlashAdapter
            layoutManager =
                object : LinearLayoutManager(context, RecyclerView.HORIZONTAL, false) {
                    override fun canScrollVertically(): Boolean {
                        return false
                    }

                    override fun canScrollHorizontally(): Boolean {
                        return false
                    }

                }

        }

        binding.activityFlashCard.visible()
        fieldsFlashAdapter?.collection=form?.fields_list?: arrayListOf()
    }

    private fun initData() {
        boardVM.retrieveSharedBlockDetail(BuildConfig.APPUI_ADDRESS, blockSlug ?: "")



        boardVM.block.observe(viewLifecycleOwner) {
            it?.form?.let {
                binding.form = it
                form = it
                it.fields_list?.let {
                    this.fields = it
                }
                formSlug = form?.slug ?: ""

                viewModel.initFormSlug(formSlug ?: "")
//                viewModel.getSubmitEntity()

                initView()

                binding.form = form
                binding.executePendingBindings()

            }

        }
        boardVM.failure.observe(viewLifecycleOwner) {
            it?.let {
                boardVM.retrieveBlockFromDB(blockSlug ?: "")

            }
        }

    }

    override fun next() {}

    override fun pre() {

    }

    override fun openFullScreen(field: co.formaloo.model.form.Fields, link: String) {

        val intent= Intent(requireContext(),FullScreenWebViewActivity::class.java)
        intent.putExtra("link",link)
        intent.putExtra("title",field.title)
        openIntent(requireActivity(),intent)
//        binding.closeFullBtn.setOnClickListener {
//            closeFulPage()
//        }
//
//
//        val webview = binding.videoviewfull
//
//        webview.settings.userAgentString = "Android"
//        webview.settings.loadWithOverviewMode = true
//        webview.settings.setJavaScriptEnabled(true)
//        webview.settings.useWideViewPort = true
//        webview.settings.databaseEnabled = true
//        webview.settings.allowContentAccess = true
//        webview.settings.allowFileAccessFromFileURLs = true
//        webview.settings.domStorageEnabled = true
//        webview.settings.allowFileAccess = true
//        webview.settings.setGeolocationEnabled(true)
//        webview.settings.setAppCacheEnabled(true)
//        webview.settings.setSupportMultipleWindows(true)
//        webview.settings.cacheMode = WebSettings.LOAD_DEFAULT
//        webview.setInitialScale(10)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            webview.webChromeClient = WebChromeClient()
//        }
//
//        webview.loadUrl(link)
//        webview.webViewClient = WebViewClient()
//
////        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
//
//        binding.fullLay.visible()
    }

    override fun closePage() {
        requireActivity().onBackPressed()
        binding.videoviewfull.onPause()
    }


    private fun closeFulPage() {

//        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding.fullLay.invisible()

    }

    override fun share() {

    }


}
