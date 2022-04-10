package co.formaloo.model.form.folders.folderDetail

import java.io.Serializable

data class FolderDetailRes(
    var status: Int? = null,
    var data: FolderDetailData? = null
) : Serializable {
    companion object {
        fun empty() = FolderDetailRes(0, null)

    }

    fun toFolderDetailRes() = FolderDetailRes(status, data)
}
