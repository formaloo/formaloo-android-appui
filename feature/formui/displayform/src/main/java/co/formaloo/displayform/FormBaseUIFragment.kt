package co.formaloo.displayform

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_CANCELED
import android.app.DatePickerDialog
import android.content.Context.CAMERA_SERVICE
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.location.*
import android.location.Address
import android.net.Uri
import android.os.Build
import android.os.CountDownTimer
import android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS
import android.util.Log
import android.util.SparseIntArray
import android.view.Surface
import android.view.View
import android.view.WindowManager
import android.webkit.URLUtil
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import androidx.work.*
import co.formaloo.common.*
import co.formaloo.common.MainBinding.getDateOnLocale
import co.formaloo.common.extension.visible
import co.formaloo.common.Constants.REQUEST_CAPTURE_IMAGE
import co.formaloo.common.base.BaseFragment
import co.formaloo.common.base.BaseViewModel
import co.formaloo.formCommon.SubmitWorker
import co.formaloo.formCommon.base.TimePickerFragment
import co.formaloo.formCommon.listener.SelectDateListener
import co.formaloo.formCommon.listener.SelectTimeListener
import co.formaloo.formCommon.listener.ViewsListener
import co.formaloo.formCommon.vm.UIViewModel
import co.formaloo.model.form.Fields
import co.formaloo.model.form.Form
import co.formaloo.formfields.getEnDatePicker
import co.formaloo.formfields.ui.CityListFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.mlkit.vision.barcode.Barcode.*
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import org.koin.androidx.viewmodel.ext.android.viewModel

import timber.log.Timber
import java.io.File
import java.io.IOException
import java.util.*
import java.util.concurrent.Executors

