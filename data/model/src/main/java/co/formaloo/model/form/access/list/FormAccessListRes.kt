package co.formaloo.model.form.access.list

import java.io.Serializable

data class FormAccessListRes(
    var status: Int? = null,
    var data: FormAccessListData? = null
) : Serializable {
    companion object {
        fun empty() = FormAccessListRes(0, null)

    }

    fun toFormAccessListRes() = FormAccessListRes(status, data)
}
