package co.formaloo.model.cat.catList

import co.formaloo.model.cat.Category
import java.io.Serializable

data class TemplateCatListData(
    var categories: List<Category>? = null
): Serializable
