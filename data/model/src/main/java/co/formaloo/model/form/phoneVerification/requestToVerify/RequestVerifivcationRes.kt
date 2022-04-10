package co.formaloo.model.form.phoneVerification.requestToVerify

import java.io.Serializable

data class RequestVerifivcationRes(
    var status: Int? = null,
    var data: RequestVerificationData? = null
) : Serializable {
    companion object {
        fun empty() = RequestVerifivcationRes(0, null)

    }

    fun toRequestVerifivcationRes() = RequestVerifivcationRes(status, data)
}
