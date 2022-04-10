package co.formaloo.model.account.signIn

import java.io.Serializable

data class Plan(
    var total_space: Int? = null,
    var slug: String? = null,
    val title: String
):Serializable
