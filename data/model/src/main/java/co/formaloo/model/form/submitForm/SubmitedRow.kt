package co.formaloo.model.form.submitForm

import java.io.Serializable

data class SubmitedRow(
        var form: Any? = null,
        var slug: String? = null,
        var submit_code: String? = null,
        var submitter_referer_address: String? = null,
        var created_at: String? = null,
        var user: Any? = null,
        var id: Int? = null,
        var data: Map<String, Any>? = null
) : Serializable
