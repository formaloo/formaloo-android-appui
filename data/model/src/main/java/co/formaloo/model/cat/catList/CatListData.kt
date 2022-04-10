package co.formaloo.model.cat.catList

import co.formaloo.model.cat.Category
import java.io.Serializable

data class CatListData(
    var categorys: List<Category>? = null
): Serializable
