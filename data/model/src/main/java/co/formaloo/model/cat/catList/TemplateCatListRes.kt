package co.formaloo.model.cat.catList

import java.io.Serializable

data class TemplateCatListRes(
    var status: Int? = null,
    var data: TemplateCatListData? = null
) : Serializable {
    companion object {
        fun empty() = TemplateCatListRes(0, null)

    }

    fun toTemplateCatListRes() = TemplateCatListRes(status, data)
}
