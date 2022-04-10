package co.formaloo.model.form.accessTypes

import java.io.Serializable

data class AccessTypeRes(
    var status: Int? = null,
    var data: AccessTypeData? = null
) : Serializable {
    companion object {
        fun empty() = AccessTypeRes(0, null)

    }

    fun toAccessTypeRes() = AccessTypeRes(status, data)
}
