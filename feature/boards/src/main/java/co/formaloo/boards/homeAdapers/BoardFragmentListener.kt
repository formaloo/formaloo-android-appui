package co.formaloo.boards.homeAdapers

import android.widget.ImageView
import co.formaloo.model.boards.block.Block


interface BoardFragmentListener {
    fun boardBlockClicked(blockItem: Block, view: ImageView, name: String)
    fun boardLinkClicked(blockItem: Block)
}
