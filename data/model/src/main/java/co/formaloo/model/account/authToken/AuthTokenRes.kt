package co.formaloo.model.account.authToken

import java.io.Serializable

data class AuthTokenRes(
    var status: Int? = null,
    var data: AuthTokenData? = null
) : Serializable {
    companion object {
        fun empty() = AuthTokenRes(0, null)
    }

    fun toAuthTokenRes() = AuthTokenRes(status, data)
}
