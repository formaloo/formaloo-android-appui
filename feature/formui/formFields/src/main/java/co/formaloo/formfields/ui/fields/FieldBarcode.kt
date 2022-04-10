package co.formaloo.formfields.ui.fields

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import co.formaloo.common.extension.invisible
import co.formaloo.common.extension.visible
import co.formaloo.common.scannerHeight
import co.formaloo.formfields.R
import co.formaloo.formCommon.vm.UIViewModel
import co.formaloo.formCommon.listener.ViewsListener
import co.formaloo.model.form.Fields
import co.formaloo.model.form.Form
import co.formaloo.model.submit.RenderedData
import co.formaloo.formfields.ui.*


private val _cameraStart = MutableLiveData<Boolean>().apply { value = null }
val cameraStart: LiveData<Boolean> = _cameraStart

private val _scannerValue = MutableLiveData<String>().apply { value = null }
val scannerValue: LiveData<String> = _scannerValue

fun fieldBarcode(
    context: Context,
    field: Fields,
    form: Form,
    viewmodel: UIViewModel,
    fromEdit: Boolean,
    listener: ViewsListener,
    position_: Int,
    renderedData: RenderedData?
): View {
    return fieldContainer(context, field, form).apply {
        val fieldErr = createFieldErr(context, field, viewmodel)

        val fieldContainerBorder = fieldContainerBorder(context, field, form, viewmodel)
        fieldContainerBorder.apply {

            val scannerValueTxv = fieldTextView(context, form).apply {
                invisible()
            }
            val previewView = createFieldPreviewView(context).apply {
                invisible()
            }
            val scanBtn = createFieldButtonWithReverseBack(context, form).apply {
                text = context.getString(R.string.scan)
                setOnClickListener {
                    listener.startCamera(field, _cameraStart, previewView, _scannerValue)
                }
                invisible()
            }
            val openScannerBtn = createFieldButtonWithReverseBack(context, form).apply {
                text = context.getString(R.string.open_scanner)
                setOnClickListener {
                    _cameraStart.value = true
                }
            }

            cameraStart.observe(context as LifecycleOwner) {
                it?.let {
                    if (it) {
                        openScannerBtn.invisible()
                        scannerValueTxv.invisible()
                        scanBtn.visible()
                        previewView.visible()
                    } else {
                        openScannerBtn.visible()
                        scannerValueTxv.visible()
                        scanBtn.invisible()
                        previewView.invisible()
                    }
                }

            }

            scannerValue.observe(context as LifecycleOwner) {
                it?.let {
                    scannerValueTxv.text = it
                }
            }
            if (renderedData?.raw_value != null) {
                renderedData.raw_value?.let { raw_value ->
                    scannerValueTxv.text = raw_value.toString()
                    scannerValueTxv.visible()
                    openScannerBtn.visible()
                    scanBtn.invisible()
                    previewView.invisible()
                }
            } else {
                listener.setUpCameraX(
                    field,
                    _cameraStart,
                    previewView,
                    _scannerValue
                )
            }

            addView(scannerValueTxv)
            addView(previewView)
            addView(scanBtn)
            addView(openScannerBtn)

        }

        addView(fieldContainerBorder)

        addView(fieldErr)

    }

}

fun setUIData(
    fieldRenderedData: RenderedData,
    listener: ViewsListener,
    _cameraStart: LiveData<Boolean>
) {


}

fun createFieldPreviewView(context: Context): PreviewView {
    return PreviewView(context).apply {
        val lp = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            scannerHeight(context)
        )

        layoutParams = lp

        invisible()
    }
}


