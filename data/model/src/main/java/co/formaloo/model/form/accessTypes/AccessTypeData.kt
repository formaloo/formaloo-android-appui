package co.formaloo.model.form.accessTypes

import java.io.Serializable

data class AccessTypeData(
    var access_type_descriptions: ArrayList<AccessType>? = null
): Serializable
