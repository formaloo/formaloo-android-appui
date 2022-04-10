package co.formaloo.displayform

import android.Manifest.permission.CAMERA
import android.annotation.SuppressLint
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.camera.view.PreviewView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import co.formaloo.common.*
import co.formaloo.common.Constants.ERRORS
import co.formaloo.common.Constants.FORM_ERRORS
import co.formaloo.common.Constants.GENERAL_ERRORS
import co.formaloo.common.Constants.REQUEST_CAPTURE_IMAGE
import co.formaloo.common.extension.invisible
import co.formaloo.common.extension.visible
import co.formaloo.displayform.databinding.FragmentDisplayFormSinglePageBinding
import co.formaloo.formCommon.BlockListener
import co.formaloo.formCommon.vm.BoardsViewModel
import co.formaloo.model.form.Fields
import co.formaloo.model.form.Form
import co.formaloo.model.submit.Row
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber


class DisplayFormSinglePageFragment : FormBaseUIFragment(), OnMapReadyCallback {

    private var blockSlug: String? = null
    private lateinit var binding: FragmentDisplayFormSinglePageBinding
    private lateinit var fieldsLM: LinearLayoutManager
    private lateinit var fieldsUiAdapter: FieldsUiAdapter
    private var form: Form? = null
    private var _row: Row? = null
    private val boardsVM: BoardsViewModel by viewModel()
    private var blockListener: BlockListener? = null

    companion object {
        fun newInstance(blockItemSlug: String?,blockSlug: String?) = DisplayFormSinglePageFragment().apply {
            arguments = Bundle().apply {
                Timber.e("newInstance $blockSlug")
                putSerializable(Constants.SLUG, blockSlug)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkBundle()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDisplayFormSinglePageBinding.inflate(inflater, container, false)
        binding.viewmodel = viewModel
        binding.listener = this
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()

        setUpListeners()

        initMap()
    }

    private fun initView(form: Form) {

        binding.formLay.visible()
        binding.formProgress.invisible()
        binding.fromSubmitBtn.visible()

        val rowRenderedData = _row?.rendered_data ?: HashMap()
        fieldsUiAdapter = FieldsUiAdapter(this, form, viewModel, rowRenderedData)

        fieldsLM = LinearLayoutManager(requireContext())

        binding.viewsRec.apply {
            adapter = fieldsUiAdapter
            layoutManager = fieldsLM
        }

        form.fields_list?.let {
            fieldsUiAdapter.setCollection(it)

            it.map {
                val required = it.required
                if (required != null && required) {
                    viewModel.reuiredField(it)

                } else {

                }

            }
        }

        form.time_limit?.let {
            setTimer(it, binding.timerTxv, binding.timerErrTxv, binding.mainFormUi)
        }

    }

    private fun checkBundle() {
        arguments?.let { it ->
            it.getString(Constants.SLUG)?.let {
                Timber.e("checkBundle $it")

                blockSlug = it
            }

            it.getSerializable("row")?.let {
                if (it is Row) {
                    viewModel.initRowSlug(it.slug!!)
                    _row = it
                }
            }
        }

    }

    private fun initData() {
        boardsVM.retrieveSharedBlockDetail(BuildConfig.APPUI_ADDRESS, blockSlug ?: "")


        boardsVM.block.observe(viewLifecycleOwner) { it ->
            it?.let { block ->
                (block.form as Form)?.let {
                    binding.form = it
                    form = it
                    initView(it)

                }

            }
        }

        viewModel.submitedData.observe(viewLifecycleOwner) { it ->
            val successMessage = form?.success_message

            it?.let {

                var msg = getString(R.string.updates_saved)

                successMessage?.let {
                    msg = Html.fromHtml(it).toString()
                }

                openSubmitFormDialog(msg)

            }
        }

        boardsVM.failure.observe(viewLifecycleOwner) {
            it?.let {
                getBlockFromDb(blockSlug ?: "")
                viewModel.hideLoading()

            }

        }

        viewModel.errorField.observe(viewLifecycleOwner) { errField ->
            errField?.let {
                Timber.e("observeErrorField")

                fieldsUiAdapter.collection.singleOrNull() { fieldFromList ->
                    fieldFromList.slug.equals(errField.slug)
                }?.let { field ->
                    val pos = fieldsUiAdapter.collection.indexOf(field)

                    Timber.e("scroll pos $pos")
                    binding.viewsRec.layoutManager?.scrollToPosition(pos)

                }
            }
        }

    }

    private fun getBlockFromDb(blockSlug: String) {
        boardsVM.retrieveBlockFromDB(blockSlug)

    }

    private fun setUpListeners() {

        binding.searchSv.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchLocation(query)

                return true

            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })

        binding.closeMapBtn.setOnClickListener {
            onBackPressed()
        }
        binding.okMapBtn.setOnClickListener {
            updateLocationFieldSelectionLATLNG(selectedLatLong, fieldFromFullMap)
            onBackPressed()
        }

        binding.fromSubmitBtn.setOnClickListener {
            if (_row?.slug != null) {
                openEditFormDialog(getString(R.string.edit_or_save))
            } else {
                submitClicked(true)
            }
        }
    }

