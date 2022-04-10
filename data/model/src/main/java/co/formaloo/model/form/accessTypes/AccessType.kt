package co.formaloo.model.form.accessTypes

import java.io.Serializable

data class AccessType(
    var access: String? = null,
    var description: String? = null,
    var slug: String? = null
) : Serializable
