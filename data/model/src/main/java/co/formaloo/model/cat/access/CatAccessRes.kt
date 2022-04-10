package co.formaloo.model.cat.access

import java.io.Serializable

data class CatAccessRes(
    var status: Int? = null,
    var data: CatAccessData? = null
) : Serializable {
    companion object {
        fun empty() = CatAccessRes(0, null)

    }

    fun toCatAccessRes() = CatAccessRes(status, data)
}
