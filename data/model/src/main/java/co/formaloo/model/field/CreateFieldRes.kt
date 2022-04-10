package co.formaloo.model.field

import java.io.Serializable

data class CreateFieldRes(
    var status: Int? = null,
    var data: CreateFieldData? = null
) : Serializable {
    companion object {
        fun empty() = CreateFieldRes(0, null)

    }

    fun toCreateFieldRes() = CreateFieldRes(status, data)
}
