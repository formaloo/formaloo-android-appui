package co.formaloo.model.form.folders.folders

import co.formaloo.model.cat.Folder

import java.io.Serializable

data class FolderListData(
    var categories: ArrayList<Folder>? = null,
    var previous: String? = null,
    var next: String? = null,
    var count: Int? = null,
    var current_page: Int? = null,
    var page_count: Int? = null,
    var page_size: Int? = null
): Serializable
