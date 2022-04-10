package co.formaloo.flashcard.lesson

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebViewClient
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.formaloo.common.BuildConfig
import co.formaloo.common.Constants
import co.formaloo.common.base.BaseFragment
import co.formaloo.common.base.BaseViewModel
import co.formaloo.flashcard.R
import co.formaloo.flashcard.databinding.ActivityLessonBinding
import co.formaloo.flashcard.lesson.adapter.LessonFieldsAdapter
import co.formaloo.flashcard.lesson.listener.LessonListener
import co.formaloo.flashcard.lesson.listener.SwipeStackListener
import co.formaloo.formCommon.vm.SharedViewModel
import co.formaloo.flashcard.viewmodel.UIViewModel
import co.formaloo.formCommon.vm.BoardsViewModel
import timber.log.Timber
import org.koin.androidx.viewmodel.ext.android.viewModel
import co.formaloo.common.extension.invisible
import co.formaloo.common.extension.visible
class LessonFragment : BaseFragment(), LessonListener {

    private lateinit var binding: ActivityLessonBinding
    private val shardedVM: SharedViewModel by viewModel()
    private val viewModel: UIViewModel by viewModel()
    private val boardVM: BoardsViewModel by viewModel()
    private var fieldsFlashAdapter: LessonFieldsAdapter? = null
    private var form: co.formaloo.model.form.Form? = null
    private var lessonsProgressMap = hashMapOf<String?, Int?>()
    private var fields: ArrayList<co.formaloo.model.form.Fields> = arrayListOf()

    private var formSlug: String? = null
    private var blockSlug: String? = null
    private var menuBlockItemSlug: String? = null

    companion object {
        fun newInstance(blockItemSlug: String?,blockSlug: String?) = LessonFragment().apply {
            arguments = Bundle().apply {
                Timber.e("newInstance $blockSlug")
                putSerializable(Constants.SLUG, blockSlug)
                putSerializable("menu_item_block_slug", blockItemSlug)
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
        binding = ActivityLessonBinding.inflate(inflater, container, false)
        binding.flashcardListener = this
        binding.layoutFashCong.flashcardListener = this
        binding.layoutFashCong.viewmodel = viewModel
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
            it.getString("menu_item_block_slug")?.let {
                menuBlockItemSlug = it
            }
        }
    }

    private fun initView() {
        fieldsFlashAdapter = LessonFieldsAdapter(
            this@LessonFragment,
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
                checkLessonProgress()

                binding.form = form
                binding.layoutFashCong.form = form
                binding.executePendingBindings()

            }

        }
        boardVM.failure.observe(viewLifecycleOwner) {
            it?.let {
                boardVM.retrieveBlockFromDB(blockSlug ?: "")

            }
        }

    }


    override fun next() {
        with(binding.flashcardFieldsRec) {
            val visibleItemPosition =
                (layoutManager as LinearLayoutManager).findLastVisibleItemPosition()

            viewModel.saveEditSubmitToDB(false, visibleItemPosition)

            val next = visibleItemPosition + 1
            updateLessonProgress(next)

            if (fields.size > next) {
                Handler(Looper.getMainLooper()).postDelayed({
                    scrollToPosition(next)
                }, Constants.SCROLL_DELAY)

                checkNeedActionFields(next)

            } else {
                openCongView()
            }

            binding.flashPreBtn.visible()

            binding.progress = next


            Timber.d("visibleItemPosition$visibleItemPosition")
        }

    }

    private fun checkNeedActionFields(pos: Int): Boolean {
        val type = fieldsFlashAdapter?.collection?.get(pos)?.type

        if (type == Constants.SINGLE_SELECT || type == Constants.DROPDOWN || type == Constants.YESNO || type == Constants.RATING || type == Constants.META) {
            binding.flashcardDoneBtn.invisible()

        } else {
            binding.flashcardDoneBtn.visible()

        }

        return false
    }

    override fun pre() {
        with(binding.flashcardFieldsRec) {
            val visibleItemPosition =
                (layoutManager as LinearLayoutManager).findLastVisibleItemPosition()


            updateLessonProgress(visibleItemPosition - 1)

            if (0 <= visibleItemPosition - 1) {
                scrollToPosition(visibleItemPosition - 1)

            }

            if (visibleItemPosition == 1) {
                binding.flashPreBtn.invisible()
            }

            binding.flashNextBtn.visible()
            binding.progress = visibleItemPosition
        }
    }

