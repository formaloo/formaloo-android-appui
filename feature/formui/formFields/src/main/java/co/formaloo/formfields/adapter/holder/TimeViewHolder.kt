package co.formaloo.formfields.adapter.holder

import android.view.View
import androidx.lifecycle.LifecycleOwner
import co.formaloo.common.*
import co.formaloo.formfields.databinding.LayoutUiTimeItemBinding
import kotlinx.android.synthetic.main.layout_ui_field_footer.view.*
import kotlinx.android.synthetic.main.layout_ui_field_header.view.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TimeViewHolder(itemView: View) : BaseVH(itemView), KoinComponent {

    val binding = LayoutUiTimeItemBinding.bind(itemView)
    val baseMethod: BaseMethod by inject()

    override fun initView() {
        val context = itemView.context

        val fieldUiHeader = binding.timeLay
        fillHeaderData(fieldUiHeader.key_txv,fieldUiHeader.desc_txv)

        setViewTxtColorFormTextColor(binding.keyTxv)

        viewmodel.selectedTime.observe(lifeCycleOwner) {
            it?.let {
                binding.keyTxv.text = it
            }
        }
        errStatus.observe(lifeCycleOwner) {
            fieldBackgroundUI(binding.timeBtn, it)
            if (it == true) {

                displayErrLay(binding.errLay, binding.errLay.error_txv)

            }
        }

        if (fromEdit) {

        } else {
            binding.timeBtn.setOnClickListener {
                listener.openTimePicker(field)
            }
        }

        viewmodel.resetData.observe(context as LifecycleOwner) {
            it?.let {
                if (it) {
                    binding.keyTxv.text = ""
                } else {

                }
            }
        }

        fieldRenderedData?.value?.let {
            viewmodel.initSelectedTime(it.toString())

        }

    }


}
