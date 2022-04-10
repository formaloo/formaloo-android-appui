package co.formaloo.model.paymentmethods

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PaymentMethodsData(
    @SerializedName("objects")
    var paymentMethod: ArrayList<PaymentMethod>? = null,


): Serializable
