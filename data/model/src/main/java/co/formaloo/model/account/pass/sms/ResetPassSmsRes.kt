package co.formaloo.model.account.pass.sms

import java.io.Serializable

data class ResetPassSmsRes(
    var status: Int? = null,
    var data: ResetPassSmsData? = null
):Serializable{
    companion object{
        fun empty()= ResetPassSmsRes(0, null)
    }

    fun toResetPassSmsRes()= ResetPassSmsRes(status, data)
}