open class FormBaseUIFragment : BaseFragment(), ViewsListener, LocationListener,
    SelectDateListener {

    private lateinit var enDatePicker: DatePickerDialog
    val viewModel: UIViewModel by viewModel()
    var _field: Fields? = null
    private var imagePath: String? = null
    var picUri: Uri? = null
    var mMap: GoogleMap? = null
    var fieldFromFullMap: Fields? = null
    var selectedLatLong: LatLng? = null
    var mLocationPermissionGranted = false
    var mLastKnownLocation: Location? = null
    var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    var DEFAULT_ZOOM = 16
    var lm: LocationManager? = null

    val timePaickerFragment =
        TimePickerFragment(object : SelectTimeListener {
            override fun timeSelected(hour: Int, minute: Int) {
                val time = "$hour:$minute"
                if (checkTimeLimit(_field, hour, minute)) {
                    viewModel.initSelectedTime(time)
                    viewModel.addKeyValueToReq(_field?.slug!!, time)
                    viewModel.setErrorToField(Fields(), "")

                } else {
                    viewModel.setErrorToField(
                        _field!!,
                        "${getString(R.string.from)} : ${_field?.from_time ?: "-"}" +
                                "  " +
                                "${getString(R.string.to)} : ${_field?.to_time ?: "-"}"

                    )
                }


            }
        })

    companion object {
        val TAG = FormBaseUIFragment::class.java.simpleName
        const val PERMISSION_CAMERA_REQUEST = 1
        const val PERMISSION_Location_REQUEST = 2
    }

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        when (requestCode) {
            Constants.PICKFILE_RESULT_CODE -> if (resultCode != RESULT_CANCELED) {

                intent?.let {
                    it.data?.let {
                        FileUtils(requireActivity()).getPath(it)?.let { path ->

                            resultIsReady(path)
                        }

                    }
                }

            }
        }
    }

    @SuppressLint("MissingPermission")
    fun updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            when {
                mLocationPermissionGranted -> {
                    mMap?.isMyLocationEnabled = true
                    mMap?.uiSettings?.isMyLocationButtonEnabled = true
                }
                else -> {
                    mMap?.isMyLocationEnabled = false
                    mMap?.uiSettings?.isMyLocationButtonEnabled = false
                    mLastKnownLocation = null
                    getLocationPermission()
                }
            }
        } catch (e: SecurityException) {
        }

        mMap?.setOnMyLocationButtonClickListener {
            isLocationEnabled()

            mLastKnownLocation?.let {
                val latLng = LatLng(mLastKnownLocation!!.latitude, mLastKnownLocation!!.longitude)

                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM.toFloat())
                mMap?.animateCamera(cameraUpdate)
                lm?.removeUpdates(this)
            }

            true
        }
    }

    fun putMarker(it: LatLng, mMap: GoogleMap?) {
        mMap?.clear()
        mMap?.addMarker(MarkerOptions().position(it))

    }

    @SuppressLint("UnsafeOptInUsageError")
    override fun startCamera(
        field: Fields,
        cameraStart: MutableLiveData<Boolean>,
        previewView: PreviewView,
        _scannerValue: MutableLiveData<String>
    ) {
        cameraStart.value = true

        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
            val imageCapture = ImageCapture.Builder()
                .build()

            val ana = ImageAnalysis.Analyzer { imageProxy ->
                val mediaImage = imageProxy.image
                if (mediaImage != null) {
                    val image = InputImage.fromMediaImage(
                        mediaImage,
                        imageProxy.imageInfo.rotationDegrees
                    )

                    // Pass image to an ML Kit Vision API
                    scanBarcodes(image, cameraStart, field, _scannerValue)
                    // ...
                }
            }

            val imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(Executors.newSingleThreadExecutor(), ana)
                }
            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture, imageAnalyzer
                )

            } catch (exc: Exception) {
                Timber.e("Use case binding failed$exc")

            }

        }, ContextCompat.getMainExecutor(requireActivity()))
    }

    private var cityListFragment: CityListFragment? = null

    override fun openCityListDialog(viewmodel: UIViewModel, field: Fields) {
        val fm: FragmentManager = childFragmentManager
        cityListFragment =
            CityListFragment(viewmodel, arrayListOf(), field)

        cityListFragment?.show(fm, "newFolderFragment")

    }

    override fun closeCityDialog() {
        hideKeyboard(requireActivity())
        cityListFragment?.dismiss()
        hideKeyboard(requireActivity())

    }

    /**
     * Prom
     * pts the user for permission to use the device location.
     */
    override fun getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */

        if (!isLocationPermissionGranted(requireActivity())) {
            reqPermissions(
                requireActivity(),
                arrayOf(ACCESS_FINE_LOCATION),
                PERMISSION_Location_REQUEST
            )
        } else {
            mLocationPermissionGranted = true
            isLocationEnabled()

        }
    }

    @SuppressLint("MissingPermission")
    fun isLocationEnabled() {
        lm = requireActivity().getSystemService(LOCATION_SERVICE) as LocationManager
        var gps_enabled = false;
        var network_enabled = false;

        lm?.let { lm ->
            try {
                gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch (ex: Exception) {
            }

            try {
                network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch (ex: Exception) {
            }

            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            lm.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                400,
                1000f,
                this
            ); //You can also use LocationManager.GPS_PROVIDER and LocationManager.PASSIVE_PROVIDER

        }

        if (!gps_enabled && !network_enabled) {
            // notify user
            openLocationPermissionDialog(
                getString(R.string.open_location_settings),
                getString(R.string.cancel),
                getString(R.string.gps_network_not_enabled)
            )

        }
    }

    fun openLocationPermissionDialog(oktBtnTxt: String, cancelBtnTxt: String, msgTxt: String) {
        openSelectDialog(msgTxt, oktBtnTxt, cancelBtnTxt, object : DialogListener {
            override fun performOkButton() {
                startActivity(Intent(ACTION_LOCATION_SOURCE_SETTINGS))

            }

            override fun performCancelButton() {


            }
        }, requireContext())


    }

    fun moveCamera(latLng: LatLng) {
        mMap?.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                latLng, DEFAULT_ZOOM.toFloat()
            )
        )
    }

    fun openFilePickerInten(type: String, action: String) {
        Timber.e("openFilePickerInten 1")

        if (isStoragePermissionGranted(requireActivity())) {
            Timber.e("openFilePickerInten 2")
            openFilePickerInten(requireActivity(), type, action)

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(Constants.PERMISSIONS_EXTERNAL_STORAGE, Constants.PERMISSION_ALL)
            }
        }
    }

    fun updateTheme(form: Form, view: View?) {
        val hexColor = getHexHashtagColorFromRgbStr(form.background_color)
        val window = requireActivity().window

        if (hexColor != null || form.background_image != null) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.statusBarColor =
                ContextCompat.getColor(requireActivity(), android.R.color.transparent);
            window.navigationBarColor =
                ContextCompat.getColor(requireActivity(), android.R.color.transparent);

        }

        if (form.background_image == null && hexColor != null) {
            window.setBackgroundDrawable(ColorDrawable(parseColor(hexColor)))

        } else {
//            loadImage(form.background_image, window, hexColor)

        }

        view?.backgroundTintList = getTxtColor(form)

    }

    fun getTxtColor(form: Form): ColorStateList? {
        getHexHashtagColorFromRgbStr(form.text_color)?.let {
            return ColorStateList(
                arrayOf(
                    intArrayOf(-android.R.attr.state_enabled),
                    intArrayOf(android.R.attr.state_enabled)
                ), intArrayOf(
                    parseColor(it) //disabled
                    , parseColor(it) //enabled
                )
            )

        }
        return null
    }


    fun selectedDateIsReady(
        strDateToShow: String,
        strDateToSend: String,
        field: Fields?
    ) {
        if (checkDateLimit(strDateToSend, field)) {
            viewModel.initSelectedDate(strDateToShow)
            viewModel.addKeyValueToReq(field?.slug!!, strDateToSend)
            viewModel.setErrorToField(Fields(), "")

        } else {
            viewModel.setErrorToField(
                field ?: Fields(),
                "${getString(R.string.from_date)} : ${getDateOnLocale(field?.from_date ?: "") ?: "-"}" +
                        "  " +
                        "${getString(R.string.to_date)} : ${getDateOnLocale(field?.to_date ?: "") ?: "-"}"
            )
        }

    }

    fun checkDateLimit(strDateToSend: String, fields: Fields?): Boolean {
        val dateStr = converStrToDate(strDateToSend)
        val afterDate =
            fields?.from_date == null || dateStr?.after(converStrToDate(fields.from_date!!)) ?: false
        val beforeDate =
            fields?.to_date == null || dateStr?.before(converStrToDate(fields.to_date!!)) ?: false
        return afterDate && beforeDate

    }

    fun checkTimeLimit(_field: Fields?, hour: Int, minute: Int): Boolean {
        val selectedTime = convertTimeStrToLong("$hour:$minute")
        val afterTime =
            _field?.from_time == null || convertTimeStrToLong(_field.from_time!!) < selectedTime
        val toTime =
            _field?.to_time == null || convertTimeStrToLong(_field.to_time!!) > selectedTime

        return afterTime && toTime
    }

    fun openEnDatePicker(field: Fields?) {
        _field = field

        val cal = Calendar.getInstance()
        cal.time = Date()
        converStrToDate("1900-00-01")?.let { date ->
            cal.time = date
        }

        field?.from_date?.let {
            converStrToDate(it)?.let { date ->
                cal.time = date
            }

        }

        enDatePicker = getEnDatePicker(requireActivity(), cal, this)

        enDatePicker.show()
    }

    fun closeEnDatePicker() {
        enDatePicker.dismiss()
    }

    fun resultIsReady(path: String) {
        Timber.e("Path $path");

        if (checkFileSize(_field, path)) {
            viewModel.setErrorToField(
                _field!!,
                "${getString(R.string.file_is_bigger_than_required_size)} : ${_field?.max_size}"
            )
        } else {
            viewModel.addFileToReq(_field, path)

        }

    }

    override fun enDateSelected(year: Int, month: Int, day: Int) {
        val monthInt = month + 1
        val monthStr = if (monthInt.toString().length < 2) {
            "0$monthInt"
        } else {
            "$monthInt"
        }

        val dayStr = if (day.toString().length < 2) {
            "0$day"
        } else {
            "$day"
        }

        val strDate = "$year-$monthStr-$dayStr"

        selectedDateIsReady(strDate, strDate, _field)

    }


    fun checkFileSize(_field: Fields?, path: String): Boolean {
        val file = File(path)
        val file_size: Int = java.lang.String.valueOf(file.length() / 1024).toInt()
        val maxSize = _field?.max_size

        return maxSize != null && file_size > maxSize.toInt()
    }

    fun setTimer(time: String, timerTxv: TextView, timerErrTxv: TextView, mainFormUi: View) {
        val milisInFuture: Long = convertTimeStrToLong(time)

        Timber.e("milisInFuture $milisInFuture")
        val timer = object : CountDownTimer(milisInFuture, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val hour = getHourFromLongMillis(millisUntilFinished)
                val min = getMinFromLongMillis(millisUntilFinished)
                val sec = getSecFromLongMillis(millisUntilFinished)

                fillTextViewOnTick(hour, min, sec)

            }

            private fun fillTextViewOnTick(hour: Long, min: Long, sec: Long) {
                val minim: Long = 60

                if (hour == 0.toLong() && min == 0.toLong() && sec < minim) {
                    timerTxv.background = requireActivity().getDrawable(R.drawable.back_fill_accent)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        timerTxv.setTextColor(requireActivity().getColor(R.color.colorLightGray))
                    }

                } else {


                }

                timerTxv.text = String.format("%02d:%02d:%02d", hour, min, sec)


            }

            override fun onFinish() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    timerErrTxv.background =
                        ColorDrawable(requireActivity().getColor(android.R.color.transparent))
                    mainFormUi.foreground =
                        ColorDrawable(requireActivity().getColor(android.R.color.transparent))

                }
                timerErrTxv.isFocusable = false
                timerErrTxv.visible()
            }
        }

        timerTxv.visible()

        timer.start()

    }


    override fun openTimePicker(field: Fields?) {
        _field = field
        timePaickerFragment.show(childFragmentManager, null)
    }

    override fun removeTimePicker() {
        timePaickerFragment.dismiss()
    }


    override fun openDatePicker(field: Fields?) {
        openEnDatePicker(field)

    }

    override fun removeDatePicker() {
        closeEnDatePicker()

    }

    override fun openFilePicker(field: Fields?, type: String, action: String) {
        _field = field
        openFilePickerInten(type, action)
    }

    override fun openFileCameraPicker(field: Fields?, type: String, action: String) {
        _field = field

        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.select_source))
        builder.setPositiveButton("${getString(R.string.gallery)}    ") { dialog, id ->
            dialog.cancel()
            openFilePickerInten("image/*", action)
        }

        builder.setNeutralButton("${getString(R.string.file_manager)}    ") { dialog, id ->
            dialog.cancel()
            openFilePickerInten("*/*", action)

        }

        builder.setNegativeButton("${getString(R.string.camera)}    ") { dialog, id ->
            if (isStorageCameraPermissionGranted(requireActivity())) {
                Log.e("openFilePickerInten", " 2")

                val file = openCameraIntent(requireActivity())
                Timber.e("file $file")
                file?.let {
                    imagePath = it.absolutePath
                    picUri = Uri.fromFile(it)
                }
                dialog.cancel()

            } else {
                reqPermissions(
                    requireActivity(),
                    arrayOf(WRITE_EXTERNAL_STORAGE, CAMERA),
                    REQUEST_CAPTURE_IMAGE
                )

            }


        }

        builder.create().show()

    }

    override fun openUrl(v: String) {
        if (v.isNotEmpty() && URLUtil.isValidUrl(v)) {
            openUri(requireActivity(), v)
        }
    }

    override fun openFullMap(form: Form?, field: Fields?, selectedLatLong: LatLng?) {


    }

    override fun setUpCameraX(
        fields: Fields,
        cameraStart: MutableLiveData<Boolean>,
        previewView: PreviewView,
        scannerValue: MutableLiveData<String>
    ) {


    }


    fun scanBarcodes(
        image: InputImage,
        cameraStart: MutableLiveData<Boolean>,
        field: Fields,
        _scannerValue: MutableLiveData<String>
    ) {
        // [START set_detector_options]
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                FORMAT_ALL_FORMATS
            )
            .build()

        // [END set_detector_options]

        // [START get_detector]
