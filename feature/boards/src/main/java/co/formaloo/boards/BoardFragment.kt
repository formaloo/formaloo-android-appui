package co.formaloo.boards

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import co.formaloo.boards.databinding.FragmentBoardBinding
import co.formaloo.boards.homeAdapers.BoardFragmentListener
import co.formaloo.boards.homeAdapers.BoardItemsAdapter
import co.formaloo.common.base.BaseFragment
import co.formaloo.common.base.BaseViewModel
import co.formaloo.common.extension.invisible
import co.formaloo.common.extension.visible
import co.formaloo.common.isOnline
import co.formaloo.common.openUri
import co.formaloo.common.parseColor
import co.formaloo.formCommon.BlockListener
import co.formaloo.formCommon.vm.BoardsViewModel
import co.formaloo.formCommon.vm.SharedViewModel
import co.formaloo.model.boards.BlockType
import co.formaloo.model.boards.StateItem
import co.formaloo.model.boards.block.Block

import com.ethanhua.skeleton.RecyclerViewSkeletonScreen
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import kotlinx.android.synthetic.main.lesson_inprogress.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class BoardFragment() : BaseFragment() {
    private var lastMenuItems = arrayListOf<Block>()
    private var lastStatItems = arrayListOf<Block>()
    private lateinit var binding: FragmentBoardBinding
    private val boardVM: BoardsViewModel by viewModel()
    override fun getViewModel(): BaseViewModel = boardVM
    private val shardedVM: SharedViewModel by viewModel()
    private var skeleton: RecyclerViewSkeletonScreen? = null
    private var skeletonScreen: SkeletonScreen? = null
    private var menuSlug: String?=null
    private var statSlug: String?=null

    var blockListener: BlockListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBoardBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuBlocksAdapter = BoardItemsAdapter(boardVM, object : BoardFragmentListener {
            override fun boardBlockClicked(blockItem: Block, view: ImageView, name: String) {
                shardedVM.saveLastBlock(blockItem.slug)

                blockListener?.openMenuBlockItem(blockItem, view, "imageView")

                fillLastItemBlockType(blockItem)

            }

            override fun boardLinkClicked(blockItem: Block) {
                shardedVM.saveLastBlock(blockItem.slug)

                blockListener?.openLink(blockItem)
                fillLastItemLinkType(blockItem)

            }

        })


        binding.menublocksRV.apply {
            adapter = menuBlocksAdapter
            layoutManager = LinearLayoutManager(context)

        }


        skeleton = Skeleton.bind(binding.menublocksRV)
            .adapter(menuBlocksAdapter)
            .load(R.layout.layout_block_itemskeleton)
            .color(R.color.static_item_light_gray)
            .count(15)
            .show()


        skeletonScreen = Skeleton.bind(binding.lessonInprogress)
            .load(R.layout.lesson_inprogress_skeleton)
            .color(R.color.static_item_light_gray)
            .show()

        fetchLocalBoards()

        boardVM.rawMenuBlock.observe(viewLifecycleOwner) { it ->
            it?.let { block ->
                val slug = block.slug
                menuSlug = slug
                fetchMenuBlock(slug)
            }
        }

        boardVM.rawStatBlock.observe(viewLifecycleOwner) { it ->
            it?.let { block ->
                val slug = block.slug
                statSlug = slug
                fetchStatBlock(slug)
            }
        }

        binding.boardsRefreshLay.setOnRefreshListener {
            if (menuSlug==null || statSlug==null){
                fetchBoards()
            }else{
                if (menuSlug!=null){
                    fetchMenuBlock(menuSlug?:"")
                }
                if (statSlug!=null){
                    fetchStatBlock(statSlug?:"")
                }
            }



        }

        boardVM.menuBlock.observe(viewLifecycleOwner) { it ->
            it?.let {
                menuSlug=it.slug
                val blockMenuList = arrayListOf<Block>()

                checkLastItem(it.items ?: arrayListOf())
                blockMenuList.add(it)

                stopMenuLoading(blockMenuList)

                if (lastMenuItems != blockMenuList) {
                    menuBlocksAdapter.collection = blockMenuList.toList()
                }

                lastMenuItems = blockMenuList
            }
        }

        boardVM.statBlock.observe(viewLifecycleOwner) { it ->
            it?.let {
                statSlug=it.slug

                var hasEnableItem = false
                it.stats?.forEach { statItems ->
                    val enabled = statItems.enabled
                    if (enabled == true) {
                        hasEnableItem = true
                    } else {

                    }

                    val statItemsMap = statItems.data ?: hashMapOf()
                    statItemsMap["count"]?.toString()?.let {
                        val count = it.toFloat().toInt()

                        when (statItems.slug) {
                            StateItem.TOTAL_VISITS.slug -> {
                                binding.totalVisitTxv.text = "${count}"
                                binding.totalVisitLay.isVisible = enabled ?: false
                            }
                            StateItem.TOTAL_SUBMITS.slug -> {
                                binding.totalSubmitsTxv.text = "$count"
                                binding.totalSubmitLay.isVisible = enabled ?: false


                            }
                            StateItem.DAILY_VISITS.slug -> {
                                binding.dailyVisitTxv.text = "$count"
                                binding.dailyVisitLay.isVisible = enabled ?: false

                            }
                            StateItem.DAILY_SUBMITS.slug -> {
                                binding.dailySubmitsTxv.text = "$count"
                                binding.dailySubmitLay.isVisible = enabled ?: false

                            }
                        }

                    }


                }

                binding.titleTv.text = it.title ?: ""
                binding.statlay.isVisible = hasEnableItem

            }


        }

        boardVM.localBlockList.observe(viewLifecycleOwner) { it ->
            it?.let { blockList ->
                Timber.e("localBlockList $blockList")
                if (blockList.isEmpty()){
                    fetchBoards()
                }else{
                    blockList.findLast {
                        it.type == BlockType.MENU.slug
                    }?.let {
                        boardVM.retrieveMenuBlockDetailFromDB(it.slug ?: "")
                    }

                    blockList.findLast {
                        it.type == BlockType.STATS.slug
                    }?.let {
                        boardVM.retrieveStatBlockDetailFromDB(it.slug ?: "")

                    }
                }

            }
        }

    }

    private fun stopMenuLoading(blockList: List<Block>) {
        skeleton?.hide()
        skeletonScreen?.hide()

        binding.boardsRefreshLay.isRefreshing = false


    }

    private fun checkLastItem(blockList: List<Block>) {
        val lastBlockSlug = shardedVM.getLastBlock()
        blockList.find {
            it.slug == lastBlockSlug
        }?.let { lastBlock ->
            fillLastItemBlockType(lastBlock)
        }


    }

    override fun onResume() {
        super.onResume()
    }
    private fun fetchBoards() {
        if (isOnline(requireContext())) {
            boardVM.retrieveSharedBoardDetail(BuildConfig.APPUI_ADDRESS)

        } else {

        }
    }
    private fun fetchLocalBoards() {
        boardVM.retrieveBlockListFromDB()



    }
    private fun fetchMenuBlock(menuSlug:String) {
        if (isOnline(requireContext())) {
            boardVM.retrieveMenuBlockDetail(BuildConfig.APPUI_ADDRESS,menuSlug)

        } else {
            boardVM.retrieveMenuBlockDetailFromDB(menuSlug)

        }

    }

    private fun fetchStatBlock(menuSlug:String) {
        if (isOnline(requireContext())) {
            boardVM.retrieveStatBlockDetail(BuildConfig.APPUI_ADDRESS,menuSlug)

        } else {
            boardVM.retrieveStatBlockDetailFromDB(menuSlug)

        }


    }

    fun fillLastItemLinkType(lastBlock: Block) {
        binding.lessonInprogress.continueBtn.setOnClickListener {
            openUri(requireActivity(), lastBlock.link ?: "")
        }

        val color = lastBlock.color
        if (color != null) {
            ImageViewCompat.setImageTintList(
                binding.lessonInprogress.lesson_in_progress_img,
                ColorStateList.valueOf(parseColor(color))
            )

        } else {

        }

        binding.lessonInprogress.titleTv.text = lastBlock.title
        binding.lessonInprogress.visible()

    }

    fun fillLastItemBlockType(lastBlock: Block) {
        updateLastBlockUI(lastBlock)

        updateFlashCardProgressView(lastBlock)

    }

    private fun updateLastBlockUI(lastBlock: Block) {
        binding.lessonInprogress.continueBtn.setOnClickListener {
            shardedVM.saveLastBlock(lastBlock.slug)

            blockListener?.openMenuBlockItem(
                lastBlock,
                binding.lessonInprogress.continueBtn,
                "continueBtn"
            )
        }

        lastBlock.color?.let {
            ImageViewCompat.setImageTintList(
                binding.lessonInprogress.lesson_in_progress_img,
                ColorStateList.valueOf(parseColor(it))
            )

        }


        binding.lessonInprogress.titleTv.text = lastBlock.title
        binding.lessonInprogress.visible()

    }

    private fun updateFlashCardProgressView(lastBlock: Block) {

        val percentageMap = shardedVM.retrieveLessonPercentage()
        if (percentageMap.containsKey(lastBlock.slug)) {
            val percentageValue = percentageMap[lastBlock.slug] ?: 0

            binding.lessonInprogress.percentageLay.visible()
            binding.lessonInprogress.percentageLayV.visible()

            binding.lessonInprogress.percentTv.text = "% $percentageValue"
            binding.lessonInprogress.progressBar.max = 100
            binding.lessonInprogress.progressBar.progress = percentageValue

        } else {
            binding.lessonInprogress.percentageLay.invisible()
            binding.lessonInprogress.percentageLayV.invisible()

        }


    }


}
