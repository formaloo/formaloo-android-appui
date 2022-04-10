package co.formaloo.model.account.pass.email

import java.io.Serializable

data class ResetPassEmailRes(
    var status: Int? = null,
    var data: ResetPassEmailData? = null
):Serializable{
    companion object{
        fun empty()= ResetPassEmailRes(0, null)
    }

    fun toResetPassEmailRes()= ResetPassEmailRes(status, data)
}
