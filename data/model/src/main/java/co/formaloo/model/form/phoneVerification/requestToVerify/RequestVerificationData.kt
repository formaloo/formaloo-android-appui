package co.formaloo.model.form.phoneVerification.requestToVerify

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class RequestVerificationData(
    @SerializedName("object")
    var objectData: ObjectData? = null
) : Serializable
