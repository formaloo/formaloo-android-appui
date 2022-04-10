package co.formaloo.formfields.adapter.holder

import android.location.Location
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import co.formaloo.common.extension.invisible
import co.formaloo.common.extension.visible
import co.formaloo.formCommon.listener.ViewsListener
import co.formaloo.formCommon.vm.UIViewModel
import co.formaloo.model.form.Fields
import co.formaloo.model.form.Form
import co.formaloo.model.submit.RenderedData
import co.formaloo.formfields.*
import com.google.android.gms.maps.model.LatLng
import org.koin.core.component.KoinComponent

open class BaseVH(itemView: View) : RecyclerView.ViewHolder(itemView), KoinComponent {

    private val _errStatus = MutableLiveData<Boolean>().apply { value = null }
    val errStatus: LiveData<Boolean> = _errStatus

    internal var lifeCycleOwner: LifecycleOwner
    internal var fromEdit: Boolean = false
    internal var locationChanged: Boolean = false
    internal var position_: Int = -1
    internal lateinit var field: Fields
    internal lateinit var listener: ViewsListener
    internal lateinit var form: Form
    internal lateinit var viewmodel: UIViewModel
    internal var fieldRenderedData: RenderedData? = null
    internal var rowRenderedData: Map<String, RenderedData>? = null
    internal var errField: Fields? = null
    var selectedLatLong: LatLng? = null
    var userLocation: Location?=null

    init {
        setIsRecyclable(false)
        itemView.setOnTouchListener { view, motionEvent -> // Disallow the touch request for parent scroll on touch of child view
            view.parent.requestDisallowInterceptTouchEvent(false)
            true
        }
        lifeCycleOwner = itemView.context as LifecycleOwner


    }

    internal fun hideErr(errLay: View, viewmodel: UIViewModel) {
        errLay.invisible()
        viewmodel.setErrorToField(Fields(), "")

    }

    fun bindItems(
        field: Fields,
        position_: Int,
        listener: ViewsListener,
        form: Form,
        viewmodel: UIViewModel,
        rowRenderedData: Map<String, RenderedData>,
        fromEdit: Boolean,
        locationChanged: Boolean,
        selectedLatLong: LatLng?,
        userLocation: Location?
    ) {
        this.fromEdit = fromEdit
        this.rowRenderedData = rowRenderedData
        this.viewmodel = viewmodel
        this.form = form
        this.listener = listener
        this.position_ = position_
        this.userLocation = userLocation
        this.field = field
        this.locationChanged = locationChanged
        this.selectedLatLong = selectedLatLong

        rowRenderedData.forEach {
            if (it.key.equals(field.slug)) {
                fieldRenderedData = it.value
            }

        }

        viewmodel.errorField.observe(lifeCycleOwner) {
            it?.let { errField ->
                this.errField = errField
                val errFieldSlug = errField.slug

                _errStatus.value =
                    errFieldSlug == field.slug || errFieldSlug == itemView.context.getString(R.string.phone_)


            }

        }

        if (field.required == true) {
            viewmodel.reuiredField(field)

        } else {

        }

        setIsRecyclable(false)

        initView()
    }

    open fun initView() {

    }

    fun fillHeaderData(titleTxv: TextView, descTxv: TextView) {
        val title = field.title
        titleTxv.text = if (field.required == true) {
            "$title *"
        } else {
            title
        }

        setViewTxtColorFormTextColor(titleTxv)
        setViewTxtColorFormTextColor(descTxv)

        if (field.description?.isNotEmpty() == true) {
            fieldDesc(descTxv, field)

        } else {
            descTxv.invisible()
        }

    }

    fun fieldBackgroundUI(view: View, hasErr: Boolean?) {
        fieldBackground(view, form, hasErr)
    }

    fun setViewTxtColorFormTextColor(view: TextView) {
        setTextColor(view, form.text_color)
    }

    fun setViewTxtColorFormField(view: TextView) {
        setTextColor(view, form.field_color)
    }

    fun setReverseFieldBackground(view: View) {
        reverseFieldBackground(view, form)
    }

    fun displayErrLay(errLay: RelativeLayout, errorTxv: TextView) {
        errLay.visible()
        errorTxv.text = errField?.title ?: ""

    }
}