    private fun onBackPressed() {
        requireActivity().onBackPressed()
    }

    private fun initMap() {
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

    }

    private fun openEditFormDialog(msgTxt: String) {
        var txt = msgTxt
        if (txt.contains("[\""))
            txt = txt.replace("[\"", "")
        else if (txt.contains("\"]"))
            txt = txt.replace("\"]", "")

        AlertDialog.Builder(requireActivity()).setTitle(txt)
//            .setMessage(msgTxt)
            .setPositiveButton(getString(R.string.submit_new_row)) { _, _ ->
                submitClicked(true)
            }
            .setNegativeButton(getString(R.string.edit_pre)) { _, _ ->
                submitClicked(false)

            }.show()


    }

    private fun openSubmitFormDialog(msgTxt: String) {
        var txt = msgTxt
        if (txt.contains("[\""))
            txt = txt.replace("[\"", "")
        else if (txt.contains("\"]"))
            txt = txt.replace("\"]", "")

        AlertDialog.Builder(requireActivity()).setTitle(txt)
//            .setMessage(msgTxt)
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                onBackPressed()
            }
            .setNegativeButton(getString(R.string.submit_new_row)) { _, _ ->
                Handler().postDelayed({
                    clearAnswers()
                }, 300)
            }.show()


    }

    private fun renderFailure(message: String?) {
        Timber.e("renderFailure $message")
        message?.let {
            try {

                val jObjError = JSONObject(message)
                setErrorsToViews(jObjError)


            } catch (e: Exception) {
                Timber.e("${e.localizedMessage}")

            }
        }
    }

    private fun setErrorsToViews(jObjError: JSONObject) {
        val errors = getJSONObject(jObjError, ERRORS)
        val formErrors = getJSONObject(errors, FORM_ERRORS)
        val gErrors = getJSONArray(errors, GENERAL_ERRORS)

        formErrors.let {
//            viewModel.errorFind(it, baseMethod)

        }
        val retrieveGeneralErr = retrieveJSONArrayFirstItem(gErrors)
        if (retrieveGeneralErr.isNotEmpty())
            showMsg(binding.root.rootView, retrieveGeneralErr)
        Timber.e(retrieveGeneralErr)

    }

    private fun submitClicked(b: Boolean) {
        if (viewModel.checkRequiredField()) {
            viewModel.saveEditSubmitToDB(b)

        } else {
            showMsg(binding.uiSv, getString(R.string.fill_required_field))

        }

        callWorker()

    }

    private fun clearAnswers() {
        viewModel.resetData()
        form?.let {
            initView(it)
        }

        binding.uiSv.fullScroll(ScrollView.FOCUS_UP);

    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    @SuppressLint("MissingPermission")
    private fun getDeviceLocation() { /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            when {
                mLocationPermissionGranted -> {
                    updateLocationUI()

                    val locationResult: Task<Location> =
                        mFusedLocationProviderClient?.lastLocation!!
                    locationResult.addOnCompleteListener(
                        requireActivity()
                    ) { task ->
                        when {
                            task.isSuccessful -> { // Set the map's camera position to the current location of the device.
                                mLastKnownLocation = task.result

                                Timber.e("mLastKnownLocation $mLastKnownLocation")
                                if (mLastKnownLocation != null) {
                                    updateLocationFieldUserLocation(mLastKnownLocation)

                                    if (selectedLatLong == null)
                                        moveCamera(
                                            LatLng(
                                                mLastKnownLocation?.latitude!!,
                                                mLastKnownLocation?.longitude!!
                                            )
                                        )
                                }
                            }
                            else -> {
                                Timber.e("Current location is null. Using defaults.")
                                Timber.e("Exception: %s", task.exception)
                                selectedLatLong?.let {
                                    moveCamera(it)
                                }
                                mMap?.uiSettings?.isMyLocationButtonEnabled = true
                            }
                        }
                    }
                }
            }
        } catch (e: SecurityException) {
            Timber.e("${e.message}")
        }
    }

    private fun updateLocationFieldUserLocation(mLastKnownLocation: Location?) {

        val collection = fieldsUiAdapter.collection
        for (item in collection) {
            if (item.type == Constants.LOCATION) {
                fieldsUiAdapter.itemLocationChanged(mLastKnownLocation!!, collection.indexOf(item))
            }
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        when (resultCode) {
            RESULT_OK -> {
                when (requestCode) {
                    Constants.PICKFILE_RESULT_CODE -> {

                        intent?.let { intent ->
                            intent.data?.let {
                                FileUtils(requireActivity()).getPath(it)?.let { path ->
                                    resultIsReady(path)

                                }

                            }


                        }

                    }
                    REQUEST_CAPTURE_IMAGE -> {
                        val bitmap = captureImageResultOK(picUri, requireActivity())
                        bitmap?.let {
                            getPathFromselectedImage(bitmap, requireActivity())?.let {
                                resultIsReady(it)

                            }

                        }


                    }

                }


            }

            RESULT_CANCELED -> {
                // The user canceled the operation.
            }
        }
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {

    }

    override fun onProviderEnabled(p0: String) {

    }

    override fun onProviderDisabled(p0: String) {

    }

    private fun updateLocationFieldSelectionLATLNG(
        selectedLatLong: LatLng?,
        fieldFromFullMap: Fields?
    ) {
        val collection = fieldsUiAdapter.collection
        if (collection.contains(fieldFromFullMap) && selectedLatLong != null) {
            val pos = collection.indexOf(fieldFromFullMap)
            fieldsUiAdapter.itemSelectedLocationChange(selectedLatLong, pos)
        } else {

        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_CAMERA_REQUEST) {
            if (isCameraPermissionGranted(requireActivity())) {
                // start camera
            } else {
                Timber.e(TAG, "no camera permission")
            }
        }
        mLocationPermissionGranted = false
        when (requestCode) {
            PERMISSION_Location_REQUEST -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    mLocationPermissionGranted = true

                    isLocationEnabled()

                }
            }
        }
        updateLocationUI()

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    override fun setUpCameraX(
        field: Fields,
        cameraStart: MutableLiveData<Boolean>,
        previewView: PreviewView,
        _scannerValue: MutableLiveData<String>
    ) {
        if (isCameraPermissionGranted(requireActivity())) {
            // startCamera
            startCamera(field, cameraStart, previewView, _scannerValue)
        } else {
            reqPermissions(requireActivity(), arrayOf(CAMERA), PERMISSION_CAMERA_REQUEST)
        }

    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        getDeviceLocation()

        selectedLatLong?.let {
            moveCamera(it)
            putMarker(it, mMap)
            selectedLatLong = it

        }

        mMap?.setOnMapClickListener {
            putMarker(it, mMap)
            selectedLatLong = it

        }
    }

    override fun openFullMap(form: Form?, field: Fields?, selectedLatLong: LatLng?) {
        fieldFromFullMap = field
        this.selectedLatLong = selectedLatLong
        // Prompt the user for permission.
        getLocationPermission()

        binding.fullMapLay.visible()

    }

    fun setListener(blockListener: BlockListener) {
        this.blockListener = blockListener
    }

//    override fun onBackPressed() {
//        if (binding.fullMapLay.isVisible()) {
//            binding.fullMapLay.invisible()
//        } else {
//            super.onBackPressed()
//
//        }
//    }


}
