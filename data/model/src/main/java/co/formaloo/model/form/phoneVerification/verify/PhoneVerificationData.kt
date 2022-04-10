package co.formaloo.model.form.phoneVerification.verify

import co.formaloo.model.form.phoneVerification.SmsOtp
import java.io.Serializable

data class PhoneVerificationData(
    var sms_otp: SmsOtp? = null

) : Serializable
