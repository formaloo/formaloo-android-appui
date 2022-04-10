package co.formaloo.model.account.pass.email

import java.io.Serializable

data class ResetPassEmailData(
    var email: String? = null,
    var message: String? = null,
    var token: String? = null
): Serializable
