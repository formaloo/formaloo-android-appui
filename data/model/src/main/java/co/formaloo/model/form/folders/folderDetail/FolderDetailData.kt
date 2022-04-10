package co.formaloo.model.form.folders.folderDetail

import co.formaloo.model.cat.Folder

import java.io.Serializable

data class FolderDetailData(
    var category: Folder? = null
): Serializable
