package co.formaloo.model.account.pass.change

import java.io.Serializable

data class ChangePassRes(
    var status: Int? = null,
    var data: ChangePassData? = null
):Serializable{
    companion object{
        fun empty()= ChangePassRes(0, null)
    }

    fun toChangePassRes()= ChangePassRes(status, data)
}
