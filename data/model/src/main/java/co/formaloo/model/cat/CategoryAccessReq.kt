package co.formaloo.model.cat

import java.io.Serializable

data class CategoryAccessReq(
    var profile: String? = null,
    var access: String? = null,
    var category: String? = null

): Serializable
