package co.formaloo.model.submit

import java.io.Serializable

data class User(
    var id: String? = null,
    var first_name: String? = null,
    var last_name: String? = null
) : Serializable
