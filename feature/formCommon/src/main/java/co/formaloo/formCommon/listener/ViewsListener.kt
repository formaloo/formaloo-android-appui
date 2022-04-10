package co.formaloo.formCommon.listener

import androidx.camera.view.PreviewView
import androidx.lifecycle.MutableLiveData
import co.formaloo.formCommon.vm.UIViewModel
import co.formaloo.model.form.Fields
import co.formaloo.model.form.Form
import com.google.android.gms.maps.model.LatLng

interface ViewsListener {
    fun openTimePicker(field: Fields?)
    fun removeTimePicker()
    fun openDatePicker(field: Fields?)
    fun removeDatePicker()
    fun openFilePicker(field: Fields?, type: String, actionGetContent: String)
    fun openFileCameraPicker(field: Fields?, type: String, actionGetContent: String)
    fun openUrl(v: String)
    fun openFullMap(form: Form?,field: Fields?,selectedLatLong:LatLng?)
    fun setUpCameraX(
        fields: Fields,
        cameraStart: MutableLiveData<Boolean>,
        previewView: PreviewView,
        scannerValue: MutableLiveData<String>
    )
    fun startCamera(
        fields: Fields,
        cameraStart: MutableLiveData<Boolean>,
        previewView: PreviewView,
        _scannerValue: MutableLiveData<String>
    )

    fun openCityListDialog(viewmodel: UIViewModel, field: Fields)
    fun closeCityDialog()
    fun getLocationPermission()
}
