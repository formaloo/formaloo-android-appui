package co.formaloo.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.DataBindingUtil.setContentView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.*
import co.formaloo.boards.BoardFragment
import co.formaloo.boards.homeAdapers.BoardFragmentListener
import co.formaloo.boards.BuildConfig
import co.formaloo.boards.drawerAdapters.DrawerBlockItemsAdapter
import co.formaloo.common.Constants
import co.formaloo.common.isOnline
import co.formaloo.common.openUri
import co.formaloo.displayform.DisplayFormSinglePageFragment
import co.formaloo.formresponses.kanban.KanBanFragment
import co.formaloo.flashcard.MainListener
import co.formaloo.flashcard.lesson.FieldFragment
import co.formaloo.flashcard.lesson.LessonFragment
import co.formaloo.flashcard.worker.SubmitWorker
import co.formaloo.formCommon.BlockListener
import co.formaloo.formCommon.vm.BoardsViewModel
import co.formaloo.formCommon.vm.SharedViewModel
import co.formaloo.formresponses.charts.ChartsFragment
import co.formaloo.formresponses.charts.FieldChartsFragment
import co.formaloo.formresponses.reportMaker.ReportFragment
import co.formaloo.formresponses.response.ResponsesFragment
import co.formaloo.home.databinding.ActivityMainBinding
import co.formaloo.model.boards.BlockSubType
import co.formaloo.model.boards.BlockType
import co.formaloo.model.boards.FormDisplayType
import co.formaloo.model.boards.block.Block

import com.ethanhua.skeleton.RecyclerViewSkeletonScreen
import com.ethanhua.skeleton.Skeleton
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent

class HomeActivity : AppCompatActivity(), KoinComponent, MainListener, BlockListener {
    private lateinit var blocksAdapter: DrawerBlockItemsAdapter
    private var lastTime: Long = 0
    private var lastBoards = listOf<Block>()
    private lateinit var binding: ActivityMainBinding
    private val boardVM: BoardsViewModel by viewModel()
    private val shardedVM: SharedViewModel by viewModel()
    private var skeleton: RecyclerViewSkeletonScreen? = null

    private lateinit var drawerToggle: ActionBarDrawerToggle
    lateinit var fragmentHome: BoardFragment

    private var menuBlockSlug: String? = null

    val fm: FragmentManager = supportFragmentManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setContentView(this, R.layout.activity_main)
        setSupportActionBar(binding.appBarMain.toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        binding.lifecycleOwner = this
        setupDrawer()
        initView()
        addHomePage()
        setUpListeners()
        initData()

    }

    private fun initView() {

        binding.homeBtn.setOnClickListener {
            openMainPage()
        }
        blocksAdapter = DrawerBlockItemsAdapter(boardVM, object : BoardFragmentListener {
            override fun boardBlockClicked(blockItem: Block, view: ImageView, name: String) {
                shardedVM.saveLastBlock(blockItem.slug)

                openMenuBlockItem(blockItem, view, "imageView")
                fragmentHome.fillLastItemBlockType(blockItem)

            }

            override fun boardLinkClicked(blockItem: Block) {
                shardedVM.saveLastBlock(blockItem.slug)

                openLink(blockItem)
                fragmentHome.fillLastItemLinkType(blockItem)

            }

        })
        binding.categoryRv.apply {
            adapter = blocksAdapter
            layoutManager = LinearLayoutManager(this.context)
        }

        skeleton = Skeleton.bind(binding.categoryRv)
            .adapter(blocksAdapter)
            .load(co.formaloo.boards.R.layout.layout_block_itemskeleton)
            .color(co.formaloo.boards.R.color.static_item_light_gray)
            .count(15)
            .show()


    }

