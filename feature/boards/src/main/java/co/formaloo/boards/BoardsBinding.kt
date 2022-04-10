package co.formaloo.boards

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import co.formaloo.boards.homeAdapers.MenuBlockItemsAdapter
import co.formaloo.model.boards.block.Block

object BoardsBinding {
    @BindingAdapter("app:items")
    @JvmStatic
    fun setBlockItems(recyclerView: RecyclerView, resource: List<Block>?) {
        if (recyclerView.adapter is MenuBlockItemsAdapter)
            with(recyclerView.adapter as MenuBlockItemsAdapter) {
                resource?.let {
                }
            }
    }

}
