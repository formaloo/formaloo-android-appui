package co.formaloo.remote.forms

import okhttp3.MultipartBody
import okhttp3.RequestBody


/**
 * Implementation of [FormService] interface
 */

class FormAllDatasource(private val service: FormService) {

    fun cityFieldChoices(fieldSlug: String?, search: String?) =
        service.cityFieldChoices(fieldSlug, search)

    fun requestPhoneVerification(req: RequestBody?) = service.requestPhoneVerification(req)
    fun verifyPhoneVerification(uuid: String, req: RequestBody?) =
        service.verifyPhoneVerification(uuid, req)

    fun resendPhoneVerification(uuid: String) = service.resendPhoneVerification(uuid)
    fun submitForm(
        slug: String,
        req: HashMap<String, RequestBody>,
        files: List<MultipartBody.Part>?
    ) = service.submitForm(slug, req, files)

    fun editRowDetail(
        slug: String,
        req: HashMap<String, RequestBody>,
        files: List<MultipartBody.Part>?
    ) = service.editRowDetail(slug, req, files)

}
