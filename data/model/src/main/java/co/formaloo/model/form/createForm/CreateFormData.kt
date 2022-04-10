package co.formaloo.model.form.createForm

import co.formaloo.model.form.Form
import java.io.Serializable

data class CreateFormData(
    var form: Form? = null
): Serializable
