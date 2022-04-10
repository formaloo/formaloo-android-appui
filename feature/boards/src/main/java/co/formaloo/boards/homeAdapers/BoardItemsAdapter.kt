package co.formaloo.boards.homeAdapers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.formaloo.boards.R
import co.formaloo.boards.databinding.LayoutMenuBlockItemBinding
import co.formaloo.formCommon.vm.BoardsViewModel
import co.formaloo.model.boards.block.Block
import kotlin.properties.Delegates


class BoardItemsAdapter(
    private val boardVM: BoardsViewModel,
    private val blockListener: BoardFragmentListener?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    var collection: List<Block> by Delegates.observable(listOf()) { _, _, _ ->
        notifyDataSetChanged()

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_menu_block_item, parent, false)
        return    MenuBlockHolder(itemView)

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position_: Int) {
        val itemViewType = getItemViewType(position_)
        val btnItem = collection[position_]
        (holder as MenuBlockHolder).bindItems(
            btnItem,
            position_,
            boardVM,
            blockListener
        )



    }

    override fun getItemCount(): Int {
        return collection.size
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }
}

class MenuBlockHolder(view: View) : RecyclerView.ViewHolder(view) {
    val binding = LayoutMenuBlockItemBinding.bind(view)
    val context = view.context

    init {

    }

    fun bindItems(
        block: Block,
        pos: Int,
        boardVM: BoardsViewModel,
        blockListener: BoardFragmentListener?
    ) {

        val blocksAdapter = MenuBlockItemsAdapter(boardVM, blockListener)

        binding.menuBlocksRv.apply {
            adapter = blocksAdapter
            layoutManager = LinearLayoutManager(context)

        }
        blocksAdapter.collection = block.items ?: listOf()

        binding.titleTv.text = block.title?:""

        binding.lifecycleOwner = binding.titleTv.context as LifecycleOwner

    }

}



