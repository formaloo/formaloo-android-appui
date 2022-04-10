package co.formaloo.model.account.signIn

import java.io.Serializable

data class SignInRes(
    var status: Int? = null,
    var data: SignInData? = null
) : Serializable {
    companion object {
        fun empty() = SignInRes(0, null)
    }

    fun toSignInRes() = SignInRes(status, data)
}
