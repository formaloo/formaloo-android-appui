package co.formaloo.model.tag.create

import java.io.Serializable

data class CreateTagRes(
    var status: Int? = null,
    var data: CreateTagsData? = null
) : Serializable {
    companion object {
        fun empty() = CreateTagRes(0, null)
    }

    fun toCreateTagRes() = CreateTagRes(status, data)
}
