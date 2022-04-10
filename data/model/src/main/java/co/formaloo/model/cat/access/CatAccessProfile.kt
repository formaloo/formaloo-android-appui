package co.formaloo.model.cat.access

import java.io.Serializable

data class CatAccessProfile(
    var category_user: String? = null,
    var first_name: String? = null,
    var last_name: String? = null,
    var shared_access: String? = null,
    var id: String? = null

): Serializable
