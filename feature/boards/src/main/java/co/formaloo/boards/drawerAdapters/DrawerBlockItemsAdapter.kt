package co.formaloo.boards.drawerAdapters

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.ImageViewCompat
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.formaloo.boards.R
import co.formaloo.boards.databinding.LayoutDrawerBlockItemBinding
import co.formaloo.boards.databinding.LayoutDrawerGroupBlockItemBinding
import co.formaloo.boards.homeAdapers.BoardFragmentListener
import co.formaloo.common.parseColor
import co.formaloo.formCommon.vm.BoardsViewModel
import co.formaloo.model.boards.BlockType
import co.formaloo.model.boards.block.Block

import kotlin.properties.Delegates

class DrawerBlockItemsAdapter(
    private val boardVM: BoardsViewModel,
    private val blockListener: BoardFragmentListener?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var collection: List<Block> by Delegates.observable(listOf()) { _, _, _ ->
        notifyDataSetChanged()

    }

    private val TYPE_BLOCK = 0
    private val TYPE_GROUP = 1
    private val TYPE_LINK = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView: View

        return when (viewType) {
            TYPE_BLOCK -> {
                itemView =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.layout_drawer_block_item, parent, false)
                MenuItemBlockHolder(itemView)
            }
            TYPE_GROUP -> {
                itemView =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.layout_drawer_group_block_item, parent, false)
                GroupHolder(itemView)
            }
            TYPE_LINK -> {
                itemView =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.layout_drawer_block_item, parent, false)
                MenuItemBlockHolder(itemView)
            }
            else -> {
                itemView =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.layout_drawer_block_item, parent, false)
                MenuItemBlockHolder(itemView)
            }
        }


    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position_: Int) {
        val itemViewType = getItemViewType(position_)
        val btnItem = collection[position_]

        when (itemViewType) {
            TYPE_BLOCK -> {
                (holder as MenuItemBlockHolder).bindItems(
                    btnItem,
                    position_,
                    boardVM,
                    blockListener
                )
            }
            TYPE_GROUP -> {
                (holder as GroupHolder).bindItems(
                    btnItem,
                    position_,
                    boardVM,
                    blockListener
                )
            }
            TYPE_LINK -> {
                (holder as MenuItemBlockHolder).bindItems(
                    btnItem,
                    position_,
                    boardVM,
                    blockListener
                )
            }
            else -> {
                (holder as MenuItemBlockHolder).bindItems(
                    btnItem,
                    position_,
                    boardVM,
                    blockListener
                )
            }
        }


    }

    override fun getItemViewType(position: Int): Int {
        val block = collection[position]
        return when (block.type) {
            BlockType.GROUP.slug -> {
                TYPE_GROUP
            }
            BlockType.BLOCK.slug -> {
                TYPE_BLOCK
            }
            BlockType.LINK.slug -> {
                TYPE_LINK

            }
            else -> {
                TYPE_BLOCK

            }
        }

    }

    override fun getItemCount(): Int {
        return collection.size
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }
}

class MenuItemBlockHolder(view: View) : RecyclerView.ViewHolder(view) {
    private var lastSelectedItem: View? = null
    val binding = LayoutDrawerBlockItemBinding.bind(view)
    val context = view.context

    init {

    }

    fun bindItems(
        blockItem: Block,
        pos: Int,
        boardVM: BoardsViewModel,
        blockListener: BoardFragmentListener?
    ) {
//        binding.blockLay.backgroundTintList= ColorStateList(
//            arrayOf(
//                intArrayOf(-android.R.attr.state_selected),
//                intArrayOf(android.R.attr.state_selected)
//            ), intArrayOf(
//               R.color.light_transparent //disabled
//                , R.color.colorBlueShadow //enabled
//            )
//        )
        itemView.setOnClickListener {
//            lastSelectedItem?.isSelected=false
//            binding.blockLay.isSelected=true
//            lastSelectedItem= binding.blockLay

            when (blockItem.type) {
                BlockType.BLOCK.slug -> {
                    blockListener?.boardBlockClicked(blockItem, binding.imageView, "imageView")

                }
                BlockType.LINK.slug -> {
                    blockListener?.boardLinkClicked(blockItem)

                }
            }
        }

        binding.titleTv.text = blockItem.title


        val color = blockItem.color
        if (color != null) {
            ImageViewCompat.setImageTintList(
                binding.closeFullBtn,
                ColorStateList.valueOf(parseColor(color))
            )

        } else {

        }

        binding.lifecycleOwner = binding.titleTv.context as LifecycleOwner
    }

}

class GroupHolder(view: View) : RecyclerView.ViewHolder(view) {
    val binding = LayoutDrawerGroupBlockItemBinding.bind(view)
    val context = view.context

    fun bindItems(
        blockItem: Block,
        pos: Int,
        boardVM: BoardsViewModel,
        blockListener: BoardFragmentListener?
    ) {
//        binding.groupBlocksRv.invisible()

        val blocksAdapter = DrawerBlockItemsAdapter(boardVM, blockListener)

        binding.groupBlocksRv.apply {
            adapter = blocksAdapter
            layoutManager = LinearLayoutManager(context)

        }
        blocksAdapter.collection = blockItem.sub_items ?: listOf()

        binding.titleTv.text = blockItem.title

        itemView.setOnClickListener {
//            val visible = binding.groupBlocksRv.isVisible
//
//            val angel = if (visible) {
//              0f
//            } else {
//                90f
//            }
//
//            binding.arrowimv.rotation = angel
//
//            binding.groupBlocksRv.isVisible = !visible
        }
    }

}


