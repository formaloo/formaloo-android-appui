package co.formaloo.model.account.bussiness

import java.io.Serializable

data class Business(
    var slug: String? = null,
    var title: String? = null,
    var business_identifier : String? = null,
) : Serializable
