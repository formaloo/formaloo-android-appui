package co.formaloo.repository

import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import co.formaloo.common.Constants
import co.formaloo.common.exception.Failure
import co.formaloo.common.functional.Either
import co.formaloo.local.dao.*
import co.formaloo.model.cityChoices.CityChoicesRes
import co.formaloo.model.form.*
import co.formaloo.model.form.phoneVerification.requestToVerify.RequestVerifivcationRes
import co.formaloo.model.form.phoneVerification.verify.PhoneVerifivcationRes
import co.formaloo.model.form.submitForm.SubmitFormRes
import co.formaloo.remote.forms.FormAllDatasource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.net.URLEncoder
import java.util.*

const val TAG = "FormzRepo"

class FormzRepoImpl(
    val source: FormAllDatasource,
    private val submitDao: SubmitDao,
) : FormzRepo,BaseRepo() {

    override suspend fun requestPhoneVerification(req: RequestBody?): Either<Failure, RequestVerifivcationRes> {
        val call = source.requestPhoneVerification(req)
        return try {
            request(call, { it.toRequestVerifivcationRes() }, RequestVerifivcationRes.empty())
        } catch (e: Exception) {
            Either.Left(Failure.Exception)
        }

    }

    override suspend fun verifyPhoneVerification(
        uuid: String,
        req: RequestBody?
    ): Either<Failure, PhoneVerifivcationRes> {
        val call = source.verifyPhoneVerification(uuid, req)
        return try {
            request(call, { it.toPhoneVerifivcationRes() }, PhoneVerifivcationRes.empty())
        } catch (e: Exception) {
            Either.Left(Failure.Exception)
        }

    }

    override suspend fun resendPhoneVerification(uuid: String): Either<Failure, PhoneVerifivcationRes> {
        val call = source.resendPhoneVerification(uuid)
        return try {
            request(call, { it.toPhoneVerifivcationRes() }, PhoneVerifivcationRes.empty())
        } catch (e: Exception) {
            Either.Left(Failure.Exception)
        }

    }

    override suspend fun saveSubmit(submitEntity: SubmitEntity) {
        Log.e(TAG, "saveSubmit: $submitEntity")

        submitDao.save(submitEntity)
    }

    override suspend fun sendSavedSubmitToServer() {

        getSubmitListFromDB().map {
            Log.e(TAG, "sendSavedSubmitToServer: $it")
            val newRow = it.newRow
            val formSLug = it.formSlug
            val rowSlug = it.rowSlug
            val formReq = createFormReq(it.formReq)
            val files = createFilesReq(it.files)

            if (((newRow != null && newRow) || it.rowSlug == null)) {
                if (formSLug != null) {
                    submitForm(it, formSLug, formReq, files)
                } else {

                }
            } else {
                if (rowSlug != null) {
                    editRowDetail(it, rowSlug, formReq, files)
                } else {

                }
            }

        }
    }

    suspend fun getSubmitListFromDB(): List<SubmitEntity> {
        return submitDao.getSubmitEntityList()
    }

    fun createFormReq(reqMap: HashMap<String, String>): HashMap<String, RequestBody> {
        val jsonParams: HashMap<String, RequestBody> = HashMap()
        jsonParams[""] = createPartFromString("")
        reqMap.keys.forEach { slug ->
            val data = createPartFromString(reqMap[slug] ?: "")
            jsonParams[slug] = data
        }

        return jsonParams
    }

    fun createFilesReq(filesMap: HashMap<String, Fields>): ArrayList<MultipartBody.Part> {
        val files: ArrayList<MultipartBody.Part> = arrayListOf()

        filesMap.keys.forEach {
            files.add(prepareFilePart(filesMap[it], it))
        }

        return files
    }

    private fun prepareFilePart(
        field: Fields?,
        filePath: String
    ): MultipartBody.Part {
        Log.e(TAG, "prepareFilePart: $filePath")
        val file = File(filePath)

        val fileExt = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString())

        val requestBody =
            if (field?.file_type == Constants.image || field?.type == Constants.SIGNATURE) {
                RequestBody.create("image/png".toMediaTypeOrNull(), file)
            } else {
                RequestBody.create("*/$fileExt".toMediaTypeOrNull(), file)
            }

        return MultipartBody.Part.createFormData(
            field?.slug!!, URLEncoder.encode(file.name, "utf-8"), requestBody
        )

    }


    private fun createPartFromString(descriptionString: String): RequestBody {
        return RequestBody.create(MultipartBody.FORM, descriptionString)
    }

    override suspend fun submitForm(
        submitEntity: SubmitEntity,
        slug: String,
        req: HashMap<String, RequestBody>,
        files: List<MultipartBody.Part>?
    ) {
        Log.e(TAG, "submitForm:formReq "+submitEntity.formReq.toString() )
        val call = source.submitForm(slug, req, files)
        call.enqueue(object : Callback<SubmitFormRes> {
            override fun onResponse(call: Call<SubmitFormRes>, response: Response<SubmitFormRes>) {
                Log.e(TAG, "submitForm:onResponse " + response.raw())

                val code = response.code()
                if (code == 200 || code == 201) {
                    removeSentSubmitFromDB(submitEntity)

                } else {
                    submitEntity.hasFormError = true
                    updateSubmitEntityFromDB(submitEntity)


//                    val jObjError = JSONObject(response.errorBody()?.string())
//                    Log.e("request", "submitForm jObjError-> $jObjError")

                }

            }

            override fun onFailure(call: Call<SubmitFormRes>, t: Throwable) {
                Log.e(TAG, "submitForm:onFailure " + t.message)

            }

        })

    }

    private fun removeSentSubmitFromDB(submitEntity: SubmitEntity) {
        GlobalScope.launch(Dispatchers.Main) {
            submitDao.deleteSubmitEntity(submitEntity.uniqueId)

        }

    }

    private  fun updateSubmitEntityFromDB(submitEntity: SubmitEntity) {
        GlobalScope.launch(Dispatchers.Main) {
            submitDao.save(submitEntity)
        }

    }

    override suspend fun editRowDetail(
        submitEntity: SubmitEntity,
        slug: String,
        req: HashMap<String, RequestBody>,
        files: List<MultipartBody.Part>?
    ): Either<Failure, SubmitFormRes> {
        val call = source.editRowDetail(slug, req, files)
        return try {
            request(call, { it.toSubmitFormRes() }, SubmitFormRes.empty())
        } catch (e: Exception) {
            Either.Left(Failure.Exception)
        }

    }

    override suspend fun cityFieldChoices(
        fieldSlug: String?,
        query: String?
    ): Either<Failure, CityChoicesRes> {
        val call = source.cityFieldChoices(fieldSlug, query)
        return try {
            request(call, { it.toCityChoicesRes() }, CityChoicesRes.empty())
        } catch (e: Exception) {
            Either.Left(Failure.Exception)
        }


    }



}
