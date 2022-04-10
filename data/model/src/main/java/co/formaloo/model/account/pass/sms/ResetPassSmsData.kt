package co.formaloo.model.account.pass.sms

import java.io.Serializable

data class ResetPassSmsData(
    var phone_number: String? = null,
    var message: String? = null,
    var token: String? = null
): Serializable