    private fun initData() {
        fetchLocalBlockList()

        boardVM.localBlockList.observe(this) { it ->
            it?.let { blockList ->
                if (blockList.isEmpty()) {
                    fetchBoardList()

                } else {

                    blockList.findLast {
                        it.type == BlockType.MENU.slug
                    }?.let {
                        boardVM.retrieveMenuBlockDetailFromDB(it.slug ?: "")
                    }

                }

            }
        }


        boardVM.menuBlock.observe(this) { it ->
            it?.let { block ->
                val menuItems = block?.items?.toList() ?: listOf()

                skeleton?.hide()

                if (lastBoards != menuItems) {
                    blocksAdapter.collection = menuItems.toList()
                }

                lastBoards = menuItems
            }
        }

        boardVM.rawMenuBlock.observe(this) { it ->
            it?.let { block ->
                val slug = block.slug
                menuBlockSlug = slug
                fetchMenuBlock(slug)
            }
        }

    }

    private fun fetchMenuBlock(menuSlug: String) {
        if (isOnline(this)) {
            boardVM.retrieveMenuBlockDetail(co.formaloo.common.BuildConfig.APPUI_ADDRESS, menuSlug)

        } else {
            boardVM.retrieveMenuBlockDetailFromDB(menuSlug)

        }

    }

    private fun addHomePage() {
        fragmentHome = BoardFragment()
        fragmentHome.blockListener = this
        fm.beginTransaction().add(R.id.main_container, fragmentHome, "fragmentHome").commit();
    }

    private fun setUpListeners() {


        binding.madeby.setOnClickListener {
            openFormaloo(it)
        }


        binding.navHeader.setOnClickListener {
            openMainPage()
        }

        binding.appBarMain.madeByTxv.setOnClickListener {
            openFormaloo(it)
        }

    }

    private fun setupDrawer() {
        drawerToggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            (R.string.app_name),
            (R.string.app_name)
        )

        binding.drawerLayout.addDrawerListener(drawerToggle)
        binding.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {


            }

            override fun onDrawerOpened(drawerView: View) {
                if (menuBlockSlug == null) {
                    fetchBoardList()

                } else {
                    fetchMenuBlock(menuBlockSlug ?: "")
                }

            }

            override fun onDrawerClosed(drawerView: View) {

            }

