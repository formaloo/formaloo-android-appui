package co.formaloo.model.form.access

import java.io.Serializable

data class FormAccessProfiles(
    var profiles: List<String>? = null,
    var access: String? = null,
    var form: String? = null

): Serializable
