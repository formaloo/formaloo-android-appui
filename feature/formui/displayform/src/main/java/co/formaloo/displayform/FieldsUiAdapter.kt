package co.formaloo.displayform

import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import co.formaloo.common.Constants
import co.formaloo.common.Constants.DATE
import co.formaloo.common.Constants.DROPDOWN
import co.formaloo.common.Constants.FILE
import co.formaloo.common.Constants.Like_Dislike
import co.formaloo.common.Constants.MATRIX
import co.formaloo.common.Constants.MULTI_SELECT
import co.formaloo.common.Constants.PHONE_VERIFICATION
import co.formaloo.common.Constants.SECTION
import co.formaloo.common.Constants.SIGNATURE
import co.formaloo.common.Constants.SINGLE_SELECT
import co.formaloo.common.Constants.TIME
import co.formaloo.common.Constants.YESNO
import co.formaloo.common.Constants.nps
import co.formaloo.common.Constants.star
import co.formaloo.formCommon.listener.ViewsListener
import co.formaloo.formCommon.vm.UIViewModel
import co.formaloo.model.form.Fields
import co.formaloo.model.form.Form
import co.formaloo.model.submit.RenderedData
import co.formaloo.formfields.adapter.holder.*
import com.google.android.gms.maps.model.LatLng
import timber.log.Timber


