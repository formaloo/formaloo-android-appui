package co.formaloo.model.paymentmethods

import java.io.Serializable

data class PaymentMethod(
    var title: String? = null,
    var name: String? = null,
    var payment_type: String? = null,
    var gateway: String? = null,
    var description: String? = null,
    var active: Boolean? = null,
    var merchant_code: Any? = null,
    var order: String? = null,
    var private_key: Any? = null,
    var terminal_code: Any? = null,
) : Serializable
