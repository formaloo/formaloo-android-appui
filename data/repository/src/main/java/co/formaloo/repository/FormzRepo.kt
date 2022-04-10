package co.formaloo.repository

import co.formaloo.common.exception.Failure
import co.formaloo.common.functional.Either
import co.formaloo.model.cityChoices.CityChoicesRes
import co.formaloo.model.form.*
import co.formaloo.model.form.phoneVerification.requestToVerify.RequestVerifivcationRes
import co.formaloo.model.form.phoneVerification.verify.PhoneVerifivcationRes
import co.formaloo.model.form.submitForm.SubmitFormRes
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.util.*

interface FormzRepo {

    suspend fun submitForm(
        submitEntity: SubmitEntity,
        slug: String,
        req: HashMap<String, RequestBody>,
        files: List<MultipartBody.Part>?
    )

    suspend fun editRowDetail(
        submitEntity: SubmitEntity,
        slug: String,
        req: HashMap<String, RequestBody>,
        files: List<MultipartBody.Part>?
    ): Either<Failure, SubmitFormRes>
    suspend fun requestPhoneVerification(req: RequestBody?): Either<Failure, RequestVerifivcationRes>
    suspend fun verifyPhoneVerification(
        uuid: String,
        req: RequestBody?
    ): Either<Failure, PhoneVerifivcationRes>
    suspend fun resendPhoneVerification(uuid: String): Either<Failure, PhoneVerifivcationRes>
    suspend fun saveSubmit(submitEntity: SubmitEntity)
    suspend fun sendSavedSubmitToServer()
    suspend fun cityFieldChoices(
        fieldSlug: String?,
        query: String?
    ): Either<Failure, CityChoicesRes>

}