    override fun openFullScreen(field: co.formaloo.model.form.Fields, link: String) {
        binding.closeFullBtn.setOnClickListener {
            closeFulPage()
        }


        val webview = binding.videoviewfull

        webview.settings.userAgentString = "Android"
        webview.settings.loadWithOverviewMode = true
        webview.settings.setJavaScriptEnabled(true)
        webview.settings.useWideViewPort = true
        webview.settings.databaseEnabled = true
        webview.settings.allowContentAccess = true
        webview.settings.allowFileAccessFromFileURLs = true
        webview.settings.domStorageEnabled = true
        webview.settings.allowFileAccess = true
        webview.settings.setGeolocationEnabled(true)
        webview.settings.setAppCacheEnabled(true)
        webview.settings.setSupportMultipleWindows(true)
        webview.settings.cacheMode = WebSettings.LOAD_DEFAULT
        webview.setInitialScale(1)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            webview.webChromeClient = WebChromeClient()
        }


        webview.loadUrl(link)
        webview.webViewClient = WebViewClient()

        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        binding.fullLay.visible()
    }

    override fun closePage() {
        val fm = requireActivity().supportFragmentManager

        for (i in 0 until fm.backStackEntryCount) {
            fm.popBackStack()
        }
    }


    private fun closeFulPage() {
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding.fullLay.invisible()

    }

    override fun share() {
        val shareTxt =
            "I'm learning ${form?.title ?: ""} in ${
                getString(co.formaloo.common.R.string.app_name)
            }. Check it out on your phone: ${
                getString(R.string.appAddress)
            }"
        shareVia(shareTxt, getString(co.formaloo.common.R.string.app_name), requireContext())

    }

    fun shareVia(extraTxt: String, title: String, mContext: Context) {
        val sendIntent = Intent()
        sendIntent.type = "text/plain"
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, extraTxt)
        val createChooser = Intent.createChooser(sendIntent, title)
        createChooser.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        mContext.startActivity(createChooser)
    }

    private fun openCongView() {
        updateLessonProgress(-1)

//        shardedVM.saveLastBlock("")

        val visibleItemPosition =
            (binding.flashcardFieldsRec.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
        Timber.d("visibleItemPosition$visibleItemPosition")
        viewModel.saveEditSubmitToDB(true, visibleItemPosition)
        binding.flashCongView.visible()

    }

    private fun updateLessonProgress(pos: Int) {
        saveProgress(lessonsProgressMap, pos)
        savePercentage(pos)
    }

    private fun checkLessonProgress() {
        lessonsProgressMap = shardedVM.retrieveLessonProgress()

        val progress = lessonsProgressMap[menuBlockItemSlug?: ""]

        if (progress == null || progress == 0 || progress == -1) {
            saveProgress(lessonsProgressMap, 0)
            savePercentage(progress)


        } else {
            binding.flashPreBtn.visible()
            binding.flashcardFieldsRec.scrollToPosition(progress + 1)
        }

        if (progress ?: 0 == 0) {
            binding.flashPreBtn.invisible()

        }

        binding.progress = (progress ?: 0)
        binding.executePendingBindings()

    }

    private fun savePercentage(progress: Int?) {
        val p = progress ?: 0
        val processValue= if (p<0){
            0
        }else{
           p
        }


        val percentage = calcPercentage(processValue , form?.fields_list?.size ?: 1)
        val percentageMap = HashMap<String?, Int?>()
        percentageMap[menuBlockItemSlug] = percentage

        shardedVM.saveLessonPercentage(percentageMap)
    }

    private fun saveProgress(lessonsProgressMap: java.util.HashMap<String?, Int?>, progress: Int) {

        lessonsProgressMap[menuBlockItemSlug] = progress
        shardedVM.saveLessonProgress(lessonsProgressMap)

    }

    private fun calcPercentage(progress: Int, fieldsSize: Int): Int {
        return progress * 100 / fieldsSize
    }

}
