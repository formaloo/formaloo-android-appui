package co.formaloo.model.form.phoneVerification

import java.io.Serializable

data class SmsOtp(
    var uuid: String? = null,
    var tried: String? = null,
    var last_tried_date: String? = null
) : Serializable
