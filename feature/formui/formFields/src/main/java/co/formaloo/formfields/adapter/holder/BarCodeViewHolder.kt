package co.formaloo.formfields.adapter.holder

import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import co.formaloo.common.*
import co.formaloo.formfields.databinding.LayoutUiBarcodeItemBinding
import kotlinx.android.synthetic.main.layout_ui_field_footer.view.*
import kotlinx.android.synthetic.main.layout_ui_field_header.view.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import co.formaloo.common.extension.invisible
import co.formaloo.common.extension.visible
class BarCodeViewHolder(itemView: View) : BaseVH(itemView), KoinComponent {

    val binding = LayoutUiBarcodeItemBinding.bind(itemView)
    val baseMethod: BaseMethod by inject()

    private val _cameraStart = MutableLiveData<Boolean>().apply { value = null }
    val cameraStart: LiveData<Boolean> = _cameraStart

    private val _scannerValue = MutableLiveData<String>().apply { value = null }
    val scannerValue: LiveData<String> = _scannerValue


    override fun initView() {
        val context = itemView.context
        val fieldUiHeader = binding.barcodeLay
        fillHeaderData(fieldUiHeader.key_txv,fieldUiHeader.desc_txv)


        if (fromEdit) {
        } else {
        }

        val barlay = binding.barlay

        errStatus.observe(lifeCycleOwner) {
            fieldBackgroundUI(barlay, it)
            if (it == true) {
                displayErrLay(binding.errLay, binding.errLay.error_txv)

            }
        }

        setViewTxtColorFormTextColor(binding.scannerValue)

        setViewTxtColorFormField(binding.scanbtn)
        setReverseFieldBackground(binding.scanbtn)

        setViewTxtColorFormField(binding.openScannerBtn)
        setReverseFieldBackground(binding.openScannerBtn)

        cameraStart.observe(context as LifecycleOwner) {
            it?.let {
                if (it) {
                    binding.openScannerBtn.invisible()
                    binding.scannerValue.invisible()
                    binding.scanbtn.visible()
                    binding.previewView.visible()
                } else {
                    binding.openScannerBtn.visible()
                    binding.scannerValue.visible()
                    binding.scanbtn.invisible()
                    binding.previewView.invisible()
                }
            }

        }

        scannerValue.observe(context as LifecycleOwner) {
            it?.let {
                binding.scannerValue.text = it
            }
        }

        val rawValue = fieldRenderedData?.raw_value
        if (rawValue != null) {
            rawValue?.let { raw_value ->
                binding.scannerValue.text = raw_value.toString()
                binding.scannerValue.visible()
                binding.openScannerBtn.visible()
                binding.scanbtn.invisible()
                binding.previewView.invisible()
            }
        } else {

        }
        binding.openScannerBtn.setOnClickListener {
            listener.setUpCameraX(
                field,
                _cameraStart,
                binding.previewView,
                _scannerValue
            )
        }


    }


}
