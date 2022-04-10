package co.formaloo.model.form.live

import java.io.Serializable

data class LiveDashboardCode(
    var code: String? = null,
    var form: Any? = null
): Serializable
