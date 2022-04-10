package co.formaloo.model.form.folders.folders

import java.io.Serializable

data class FolderListRes(
    var status: Int? = null,
    var data: FolderListData? = null
) : Serializable {
    companion object {
        fun empty() = FolderListRes(0, null)

    }

    fun toFolderListRes() = FolderListRes(status, data)
}
