package co.formaloo.model.form.access

import java.io.Serializable

data class FormAccessRes(
    var status: Int? = null,
    var data: FormAccessData? = null
) : Serializable {
    companion object {
        fun empty() = FormAccessRes(0, null)

    }

    fun toFormAccessRes() = FormAccessRes(status, data)
}
