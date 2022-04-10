package co.formaloo.repository.submit

import co.formaloo.local.dao.SubmitDao
import co.formaloo.model.form.SubmitEntity
import co.formaloo.repository.BaseRepo

const val TAG = "SubmitRepoImpl"

class SubmitRepoImpl(private val submitDao: SubmitDao ) : BaseRepo(), SubmitRepo {


    override suspend fun saveSubmit(submitEntity: SubmitEntity) {

        submitDao.save(submitEntity)
    }
}