class FieldsUiAdapter(
    private val listener: ViewsListener,
    private val form: Form,
    private val viewmodel: UIViewModel,
    private val rowRenderedData: Map<String, RenderedData>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var userLocation: Location? = null
    private var selectedLatLong: LatLng? = null
    private var locationChanged: Boolean = false

    private val TYPE_DROP_DOWN = 0
    private val TYPE_MULTI = 1
    private val TYPE_SIGNLE = 2
    private val TYPE_EDT = 3
    private val TYPE_LIKE_DISLIKE = 4
    private val TYPE_STAR = 5
    private val TYPE_NPS = 6
    private val TYPE_FILE = 7
    private val TYPE_SECTION = 8
    private val TYPE_TIME = 9
    private val TYPE_DATE = 10
    private val TYPE_MATRIX = 11
    private val TYPE_PHONE_VERIFICATION = 12
    private val TYPE_SIGNATURE = 13
    private val TYPE_CSAT = 14
    private val TYPE_PRODUCT = 15
    private val TYPE_VIDEO = 16
    private val TYPE_LOCATION = 17
    private val TYPE_BARCODE = 18
    private val TYPE_CITY = 19
    private val TYPE_TABLE = 20


    internal var collection = arrayListOf<Fields>()

    fun setCollection(items: ArrayList<Fields>) {
        collection = items
        notifyDataSetChanged()

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val itemView: View
        when (viewType) {
            TYPE_DROP_DOWN -> {
                itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_ui_dropdown_item, parent, false);
                return DropDownViewHolder(itemView)
            }
            TYPE_MULTI -> {
                itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_ui_multi_item, parent, false);
                return MultiViewHolder(itemView)
            }
            TYPE_SIGNLE -> {
                itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_ui_signle_item, parent, false);
                return SingleViewHolder(itemView)
            }
            TYPE_EDT -> {
                itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_ui_edt_item, parent, false);
                return TextViewHolder(itemView)
            }
            TYPE_LIKE_DISLIKE -> {
                itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_ui_like_dislike_item, parent, false);
                return LikeDislikeViewHolder(itemView)
            }
            TYPE_STAR -> {
                itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_ui_star_item, parent, false);
                return StarViewHolder(itemView)
            }
            TYPE_CSAT -> {
                itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_ui_csat_item, parent, false);
                return CSATViewHolder(itemView)
            }
            TYPE_PRODUCT -> {
                itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_ui_product_item, parent, false);
                return ProductViewHolder(itemView)
            }
            TYPE_NPS -> {
                itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_ui_nps_item, parent, false);
                return NPSViewHolder(itemView)
            }
            TYPE_FILE -> {
                itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_ui_file_item, parent, false);
                return FileViewHolder(itemView)
            }
            TYPE_SECTION -> {
                itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_ui_section_item, parent, false);
                return SectionViewHolder(itemView)
            }
            TYPE_TIME -> {
                itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_ui_time_item, parent, false);
                return TimeViewHolder(itemView)
            }
            TYPE_DATE -> {
                itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_ui_date_item, parent, false);
                return DateViewHolder(itemView)
            }
            TYPE_MATRIX -> {
                itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_ui_matrix_item_second, parent, false);
                return MatrixViewHolder(itemView)
            }
            TYPE_PHONE_VERIFICATION -> {
                itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_ui_phone_verify_item, parent, false);
                return PhoneVerifyViewHolder(itemView)
            }
            TYPE_SIGNATURE -> {
                itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_ui_signature_item, parent, false);
                return SignatureViewHolder(itemView)
            }
            TYPE_LOCATION -> {
                itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_ui_location_item, parent, false);
                return LocationViewHolder(itemView)
            }
            TYPE_CITY -> {
                itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_ui_city_item_second, parent, false);
                return CityViewHolder(itemView)
            }
            TYPE_TABLE -> {
                itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_ui_table_item, parent, false);
                return TableViewHolder(itemView)
            }
            TYPE_BARCODE -> {
                itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_ui_barcode_item, parent, false);
                return BarCodeViewHolder(itemView)
            }
            TYPE_VIDEO -> {
                itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_ui_video_item, parent, false);
                return VideoViewHolder(itemView)
            }
            else -> {
                itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_ui_edt_item, parent, false);
                return TextViewHolder(itemView)

            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position_: Int) {

        val btnItem = collection[position_]

        (holder as BaseVH).bindItems(
            btnItem,
            position_,
            listener,
            form,
            viewmodel,
            rowRenderedData, false, locationChanged,selectedLatLong,userLocation
        )

    }

    override fun getItemViewType(position: Int): Int {
        val fields = collection[position]
        val type = if (fields.sub_type != null) {
            fields.sub_type
        } else {
            fields.type
        }
        return when (type) {
            DROPDOWN -> {
                TYPE_DROP_DOWN
            }
            YESNO -> {
                TYPE_SIGNLE
            }
            MULTI_SELECT -> {
                TYPE_MULTI
            }
            SINGLE_SELECT -> {
                TYPE_SIGNLE
            }
            Like_Dislike -> {
                TYPE_LIKE_DISLIKE

            }
            Constants.embeded -> {
                TYPE_CSAT

            }
            Constants.PRODUCT -> {
                TYPE_PRODUCT

            }
            Constants.VIDEO -> {
                TYPE_VIDEO

            }
            Constants.LOCATION -> {
                TYPE_LOCATION

            }
            Constants.BARCODE -> {
                TYPE_BARCODE

            }
            Constants.CITY -> {
                TYPE_CITY

            }
            Constants.Table -> {
                TYPE_TABLE

            }
            star -> {
                TYPE_STAR

            }
            nps -> {
                TYPE_NPS

            }
            FILE -> {
                TYPE_FILE

            }
            SECTION -> {
                TYPE_SECTION

            }
            TIME -> {
                TYPE_TIME

            }
            DATE -> {
                TYPE_DATE

            }
            MATRIX -> {
                TYPE_MATRIX

            }
            PHONE_VERIFICATION -> {
                TYPE_PHONE_VERIFICATION

            }
            SIGNATURE -> {
                TYPE_SIGNATURE

            }
            else -> {
                TYPE_EDT
            }
        }

    }

    override fun getItemCount(): Int {
        return collection.size

    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    fun itemLocationChanged(location: Location, pos: Int) {
        this.userLocation = location
        notifyItemChanged(pos)

    }

    fun itemSelectedLocationChange(selectedLatLong: LatLng, pos: Int) {
        Timber.e("itemSelectedLocationChange $selectedLatLong")
        this.selectedLatLong = selectedLatLong
        locationChanged = true
        notifyItemChanged(pos)
    }

}


