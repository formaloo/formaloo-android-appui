package co.formaloo.model.cat.createCat

import java.io.Serializable

data class CreateCategoryRes(
    var status: Int? = null,
    var data: CreateCategoryData? = null
) : Serializable {
    companion object {
        fun empty() = CreateCategoryRes(0, null)

    }

    fun toCreateCategoryRes() = CreateCategoryRes(status, data)
}
