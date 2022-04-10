package co.formaloo.displayform

import android.Manifest.permission.CAMERA
import android.annotation.SuppressLint
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.view.PreviewView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import co.formaloo.common.*
import co.formaloo.common.BuildConfig
import co.formaloo.common.Constants.REQUEST_CAPTURE_IMAGE
import co.formaloo.displayform.databinding.FragmentDisplayFieldsBinding
import co.formaloo.formCommon.vm.BoardsViewModel
import co.formaloo.model.form.Fields
import co.formaloo.model.form.Form
import co.formaloo.model.submit.Row
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber


class DisplayFieldFragment : FormBaseUIFragment(), OnMapReadyCallback {

    private var blockSlug: String? = null
    private lateinit var binding: FragmentDisplayFieldsBinding
    private lateinit var fieldsLM: LinearLayoutManager
    private lateinit var fieldsUiAdapter: FieldsUiAdapter
    private var form: Form? = null
    private var _row: Row? = null
    private val boardsVM: BoardsViewModel by viewModel()

    companion object {
        fun newInstance(blockSlug: String?) = DisplayFieldFragment().apply {
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
        binding = FragmentDisplayFieldsBinding.inflate(inflater, container, false)
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
                    binding.form=it

                    form = it
                    initView(it)

                }

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


    }

    private fun onBackPressed() {
        requireActivity().onBackPressed()
    }

    private fun initMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

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

}
