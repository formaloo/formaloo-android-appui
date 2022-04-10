package co.formaloo.model.field

import co.formaloo.model.form.Fields
import java.io.Serializable

data class CreateFieldData(
    var field: Fields? = null
): Serializable
