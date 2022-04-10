package co.formaloo.model.account.pass.change

import java.io.Serializable

data class ChangePassData(
    var email: String? = null,
    var message: String? = null
): Serializable
