package co.formaloo.formCommon.vm

import android.util.ArrayMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import co.formaloo.common.BaseMethod
import co.formaloo.common.base.BaseViewModel
import co.formaloo.model.cityChoices.CityChoicesData
import co.formaloo.model.cityChoices.CityChoicesObject
import co.formaloo.model.cityChoices.CityChoicesRes
import co.formaloo.model.form.Fields
import co.formaloo.model.form.Form
import co.formaloo.model.form.SubmitEntity
import co.formaloo.model.form.phoneVerification.SmsOtp
import co.formaloo.model.form.phoneVerification.requestToVerify.RequestVerifivcationRes
import co.formaloo.model.form.phoneVerification.verify.PhoneVerificationData
import co.formaloo.model.form.phoneVerification.verify.PhoneVerifivcationRes
import co.formaloo.repository.FormzRepo
import co.formaloo.repository.submit.SubmitRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import timber.log.Timber
import java.io.File
import kotlin.random.Random

class UIViewModel(private val submitRepo: SubmitRepo, private val formsRepo: FormzRepo) :
    BaseViewModel() {
    private var fieldSlug: String? = null
    private var cityQuery: String? = null
    private var formSlug: String? = null
    private var rowSlug: String? = null
    private var formReqList = HashMap<String, String>()
    private var fileList = HashMap<String, Fields>()

    private var requestPhoneVerifiyReq: RequestBody? = null
    private var verifyPhoneVerifiyReq: RequestBody? = null
    private var uuid: String = ""

    private var files: ArrayList<MultipartBody.Part> = arrayListOf()


    private val _resetData = MutableLiveData<Boolean>().apply { value = null }
    val resetData: LiveData<Boolean> = _resetData

    private val _form = MutableLiveData<Form>().apply { value = null }
    val form: LiveData<Form> = _form

    private val _submitedData = MutableLiveData<Boolean>().apply { value = null }
    val submitedData: LiveData<Boolean> = _submitedData

    private val _isLoading = MutableLiveData<Boolean>().apply { value = null }
    val isLoading: LiveData<Boolean> = _isLoading

    private val _fields = MutableLiveData<ArrayList<Fields>>().apply { value = null }
    val fields: LiveData<ArrayList<Fields>> = _fields

    private val _errorField = MutableLiveData<Fields>().apply { value = null }
    val errorField: LiveData<Fields> = _errorField
    private val _fieldFielName = MutableLiveData<Fields>().apply { value = null }
    val fieldFileName: LiveData<Fields> = _fieldFielName

    private val _selectedTime = MutableLiveData<String>().apply { value = null }
    val selectedTime: LiveData<String> = _selectedTime
    private val _selectedDate = MutableLiveData<String>().apply { value = null }
    val selectedDate: LiveData<String> = _selectedDate

    private val _phoneToVerify = MutableLiveData<String>().apply { value = null }
    val phoneToVerify: LiveData<String> = _phoneToVerify
    private val _requestPhoneData = MutableLiveData<SmsOtp>().apply { value = null }
    val requestPhoneData: LiveData<SmsOtp> = _requestPhoneData
    private val _verifyPhoneData = MutableLiveData<PhoneVerificationData>().apply { value = null }
    val verifyPhoneData: LiveData<PhoneVerificationData> = _verifyPhoneData
    private val _verifyLoading = MutableLiveData<Boolean>().apply { value = null }
    val verifyLoading: LiveData<Boolean> = _verifyLoading
    private val _npsPos = MutableLiveData<Int>().apply { value = null }
    val npsPos: LiveData<Int> = _npsPos
    private val _cityChoicesData = MutableLiveData<CityChoicesData>().apply { value = null }
    val cityChoicesData: LiveData<CityChoicesData> = _cityChoicesData
    private val _selectedCity = MutableLiveData<CityChoicesObject>().apply { value = null }
    val selectedCity: LiveData<CityChoicesObject> = _selectedCity

    private var requiredField = arrayListOf<Fields>()

    fun updateSelectedCity(city: CityChoicesObject) {
        _selectedCity.value = city
    }

    fun getCityFieldChoices() = viewModelScope.launch {
        Timber.e("fieldSlug $fieldSlug")
        val result =
            async(Dispatchers.IO) { formsRepo.cityFieldChoices(fieldSlug, cityQuery) }.await()
        result.either(::handleFailure, ::handleCityChoicesFieldData)

    }

    private fun handleCityChoicesFieldData(res: CityChoicesRes) {
        res.let { it ->
            it.data?.let {
                if (it.objects?.isNotEmpty() == true) {
                    _selectedCity.value = it.objects?.get(0)
                }
                _cityChoicesData.value = it
            }
        }

    }

    fun searchCityFieldChoices() = viewModelScope.launch {
        Timber.e("fieldSlug $fieldSlug")
        val result =
            async(Dispatchers.IO) { formsRepo.cityFieldChoices(fieldSlug, cityQuery) }.await()
        result.either(::handleFailure, ::handleSearchCityChoicesFieldData)

    }

    private fun handleSearchCityChoicesFieldData(res: CityChoicesRes) {
        res.let { it ->
            it.data?.let {
                _cityChoicesData.value = it
            }
        }

    }


    fun requestPhoneVerification() = viewModelScope.launch {
        Timber.e("requestPhoneVerification ")
        val result =
            async(Dispatchers.IO) { formsRepo.requestPhoneVerification(requestPhoneVerifiyReq) }.await()
        result.either(::handleFailure, ::handleRequestPhoneData)

    }

    private fun handleRequestPhoneData(res: RequestVerifivcationRes) {
        res.data?.objectData?.sms_otp?.let { it ->
            it.uuid?.let {
                uuid = it
            }
            _requestPhoneData.value = it

        }

    }


    fun verifyPhoneVerification() = viewModelScope.launch {
        Timber.e("requestPhoneVerification ")
        val result =
            async(Dispatchers.IO) {
                formsRepo.verifyPhoneVerification(
                    uuid,
                    verifyPhoneVerifiyReq
                )
            }.await()
        result.either(::handleFailure, ::handleVerifyPhoneData)

    }

    private fun handleVerifyPhoneData(res: PhoneVerifivcationRes) {
        res.data?.let { it ->
            _verifyPhoneData.value = it

        }

    }

    fun initFieldSlug(slug: String) {
        fieldSlug = slug
    }

    fun initCityQuery(query: String) {
        cityQuery = query
    }

    fun initRowSlug(slug: String) {
        rowSlug = slug
    }

    fun addKeyValueToReq(slug: String, value: Any) {
        formReqList[slug] = value.toString()
    }

    fun removeKeyValueFromReq(slug: String) {
        if (formReqList.keys.contains(slug)) {
            formReqList.remove(slug)
        }
    }

    fun addFileToReq(field: Fields?, value: String) {
        field?.let {
            fileList[value] = field
            initFileName(field.slug, value)
        }

    }

    fun removeFileFromReq(field: Fields?) {
        field?.let {
            var key: String? = null
            fileList.keys.forEach {
                if (fileList[it] == field) {
                    key = it
                } else {

                }
            }
            fileList.remove(key)

            initFileName(null, "")
        }

    }

    fun initFileName(slug: String?, filePath: String) {
        val file = File(filePath)
        val fielField = Fields()
        fielField.slug = slug
        fielField.title = file.name
        _fieldFielName.value = fielField

    }

    fun removeFile(slug: String?) {
        initFileName(null, "")
        val fileList = files

        fileList.forEach { mp ->
            Timber.e("removeFile ${mp.body}")

            mp.headers?.let { headers ->
                Timber.e("headers $headers")
                if (slug != null && headers.toString().contains(slug)) {
                    files.remove(mp)
                }

            }
        }
    }

    fun showLoading() {

        _isLoading.value = true
    }

    fun hideLoading() {
        _verifyLoading.value = false
        _isLoading.value = false

    }

    fun initSelectedTime(time: String) {
        Timber.e("initSelectedTime $time")
        _selectedTime.value = time
    }

    fun initSelectedDate(date: String) {
        Timber.e("initSelectedDate $date")
        _selectedDate.value = date
    }

    fun saveEditSubmitToDB(newRow: Boolean) = viewModelScope.launch {
        submitRepo.saveSubmit(
            SubmitEntity(
                0,
                Random.nextInt(),
                false,
                newRow,
                rowSlug,
                formSlug,
                formReqList,
                fileList
            )
        )
        _submitedData.value = true

    }

    fun errorFind(it: JSONObject, baseMethod: BaseMethod) {
        it.keys().forEach { key ->
            val err = baseMethod.retrieveErr(it, key)
            val errField = Fields()
            errField.slug = key
            errField.title = err
            _errorField.value = errField
        }


    }

    fun setErrorToField(it: Fields, msg: String) {
        val errField = Fields()
        errField.slug = it.slug
        errField.title = msg
        _errorField.value = errField

    }


    fun hideErrorField() {
        val fields = Fields()
        fields.slug = null
        fields.title = ""

        _errorField.value = fields
    }

    fun verifyBtnClicked(fields: Fields) {
        _verifyLoading.value = true

        val req = ArrayMap<String, Any>()
        req["form"] = formSlug
        req["phone"] = phoneToVerify.value

        requestPhoneVerifiyReq = createRB(req)

        requestPhoneVerification()
    }

    fun initPhoneNumber(phoneNumber: String) {
        Timber.e("$phoneNumber")
        _phoneToVerify.value = phoneNumber
    }

    fun sendpinCode(pincode: String) {
        _verifyLoading.value = true

        val req = ArrayMap<String, Any>()
        req["code"] = pincode

        verifyPhoneVerifiyReq = createRB(req)

        verifyPhoneVerification()
    }

    private fun createRB(req: ArrayMap<String, Any>): RequestBody {
        return RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            JSONObject(req).toString()
        )
    }

    fun npsBtnClicked(field: Fields, pos: Int) {
        _npsPos.value = pos
        addKeyValueToReq(field.slug!!, pos)
    }

    fun reuiredField(it: Fields) {
        requiredField.add(it)
    }

    fun checkRequiredField(): Boolean {
        val fileFieldsSlug = arrayListOf<String>()
        fileList.keys.forEach {
            fileList[it]?.slug?.let {
                fileFieldsSlug.add(it)
            }
        }

        requiredField.forEach {
            Timber.e("requiredField ${it.title}")

            if (!formReqList.keys.contains(it.slug) && !fileFieldsSlug.contains(it.slug)) {
                return false
            } else {

            }
        }

        return true
    }

    fun resetData() {
        formReqList = HashMap()
        fileList = HashMap()

        requestPhoneVerifiyReq = null
        verifyPhoneVerifiyReq = null
        uuid = ""

        _npsPos.value = -1
        _resetData.value = true
    }


}