            override fun onDrawerStateChanged(newState: Int) {

            }

        })

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return drawerToggle.onOptionsItemSelected(item)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        drawerToggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: android.content.res.Configuration) {
        super.onConfigurationChanged(newConfig)
        drawerToggle.onConfigurationChanged(newConfig)

    }

    private fun fetchBoardList() {
        if (isOnline(this)) {
            boardVM.retrieveSharedBoardDetail(co.formaloo.common.BuildConfig.APPUI_ADDRESS)

        } else {
            fetchLocalBlockList()

        }
    }

    private fun fetchLocalBlockList() {
        boardVM.retrieveBlockListFromDB()

    }

    private fun openMainPage() {
        binding.drawerLayout.close()
        for (i in 0 until fm.backStackEntryCount) {
            fm.popBackStack()
        }
    }

    companion object {
        const val UPDATE_OFFSET_VALID = 3600 * 3000L
    }

    fun isValid(): Boolean = (System.currentTimeMillis() - lastTime) > UPDATE_OFFSET_VALID

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerVisible(GravityCompat.START)) {
            binding.drawerLayout.close()

        } else if (!fragmentHome.isVisible) {
            hideAllFragments(fm)
        } else {
            super.onBackPressed()

        }
    }

    private fun hideAllFragments(fm: FragmentManager) {
        val fragments = fm.fragments
        fragments.remove(fragmentHome)
        for (f in fragments) {
            fm.beginTransaction().hide(f).commit()

        }

    }

    override fun onResume() {
        super.onResume()
        callWorker()

    }

    fun callWorker() {
        val constraint: Constraints =
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

        val submitWorkRequest: OneTimeWorkRequest = OneTimeWorkRequestBuilder<SubmitWorker>()
            .setConstraints(constraint).build()

        val manager = WorkManager.getInstance(this)
        manager.enqueueUniqueWork("Submit", ExistingWorkPolicy.KEEP, submitWorkRequest);

    }

    override fun openMenuBlockItem(blockItem: Block, sharedView: View, transitionName: String) {
        shardedVM.saveLastBlock(blockItem.slug)

        fragmentHome.fillLastItemBlockType(blockItem)


        val subtype = blockItem.block?.subtype

        when (blockItem.block?.type) {

            BlockType.FORM_CHARTS.slug -> {
                when (subtype) {
                    BlockSubType.ALL_CHARTS.slug -> {
                        openAllChartBlock(blockItem, sharedView, transitionName)

                    }
                    BlockSubType.FIELDS_CHARTS.slug -> {
//                        openFieldChartBlock(blockItem, sharedView, transitionName)
                        openAllChartBlock(blockItem, sharedView, transitionName)
                    }
                    else -> {
                        openAllChartBlock(blockItem, sharedView, transitionName)

                    }
                }

            }
            BlockType.FORM_DISPLAY.slug -> {
                openDisplayBlock(blockItem, sharedView, transitionName)

            }
            BlockType.FORM_RESULT.slug -> {
                openResultBlock(blockItem, sharedView, transitionName)
            }

            BlockType.KANBAN.slug -> {
                openKanbanBlock(blockItem, sharedView, transitionName)
            }
            BlockType.REPORT.slug -> {
                openReportBlock(blockItem, sharedView, transitionName)
            }

        }


        binding.drawerLayout.close()

    }

    private fun openDisplayBlock(blockItem: Block, sharedView: View, transitionName: String) {

        val subtype = blockItem.block?.subtype
        val displayType = blockItem.block?.display_type


        when (displayType) {
            FormDisplayType.DISPLAY_MULTI_PAGE.slug -> {
                when (subtype) {
                    BlockSubType.DISPLAY_FIELDS.slug -> {
                        openFieldDisplay(blockItem, sharedView, transitionName)
                    }
                    BlockSubType.ALL_FIELDS.slug -> {
                        openMultiPageDisplay(blockItem, sharedView, transitionName)
                    }
                    else -> {
                        openMultiPageDisplay(blockItem, sharedView, transitionName)
                    }
                }

            }

            FormDisplayType.DISPLAY_SINGLE_PAGE.slug -> {
                when (subtype) {
                    BlockSubType.DISPLAY_FIELDS.slug -> {
                        openFieldDisplay(blockItem, sharedView, transitionName)
                    }
                    BlockSubType.ALL_FIELDS.slug -> {
                        openSinglePageDisplay(blockItem, sharedView, transitionName)
                    }
                    else -> {
                        openSinglePageDisplay(blockItem, sharedView, transitionName)
                    }
                }

            }

            FormDisplayType.DISPLAY_OPEN_WEB.slug -> {
                openWebViewDisplay(blockItem)
            }
            else -> {
                openMultiPageDisplay(blockItem, sharedView, transitionName)

            }
        }
    }

    private fun openWebViewDisplay(block: Block) {
        val address = BuildConfig.BASE_URL + (block.form)?.slug
        if (address.isNotEmpty()) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(address)
            startActivity(intent)

        }

    }

    private fun openSinglePageDisplay(blockItem: Block, sharedView: View, transitionName: String) {
        val fragmentSinglePage =
            DisplayFormSinglePageFragment.newInstance(blockItem.slug, blockItem.block?.slug)
        val tag = "fragmentSinglePage" + blockItem.slug

        val transaction = fm.beginTransaction()
        val lastFragment = fm.findFragmentByTag(tag)
        if (lastFragment != null) {
            hideAllFragments(fm)

            transaction.show(lastFragment).commit()
        } else {
            fragmentSinglePage.setListener(this)
            transaction.add(R.id.main_container, fragmentSinglePage, tag)
                .addToBackStack(tag).commit()

        }

    }

    private fun openMultiPageDisplay(blockItem: Block, sharedView: View, transitionName: String) {
        val fragmentMultiPage = LessonFragment.newInstance(blockItem.slug, blockItem.block?.slug)
        val tag = "fragmentMultiPage" + blockItem.slug

        val transaction = fm.beginTransaction()
        val lastFragment = fm.findFragmentByTag(tag)
        if (lastFragment != null) {
            hideAllFragments(fm)

            transaction.show(lastFragment).commit()
        } else {
            transaction.add(R.id.main_container, fragmentMultiPage, tag)
                .addToBackStack(tag).commit()
        }
    }


    private fun openFieldDisplay(blockItem: Block, sharedView: View, transitionName: String) {
        val fragmentDisplayField = FieldFragment.newInstance(blockItem.slug, blockItem.block?.slug)
        val tag = "fragmentDisplayField" + blockItem.slug

        val transaction = fm.beginTransaction()
        val lastFragment = fm.findFragmentByTag(tag)
        if (lastFragment != null) {
            hideAllFragments(fm)

            transaction.show(lastFragment).commit()
        } else {
            transaction.add(R.id.main_container, fragmentDisplayField, tag)
                .addToBackStack(tag).commit()
        }


    }

    private fun openResultBlock(blockItem: Block, sharedView: View, transitionName: String) {
        openResultBlock(blockItem.slug, blockItem.block?.slug, sharedView, transitionName)


    }

    override fun openResultBlock(
        blockItemSlug: String,
        blockSlug: String?,
        sharedView: View,
        transitionName: String
    ) {
        shardedVM.saveLastBlock(blockItemSlug)

        val submitsFragment = ResponsesFragment.newInstance(blockItemSlug, blockSlug)
        val tag = "submitsFragment" + blockSlug

        val transaction = fm.beginTransaction()
        val lastFragment = fm.findFragmentByTag(tag)
        if (lastFragment != null) {
            hideAllFragments(fm)

            transaction.show(lastFragment).commit()
        } else {
            transaction.add(R.id.main_container, submitsFragment, tag)
                .addToBackStack(tag).commit()
        }

    }

    override fun openLink(blockItem: Block) {
        openUri(this, blockItem.link ?: "")
        fragmentHome.fillLastItemLinkType(blockItem)

    }

    private fun openAllChartBlock(blockItem: Block, sharedView: View, transitionName: String) {
        val chartsFragment = ChartsFragment.newInstance(blockItem.slug, blockItem.block?.slug)
        val tag = "chartsFragment" + blockItem.slug
        val transaction = fm.beginTransaction()
        val lastFragment = fm.findFragmentByTag(tag)
        if (lastFragment != null) {
            hideAllFragments(fm)

            transaction.show(lastFragment).commit()
        } else {
            transaction.add(R.id.main_container, chartsFragment, tag)
                .addToBackStack(tag).commit()
        }
    }

    private fun openFieldChartBlock(blockItem: Block, sharedView: View, transitionName: String) {
        val fieldChartsFragment =
            FieldChartsFragment.newInstance(blockItem.slug, blockItem.block?.slug)
        val tag = "fieldChartsFragment" + blockItem.slug

        val transaction = fm.beginTransaction()
        val lastFragment = fm.findFragmentByTag(tag)
        if (lastFragment != null) {
            hideAllFragments(fm)

            transaction.show(lastFragment).commit()
        } else {
            transaction.add(R.id.main_container, fieldChartsFragment, tag)
                .addToBackStack(tag)
                .commit()
        }

    }

    private fun openKanbanBlock(blockItem: Block, sharedView: View, transitionName: String) {
        val kanBanFragment =
            KanBanFragment.newInstance(blockItem.slug, blockItem.block?.slug)
        val tag = "kanBanFragment" + blockItem.slug

        val transaction = fm.beginTransaction()
        val lastFragment = fm.findFragmentByTag(tag)
        if (lastFragment != null) {
            hideAllFragments(fm)

            transaction.show(lastFragment).commit()
        } else {
            transaction.add(R.id.main_container, kanBanFragment, tag)
                .addToBackStack(tag)
                .commit()
        }

    }

    private fun openReportBlock(blockItem: Block, sharedView: View, transitionName: String) {

        val reportFragment = ReportFragment.newInstance(blockItem.slug, blockItem.block?.slug)

        val tag = "reportFragment" + blockItem.slug

        val transaction = fm.beginTransaction()
        val lastFragment = fm.findFragmentByTag(tag)

        if (lastFragment != null) {
            hideAllFragments(fm)

            transaction.show(lastFragment).commit()
        } else {
            transaction.add(R.id.main_container, reportFragment, tag)
                .addToBackStack(tag)
                .commit()
        }

    }


}
