package co.formaloo.model.stat

import java.io.Serializable

data class StatRes(
    var status: Int? = null,
    var data: StatData? = null
) : Serializable {
    companion object {
        fun empty() = StatRes(0, null)

    }

    fun toStatRes() = StatRes(status, data)
}