//        val scanner = BarcodeScanning.getClient()
        // Or, to specify the formats to recognize:
        val scanner = BarcodeScanning.getClient(options)
        // [END get_detector]
        // [START run_detector]
        val result = scanner.process(image)
            .addOnSuccessListener { barcodes ->
                // Task completed successfully
                // [START_EXCLUDE]
                // [START get_barcodes]
                cameraStart.value = false

                for (barcode in barcodes) {
                    val bounds = barcode.boundingBox
                    val corners = barcode.cornerPoints

                    val rawValue = barcode.rawValue

                    val valueType = barcode.valueType
                    // See API reference for complete list of supported types

                    Timber.e("rawValue ${rawValue}")
                    Timber.e("valueType ${valueType}")

                    when (valueType) {
                        TYPE_WIFI -> {
                            val ssid = barcode.wifi!!.ssid
                            val password = barcode.wifi!!.password
                            val type = barcode.wifi!!.encryptionType
                        }
                        TYPE_URL -> {
                            val title = barcode.url!!.title
                            val url = barcode.url!!.url
                        }
                    }

                }

                _scannerValue.value = if (barcodes.size > 0) {
                    val rawValue = barcodes[0].rawValue ?: ""
                    viewModel.addKeyValueToReq(field.slug!!, rawValue)
                    rawValue

                } else {
                    viewModel.removeKeyValueFromReq(field.slug!!)

                    getString(R.string.scanner_cant_read_code)
                }


                // [END get_barcodes]
                // [END_EXCLUDE]
            }
            .addOnFailureListener {
                // Task failed with an exception
                // ...
                Timber.e("addOnFailureListener $it")

            }

        // [END run_detector]
    }

    private val ORIENTATIONS = SparseIntArray()

    init {
        ORIENTATIONS.append(Surface.ROTATION_0, 0)
        ORIENTATIONS.append(Surface.ROTATION_90, 90)
        ORIENTATIONS.append(Surface.ROTATION_180, 180)
        ORIENTATIONS.append(Surface.ROTATION_270, 270)
    }

    /**
     * Get the angle by which an image must be rotated given the device's current
     * orientation.
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Throws(CameraAccessException::class)
    private fun getRotationCompensation(
        cameraId: String,
        activity: Activity,
        isFrontFacing: Boolean
    ): Int {
        // Get the device's current rotation relative to its "native" orientation.
        // Then, from the ORIENTATIONS table, look up the angle the image must be
        // rotated to compensate for the device's rotation.
        val deviceRotation = activity.windowManager.defaultDisplay.rotation
        var rotationCompensation = ORIENTATIONS.get(deviceRotation)

        // Get the device's sensor orientation.
        val cameraManager = activity.getSystemService(CAMERA_SERVICE) as CameraManager
        val sensorOrientation = cameraManager
            .getCameraCharacteristics(cameraId)
            .get(CameraCharacteristics.SENSOR_ORIENTATION)!!

        if (isFrontFacing) {
            rotationCompensation = (sensorOrientation + rotationCompensation) % 360
        } else { // back-facing
            rotationCompensation = (sensorOrientation - rotationCompensation + 360) % 360
        }
        return rotationCompensation
    }

    fun searchLocation(location: String?) {
        var addressList: List<Address>? = null
        if (location != null || location != "") {
            val geocoder = Geocoder(requireActivity())
            try {
                addressList = geocoder.getFromLocationName(location, 50)
                Timber.e("addressList $addressList")

                if (addressList.isNotEmpty()) {
                    val address: Address = addressList[0]

                    val latLng = LatLng(address.latitude, address.longitude)
                    mMap!!.addMarker(MarkerOptions().position(latLng).title(location))
                    mMap!!.animateCamera(CameraUpdateFactory.newLatLng(latLng))
                    Toast.makeText(
                        requireActivity(),
                        address.latitude.toString() + " " + address.longitude,
                        Toast.LENGTH_LONG
                    ).show()
                } else {

                }

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }


    override fun onLocationChanged(location: Location) {
        mLastKnownLocation = location

    }

    fun callWorker() {
        val constraint: Constraints =
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

        val submitWorkRequest: OneTimeWorkRequest = OneTimeWorkRequestBuilder<SubmitWorker>()
            .setConstraints(constraint).build()

        val manager = WorkManager.getInstance(requireContext())
        manager.enqueueUniqueWork("Submit", ExistingWorkPolicy.KEEP, submitWorkRequest);


    }
}

