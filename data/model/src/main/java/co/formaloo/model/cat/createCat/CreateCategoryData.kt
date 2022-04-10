package co.formaloo.model.cat.createCat

import co.formaloo.model.cat.Category
import java.io.Serializable

data class CreateCategoryData(
    var category: Category? = null
): Serializable
