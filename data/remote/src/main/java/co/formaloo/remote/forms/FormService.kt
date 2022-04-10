package co.formaloo.remote.forms

import co.formaloo.common.Constants.VERSION1
import co.formaloo.common.Constants.VERSION3
import co.formaloo.model.cityChoices.CityChoicesRes
import co.formaloo.model.form.phoneVerification.requestToVerify.RequestVerifivcationRes
import co.formaloo.model.form.phoneVerification.verify.PhoneVerifivcationRes
import co.formaloo.model.form.submitForm.SubmitFormRes
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface FormService : FFieldService,
    FRowsService, FSubmitService {

    companion object {


        private const val request_phone_verification =
            "forms/request_phone_verification/"//body=>phone,form
        private const val verify_phone_verification =
            "forms/verify_phone_verification/{uuid}/"//body=>code
        private const val resend_phone_verification = "forms/resend_phone_verification/{uuid}/"


    }

    @POST(request_phone_verification)
    fun requestPhoneVerification(
        @Body req: RequestBody?
    ): Call<RequestVerifivcationRes>

    @PUT(verify_phone_verification)
    fun verifyPhoneVerification(
        @Path("uuid") uuid: String,
        @Body req: RequestBody?
    ): Call<PhoneVerifivcationRes>

    @POST(resend_phone_verification)
    fun resendPhoneVerification(@Path("uuid") uuid: String): Call<PhoneVerifivcationRes>

}


interface FFieldService {
    companion object {
        private const val FIELD_CITY_CHOICES = "${VERSION3}fields/{slug}/choices/"

    }

    @GET(FIELD_CITY_CHOICES)
    fun cityFieldChoices(
        @Path("slug") fieldSlug: String?,
        @Query("search") search: String?

    ): Call<CityChoicesRes>

}

interface FRowsService {


    companion object {

        private const val ROW_DETAIL = "${VERSION3}rows/{slug}/"


    }

    @Multipart
    @PATCH(ROW_DETAIL)
    fun editRowDetail(
        @Path("slug") slug: String,
        @PartMap req: HashMap<String, RequestBody>,
        @Part files: List<MultipartBody.Part>?
    ): Call<SubmitFormRes>


}

interface FSubmitService {


    companion object {
        private const val submitForm = "${VERSION1}forms/form/{slug}/submit/"
    }

    @Multipart
    @POST(submitForm)
    fun submitForm(
        @Path("slug") slug: String,
        @PartMap req: HashMap<String, RequestBody>,
        @Part files: List<MultipartBody.Part>?
    ): Call<SubmitFormRes>

}
