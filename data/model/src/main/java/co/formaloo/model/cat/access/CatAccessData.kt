package co.formaloo.model.cat.access

import java.io.Serializable

data class CatAccessData(
    var category_user: CategoryUser? = null
): Serializable
