package co.formaloo.model.form.access

import java.io.Serializable

data class RemoveAccessReq(
    var slug: String? = null
) : Serializable
