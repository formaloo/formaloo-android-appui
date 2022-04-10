package co.formaloo.model.account.bussiness

import java.io.Serializable

data class BusinessListData(
    var businesses: ArrayList<Business>? = null
) : Serializable
