package co.formaloo.model.account.signIn

import java.io.Serializable

data class FormProfile(
    var id: Int? = null,
    var username: String? = null,
    var score: Int? = null,
    val first_name: String?,
    val phone_number: String?,
    val email: String?,
    val gift_credit: String?,
    var plan: Plan? = null,
    var plan_detail: PlanDetail? = null,
    val user_type: String?,
    var isGuest: Boolean? = null,
    var submit_email_notif: Boolean? = null,
    var submit_push_notif: Boolean? = null,
    var weekly_digest: Boolean? = null,
    var total_forms: Int? = null,
    var total_space: Int? = null,
    var used_space: Int? = null,
    var session_token: String,
    var total_submits: Int? = null
):Serializable
