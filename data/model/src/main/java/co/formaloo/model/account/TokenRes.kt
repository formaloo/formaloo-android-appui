package co.formaloo.model.account

import java.io.Serializable

data class TokenRes(
    var token: String
) : Serializable{
    companion object {
        fun empty() = TokenRes( "")
    }

    fun toTokenRes() = TokenRes(token)
}
