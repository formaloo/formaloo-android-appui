package co.formaloo.model.account.signIn

import java.io.Serializable

data class PlanDetail(
    var plan: Int? = null,
    var from_date: String? = null,
    val to_date: String?=null,
    val low_storage_notified: Boolean?=null

):Serializable
