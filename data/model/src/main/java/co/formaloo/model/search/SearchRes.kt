package co.formaloo.model.search

import java.io.Serializable

data class SearchRes(
    var status: Int? = null,
    var data: co.formaloo.model.search.SearchData? = null
) : Serializable {
    companion object {
        fun empty() = co.formaloo.model.search.SearchRes(0, null)

    }

    fun toSearchRes() = co.formaloo.model.search.SearchRes(status, data)
}
