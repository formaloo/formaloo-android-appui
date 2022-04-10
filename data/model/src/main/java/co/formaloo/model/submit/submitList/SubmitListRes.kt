package co.formaloo.model.submit.submitList

import java.io.Serializable

data class SubmitListRes(
    var status: Int? = null,
    var data: SubmitListData? = null
) : Serializable {
    companion object {
        fun empty() = SubmitListRes(0, null)

    }

    fun toSubmitListRes() = SubmitListRes(status, data)
}
