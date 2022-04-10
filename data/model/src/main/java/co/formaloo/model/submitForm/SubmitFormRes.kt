package co.formaloo.model.submitForm

import java.io.Serializable

data class SubmitFormRes(
    var status: Int? = null,
    var data: co.formaloo.model.submitForm.SubmitFormData? = null
) : Serializable {
    companion object {
        fun empty() = co.formaloo.model.submitForm.SubmitFormRes(0, null)

    }

    fun toSubmitFormRes() = co.formaloo.model.submitForm.SubmitFormRes(status, data)
}
