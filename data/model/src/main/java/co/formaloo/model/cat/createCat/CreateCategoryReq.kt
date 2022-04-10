package co.formaloo.model.cat.createCat

import java.io.Serializable

data class CreateCategoryReq(
    var title: String? = null
) : Serializable
