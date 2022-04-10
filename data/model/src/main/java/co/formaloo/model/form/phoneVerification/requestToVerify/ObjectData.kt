package co.formaloo.model.form.phoneVerification.requestToVerify

import co.formaloo.model.form.phoneVerification.SmsOtp
import java.io.Serializable

data class ObjectData(
    var sms_otp: SmsOtp? = null
): Serializable
