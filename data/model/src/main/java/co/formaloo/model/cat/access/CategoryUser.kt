package co.formaloo.model.cat.access

import java.io.Serializable

data class CategoryUser(
    var category: String? = null,
    var access: String? = null,
    var slug: String? = null,
    var profile: Any? = null

): Serializable
