package co.formaloo.repository.submit

import co.formaloo.model.form.SubmitEntity

interface SubmitRepo {

    suspend fun saveSubmit(submitEntity: SubmitEntity)
}
