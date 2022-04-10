package co.formaloo.formCommon

import android.view.View
import co.formaloo.model.boards.block.Block


interface BlockListener {
    fun openMenuBlockItem(block: Block, imageView: View, transitionName: String)
    fun openResultBlock(blockItemSlug: String,blockSlug: String?, sharedView:View,transitionName: String)
    fun openLink(blockItem: Block)

}
