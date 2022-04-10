package co.formaloo.formfields.adapter.holder

import android.view.View
import androidx.lifecycle.LifecycleOwner
import co.formaloo.common.*
import co.formaloo.formfields.databinding.LayoutUiDateItemBinding
import kotlinx.android.synthetic.main.layout_ui_field_footer.view.*
import kotlinx.android.synthetic.main.layout_ui_field_header.view.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DateViewHolder(itemView: View) : BaseVH(itemView), KoinComponent {

    val binding = LayoutUiDateItemBinding.bind(itemView)
    val baseMethod: BaseMethod by inject()

    override fun initView(){

        val context = binding.keyTxv.context
        val fieldUiHeader = binding.dateLay
        fillHeaderData(fieldUiHeader.key_txv,fieldUiHeader.desc_txv)

        errStatus.observe(lifeCycleOwner){
            fieldBackgroundUI(binding.timeBtn,it)
            if (it == true){
                displayErrLay(binding.errLay, binding.errLay.error_txv)

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

        viewmodel.selectedDate.observe(lifeCycleOwner){
            it?.let {
                binding.keyTxv.text=it

            }
        }
        if (fromEdit) {

        } else {
            binding.timeBtn.setOnClickListener {
                listener.openDatePicker(field)
            }
        }

        setViewTxtColorFormTextColor( binding.keyTxv)

        fieldRenderedData?.value?.let {
            viewmodel.initSelectedDate(it.toString())
        }

    }


}
