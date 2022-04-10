package co.formaloo.model.paymentmethods

import java.io.Serializable

data class PaymentMethodsRes(
    var status: Int? = null,
    var data: PaymentMethodsData? = null
):Serializable{
    companion object{
        fun empty()= PaymentMethodsRes(0, null)
    }

    fun toPaymentMethodsRes()= PaymentMethodsRes(status, data)
}
