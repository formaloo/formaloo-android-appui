package co.formaloo.model.account.bussiness

import java.io.Serializable

data class BusinessListRes(
    var status: Int? = null,
    var data: BusinessListData? = null
):Serializable{
    companion object{
        fun empty()= BusinessListRes(0, null)
    }

    fun toBusinessListRes()= BusinessListRes(status, data)
}
