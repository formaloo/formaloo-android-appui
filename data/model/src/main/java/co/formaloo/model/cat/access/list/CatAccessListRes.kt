package co.formaloo.model.cat.access.list

import java.io.Serializable

data class CatAccessListRes(
    var status: Int? = null,
    var data: CatAccessListData? = null
) : Serializable {
    companion object {
        fun empty() = CatAccessListRes(0, null)

    }

    fun toCatAccessListRes() = CatAccessListRes(status, data)
}
