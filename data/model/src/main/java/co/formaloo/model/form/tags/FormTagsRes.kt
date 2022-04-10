package co.formaloo.model.form.tags

import java.io.Serializable

data class FormTagsRes(
    var status: Int? = null,
    var data: FormTagsData? = null
) : Serializable {
    companion object {
        fun empty() = FormTagsRes(0, null)

    }

    fun toFormTagsRes() = FormTagsRes(status, data)
}
