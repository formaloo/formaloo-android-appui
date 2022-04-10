package co.formaloo.model.submit.row

import java.io.Serializable

data class RowDetailRes(
    var status: Int? = null,
    var data: RowDetailData? = null
) : Serializable {
    companion object {
        fun empty() = RowDetailRes(0, null)

    }

    fun toRowDetailRes() = RowDetailRes(status, data)
}
