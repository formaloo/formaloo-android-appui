package co.formaloo.model.account.signUp

import java.io.Serializable

data class SignUpRes(
    var status: Int? = null,
    var data: SignUpData? = null
): Serializable {
    companion object{
        fun empty()=SignUpRes(0,null)

    }

    fun toSignUpRes()=SignUpRes(status,data)
}
