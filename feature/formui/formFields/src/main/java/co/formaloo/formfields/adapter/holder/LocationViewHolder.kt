package co.formaloo.formfields.adapter.holder

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.view.View
import co.formaloo.common.*
import co.formaloo.formCommon.vm.UIViewModel
import co.formaloo.model.form.Fields
import co.formaloo.model.submit.RenderedData
import co.formaloo.formfields.databinding.LayoutUiLocationItemBinding
import co.formaloo.formfields.setTextColor
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.layout_ui_field_footer.view.*
import kotlinx.android.synthetic.main.layout_ui_field_header.view.*
import org.json.JSONObject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber
import co.formaloo.common.extension.invisible
import co.formaloo.common.extension.visible
import co.formaloo.common.extension.isVisible

class LocationViewHolder(itemView: View) : BaseVH(itemView), KoinComponent,
    OnMapReadyCallback {

    private var initlatlng: LatLng? = null
    val binding = LayoutUiLocationItemBinding.bind(itemView)
    val baseMethod: BaseMethod by inject()
    private var renderedData: RenderedData? = null

    override fun initView(){
        val fieldUiHeader = binding.locationFieldLay
        fillHeaderData(fieldUiHeader.key_txv,fieldUiHeader.desc_txv)

        binding.locationFullBtn.setOnClickListener {
            listener.openFullMap(form,field,selectedLatLong)
        }
        setTextColor(binding.locationFullBtn,form.button_color)

        errStatus.observe(lifeCycleOwner){
            fieldBackgroundUI(binding.loclay,it)
            if (it == true){

                displayErrLay(binding.errLay, binding.errLay.error_txv)

            }
        }
        val context = itemView.context

        fieldRenderedData?.let {
            renderedData = it
        }





        if (fromEdit) {

        } else {

        }
        setUpMap(context, viewmodel, field)



    }

    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    private var mMap: GoogleMap? = null
    private var DEFAULT_ZOOM = 12
    private var PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    private var mLocationPermissionGranted = false
    private var mLastKnownLocation: Location? = null

    private fun setUpMap(context: Context, viewmodel: UIViewModel, field: Fields) {
        val countryCode = baseMethod.contryCode(itemView.context)
        Timber.e("countryCode $countryCode ")

        initlatlng = COUNTRY_ISOS[countryCode]

        Timber.e("countryCode $initlatlng ")

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        binding.map.onCreate(null)
        binding.map.getMapAsync(this)


    }


    private fun hideErr(viewmodel: UIViewModel) {
        binding.errLay.invisible()
        viewmodel.setErrorToField(Fields(), "")

    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap){
        MapsInitializer.initialize(itemView.context)
        mMap = googleMap;

        // Use a custom info window adapter to handle multiple lines of text in the
        // info window contents.

        // Prompt the user for permission.
//        getLocationPermission()
        // Turn on the My Location layer and the related control on the map.
        updateLocationUI()
        // Get the current location of the device and set the position of the map.


        binding.map.onResume();

        mMap?.setOnMapClickListener {
            updateLocationLatLng(it, viewmodel)

        }

        userLocation?.let {
            if (selectedLatLong == null) {
                moveCamera(LatLng(it.latitude, it.longitude))

            } else {

            }
        }

        renderedData?.raw_value?.let { raw_value ->
            val rawValueMap = JSONObject(raw_value.toString())
            Timber.e("rawValueMap $rawValueMap")
            var lat: Double? = null
            var long: Double? = null
            if (rawValueMap.has("lat")) {
                lat = rawValueMap["lat"] as Double
            } else {
            }

            if (rawValueMap.has("long")) {
                long = rawValueMap["long"] as Double
            } else {

            }

            Timber.e("rawValueMap $rawValueMap")

            if (lat != null && long != null) {
                val latLng = LatLng(lat, long)
                putMarker(latLng)
                moveCamera(latLng)


            } else {

            }


        }



        selectedLatLong?.let {
            putMarker(it)
            moveCamera(it)
        }

        if (selectedLatLong != null && locationChanged) {
            updateLocationLatLng(selectedLatLong!!, viewmodel)
        } else {


        }
    }

    val lat="lat"
    val long="long"
    private fun updateLocationLatLng(it: LatLng, viewmodel: UIViewModel) {
        Timber.e("updateLocationLatLng $it")
        putMarker(it)
        moveCamera(it)
        val jsonObject = JSONObject()
        jsonObject.put(lat, it.latitude)
        jsonObject.put(long, it.longitude)


        Timber.e("jsonObject $jsonObject")
        viewmodel.addKeyValueToReq(field.slug!!, jsonObject)
        hideErr(viewmodel)

    }

    private fun putMarker(it: LatLng) {
        mMap?.clear()

        mMap?.addMarker(
            MarkerOptions().position(
                LatLng(
                    it.latitude,
                    it.longitude
                )
            )
        )

        selectedLatLong = LatLng(
            it.latitude,
            it.longitude
        )


    }


    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    @SuppressLint("MissingPermission")
    private fun updateLocationUI() {
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
                    listener.getLocationPermission()
                }
            }
        } catch (e: SecurityException) {
        }
    }


    private fun moveCamera(latLng: LatLng) {
        mMap?.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                latLng, DEFAULT_ZOOM.toFloat()
            )
        )
    }



    val COUNTRY_ISOS: HashMap<String?, LatLng?> = object : HashMap<String?, LatLng?>() {
        init {
            put("AD", LatLng(42.546245, 1.601554))
            put("AE", LatLng(23.424076, 53.847818))
            put("AF", LatLng(33.93911, 67.709953))
            put("AG", LatLng(17.060816, -61.796428))
            put("AI", LatLng(18.220554, -63.068615))
            put("AL", LatLng(41.153332, 20.168331))
            put("AM", LatLng(40.069099, 45.038189))
            put("AN", LatLng(12.226079, -69.060087))
            put("AO", LatLng(-11.202692, 17.873887))
            put("AQ", LatLng(-75.250973, -0.071389))
            put("AR", LatLng(-38.416097, -63.616672))
            put("AS", LatLng(-14.270972, -170.132217))
            put("AT", LatLng(47.516231, 14.550072))
            put("AU", LatLng(-25.274398, 133.775136))
            put("AW", LatLng(12.52111, -69.968338))
            put("AZ", LatLng(40.143105, 47.576927))
            put("BA", LatLng(43.915886, 17.679076))
            put("BB", LatLng(13.193887, -59.543198))
            put("BD", LatLng(23.684994, 90.356331))
            put("BE", LatLng(50.503887, 4.469936))
            put("BF", LatLng(12.238333, -1.561593))
            put("BG", LatLng(42.733883, 25.48583))
            put("BH", LatLng(25.930414, 50.637772))
            put("BI", LatLng(-3.373056, 29.918886))
            put("BJ", LatLng(9.30769, 2.315834))
            put("BM", LatLng(32.321384, -64.75737))
            put("BN", LatLng(4.535277, 114.727669))
            put("BO", LatLng(-16.290154, -63.588653))
            put("BR", LatLng(-14.235004, -51.92528))
            put("BS", LatLng(25.03428, -77.39628))
            put("BT", LatLng(27.514162, 90.433601))
            put("BV", LatLng(-54.423199, 3.413194))
            put("BW", LatLng(-22.328474, 24.684866))
            put("BY", LatLng(53.709807, 27.953389))
            put("BZ", LatLng(17.189877, -88.49765))
            put("CA", LatLng(56.130366, -106.346771))
            put("CC", LatLng(-12.164165, 96.870956))
            put("CD", LatLng(-4.038333, 21.758664))
            put("CF", LatLng(6.611111, 20.939444))
            put("CG", LatLng(-0.228021, 15.827659))
            put("CH", LatLng(46.818188, 8.227512))
            put("CI", LatLng(7.539989, -5.54708))
            put("CK", LatLng(-21.236736, -159.777671))
            put("CL", LatLng(-35.675147, -71.542969))
            put("CM", LatLng(7.369722, 12.354722))
            put("CN", LatLng(35.86166, 104.195397))
            put("CO", LatLng(4.570868, -74.297333))
            put("CR", LatLng(9.748917, -83.753428))
            put("CU", LatLng(21.521757, -77.781167))
            put("CV", LatLng(16.002082, -24.013197))
            put("CX", LatLng(-10.447525, 105.690449))
            put("CY", LatLng(35.126413, 33.429859))
            put("CZ", LatLng(49.817492, 15.472962))
            put("DE", LatLng(51.165691, 10.451526))
            put("DJ", LatLng(11.825138, 42.590275))
            put("DK", LatLng(56.26392, 9.501785))
            put("DM", LatLng(15.414999, -61.370976))
            put("DO", LatLng(18.735693, -70.162651))
            put("DZ", LatLng(28.033886, 1.659626))
            put("EC", LatLng(-1.831239, -78.183406))
            put("EE", LatLng(58.595272, 25.013607))
            put("EG", LatLng(26.820553, 30.802498))
            put("EH", LatLng(24.215527, -12.885834))
            put("ER", LatLng(15.179384, 39.782334))
            put("ES", LatLng(40.463667, -3.74922))
            put("ET", LatLng(9.145, 40.489673))
            put("FI", LatLng(61.92411, 25.748151))
            put("FJ", LatLng(-16.578193, 179.414413))
            put("FK", LatLng(-51.796253, -59.523613))
            put("FM", LatLng(7.425554, 150.550812))
            put("FO", LatLng(61.892635, -6.911806))
            put("FR", LatLng(46.227638, 2.213749))
            put("GA", LatLng(-0.803689, 11.609444))
            put("GB", LatLng(55.378051, -3.435973))
            put("GD", LatLng(12.262776, -61.604171))
            put("GE", LatLng(42.315407, 43.356892))
            put("GF", LatLng(3.933889, -53.125782))
            put("GG", LatLng(49.465691, -2.585278))
            put("GH", LatLng(7.946527, -1.023194))
            put("GI", LatLng(36.137741, -5.345374))
            put("GL", LatLng(71.706936, -42.604303))
            put("GM", LatLng(13.443182, -15.310139))
            put("GN", LatLng(9.945587, -9.696645))
            put("GP", LatLng(16.995971, -62.067641))
            put("GQ", LatLng(1.650801, 10.267895))
            put("GR", LatLng(39.074208, 21.824312))
            put("GS", LatLng(-54.429579, -36.587909))
            put("GT", LatLng(15.783471, -90.230759))
            put("GU", LatLng(13.444304, 144.793731))
            put("GW", LatLng(11.803749, -15.180413))
            put("GY", LatLng(4.860416, -58.93018))
            put("GZ", LatLng(31.354676, 34.308825))
            put("HK", LatLng(22.396428, 114.109497))
            put("HM", LatLng(-53.08181, 73.504158))
            put("HN", LatLng(15.199999, -86.241905))
            put("HR", LatLng(45.1, 15.2))
            put("HT", LatLng(18.971187, -72.285215))
            put("HU", LatLng(47.162494, 19.503304))
            put("ID", LatLng(-0.789275, 113.921327))
            put("IE", LatLng(53.41291, -8.24389))
            put("IL", LatLng(31.046051, 34.851612))
            put("IM", LatLng(54.236107, -4.548056))
            put("IN", LatLng(20.593684, 78.96288))
            put("IO", LatLng(-6.343194, 71.876519))
            put("IQ", LatLng(33.223191, 43.679291))
            put("IR", LatLng(32.427908, 53.688046))
            put("IS", LatLng(64.963051, -19.020835))
            put("IT", LatLng(41.87194, 12.56738))
            put("JE", LatLng(49.214439, -2.13125))
            put("JM", LatLng(18.109581, -77.297508))
            put("JO", LatLng(30.585164, 36.238414))
            put("JP", LatLng(36.204824, 138.252924))
            put("KE", LatLng(-0.023559, 37.906193))
            put("KG", LatLng(41.20438, 74.766098))
            put("KH", LatLng(12.565679, 104.990963))
            put("KI", LatLng(-3.370417, -168.734039))
            put("KM", LatLng(-11.875001, 43.872219))
            put("KN", LatLng(17.357822, -62.782998))
            put("KP", LatLng(40.339852, 127.510093))
            put("KR", LatLng(35.907757, 127.766922))
            put("KW", LatLng(29.31166, 47.481766))
            put("KY", LatLng(19.513469, -80.566956))
            put("KZ", LatLng(48.019573, 66.923684))
            put("LA", LatLng(19.85627, 102.495496))
            put("LB", LatLng(33.854721, 35.862285))
            put("LC", LatLng(13.909444, -60.978893))
            put("LI", LatLng(47.166, 9.555373))
            put("LK", LatLng(7.873054, 80.771797))
            put("LR", LatLng(6.428055, -9.429499))
            put("LS", LatLng(-29.609988, 28.233608))
            put("LT", LatLng(55.169438, 23.881275))
            put("LU", LatLng(49.815273, 6.129583))
            put("LV", LatLng(56.879635, 24.603189))
            put("LY", LatLng(26.3351, 17.228331))
            put("MA", LatLng(31.791702, -7.09262))
            put("MC", LatLng(43.750298, 7.412841))
            put("MD", LatLng(47.411631, 28.369885))
            put("ME", LatLng(42.708678, 19.37439))
            put("MG", LatLng(-18.766947, 46.869107))
            put("MH", LatLng(7.131474, 171.184478))
            put("MK", LatLng(41.608635, 21.745275))
            put("ML", LatLng(17.570692, -3.996166))
            put("MM", LatLng(21.913965, 95.956223))
            put("MN", LatLng(46.862496, 103.846656))
            put("MO", LatLng(22.198745, 113.543873))
            put("MP", LatLng(17.33083, 145.38469))
            put("MQ", LatLng(14.641528, -61.024174))
            put("MR", LatLng(21.00789, -10.940835))
            put("MS", LatLng(16.742498, -62.187366))
            put("MT", LatLng(35.937496, 14.375416))
            put("MU", LatLng(-20.348404, 57.552152))
            put("MV", LatLng(3.202778, 73.22068))
            put("MW", LatLng(-13.254308, 34.301525))
            put("MX", LatLng(23.634501, -102.552784))
            put("MY", LatLng(4.210484, 101.975766))
            put("MZ", LatLng(-18.665695, 35.529562))
            put("NA", LatLng(-22.95764, 18.49041))
            put("NC", LatLng(-20.904305, 165.618042))
            put("NE", LatLng(17.607789, 8.081666))
            put("NF", LatLng(-29.040835, 167.954712))
            put("NG", LatLng(9.081999, 8.675277))
            put("NI", LatLng(12.865416, -85.207229))
            put("NL", LatLng(52.132633, 5.291266))
            put("NO", LatLng(60.472024, 8.468946))
            put("NP", LatLng(28.394857, 84.124008))
            put("NR", LatLng(-0.522778, 166.931503))
            put("NU", LatLng(-19.054445, -169.867233))
            put("NZ", LatLng(-40.900557, 174.885971))
            put("OM", LatLng(21.512583, 55.923255))
            put("PA", LatLng(8.537981, -80.782127))
            put("PE", LatLng(-9.189967, -75.015152))
            put("PF", LatLng(-17.679742, -149.406843))
            put("PG", LatLng(-6.314993, 143.95555))
            put("PH", LatLng(12.879721, 121.774017))
            put("PK", LatLng(30.375321, 69.345116))
            put("PL", LatLng(51.919438, 19.145136))
            put("PM", LatLng(46.941936, -56.27111))
            put("PN", LatLng(-24.703615, -127.439308))
            put("PR", LatLng(18.220833, -66.590149))
            put("PS", LatLng(31.952162, 35.233154))
            put("PT", LatLng(39.399872, -8.224454))
            put("PW", LatLng(7.51498, 134.58252))
            put("PY", LatLng(-23.442503, -58.443832))
            put("QA", LatLng(25.354826, 51.183884))
            put("RE", LatLng(-21.115141, 55.536384))
            put("RO", LatLng(45.943161, 24.96676))
            put("RS", LatLng(44.016521, 21.005859))
            put("RU", LatLng(61.52401, 105.318756))
            put("RW", LatLng(-1.940278, 29.873888))
            put("SA", LatLng(23.885942, 45.079162))
            put("SB", LatLng(-9.64571, 160.156194))
            put("SC", LatLng(-4.679574, 55.491977))
            put("SD", LatLng(12.862807, 30.217636))
            put("SE", LatLng(60.128161, 18.643501))
            put("SG", LatLng(1.352083, 103.819836))
            put("SH", LatLng(-24.143474, -10.030696))
            put("SI", LatLng(46.151241, 14.995463))
            put("SJ", LatLng(77.553604, 23.670272))
            put("SK", LatLng(48.669026, 19.699024))
            put("SL", LatLng(8.460555, -11.779889))
            put("SM", LatLng(43.94236, 12.457777))
            put("SN", LatLng(14.497401, -14.452362))
            put("SO", LatLng(5.152149, 46.199616))
            put("SR", LatLng(3.919305, -56.027783))
            put("ST", LatLng(0.18636, 6.613081))
            put("SV", LatLng(13.794185, -88.89653))
            put("SY", LatLng(34.802075, 38.996815))
            put("SZ", LatLng(-26.522503, 31.465866))
            put("TC", LatLng(21.694025, -71.797928))
            put("TD", LatLng(15.454166, 18.732207))
            put("TF", LatLng(-49.280366, 69.348557))
            put("TG", LatLng(8.619543, 0.824782))
            put("TH", LatLng(15.870032, 100.992541))
            put("TJ", LatLng(38.861034, 71.276093))
            put("TK", LatLng(-8.967363, -171.855881))
            put("TL", LatLng(-8.874217, 125.727539))
            put("TM", LatLng(38.969719, 59.556278))
            put("TN", LatLng(33.886917, 9.537499))
            put("TO", LatLng(-21.178986, -175.198242))
            put("TR", LatLng(38.963745, 35.243322))
            put("TT", LatLng(10.691803, -61.222503))
            put("TV", LatLng(-7.109535, 177.64933))
            put("TW", LatLng(23.69781, 120.960515))
            put("TZ", LatLng(-6.369028, 34.888822))
            put("UA", LatLng(48.379433, 31.16558))
            put("UG", LatLng(1.373333, 32.290275))
            put("US", LatLng(37.09024, -95.712891))
            put("UY", LatLng(-32.522779, -55.765835))
            put("UZ", LatLng(41.377491, 64.585262))
            put("VA", LatLng(41.902916, 12.453389))
            put("VC", LatLng(12.984305, -61.287228))
            put("VE", LatLng(6.42375, -66.58973))
            put("VG", LatLng(18.420695, -64.639968))
            put("VI", LatLng(18.335765, -64.896335))
            put("VN", LatLng(14.058324, 108.277199))
            put("VU", LatLng(-15.376706, 166.959158))
            put("WF", LatLng(-13.768752, -177.156097))
            put("WS", LatLng(-13.759029, -172.104629))
            put("XK", LatLng(42.602636, 20.902977))
            put("YE", LatLng(15.552727, 48.516388))
            put("YT", LatLng(-12.8275, 45.166244))
            put("ZA", LatLng(-30.559482, 22.937506))
            put("ZM", LatLng(-13.133897, 27.849332))
            put("ZW", LatLng(-19.015438, 29.154857))
        }
    }

}