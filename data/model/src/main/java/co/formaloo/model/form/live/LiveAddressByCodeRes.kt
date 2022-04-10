package co.formaloo.model.form.live

import java.io.Serializable

data class LiveAddressByCodeRes(
    var status: Int? = null,
    var data: LiveAddressByCodeData? = null
) : Serializable {
    companion object {
        fun empty() = LiveAddressByCodeRes(0, null)

    }

    fun toLiveAddressByCodeRes() = LiveAddressByCodeRes(status, data)
}
