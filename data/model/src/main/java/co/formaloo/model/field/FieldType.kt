package co.formaloo.model.field

import java.io.Serializable

data class FieldType(
    var title: String? = null,
    var drawable: Int? = null,
    var type: String? = null,
    var inner_type: String? = null

) : Serializable
