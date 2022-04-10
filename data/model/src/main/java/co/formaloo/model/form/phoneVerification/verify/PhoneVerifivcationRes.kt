package co.formaloo.model.form.phoneVerification.verify

import java.io.Serializable

data class PhoneVerifivcationRes(
    var status: Int? = null,
    var data: PhoneVerificationData? = null
) : Serializable {
    companion object {
        fun empty() = PhoneVerifivcationRes(0, null)

    }

    fun toPhoneVerifivcationRes() = PhoneVerifivcationRes(status, data)
}
