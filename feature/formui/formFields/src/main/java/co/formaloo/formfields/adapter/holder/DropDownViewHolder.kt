package co.formaloo.formfields.adapter.holder

import android.os.Build
import android.view.View
import android.widget.AdapterView
import co.formaloo.formfields.adapter.DropDownItemsAdapter
import co.formaloo.formfields.databinding.LayoutUiDropdownItemBinding
import kotlinx.android.synthetic.main.layout_ui_field_footer.view.*
import kotlinx.android.synthetic.main.layout_ui_field_header.view.*


class DropDownViewHolder(itemView: View) : BaseVH(itemView) {

    val binding = LayoutUiDropdownItemBinding.bind(itemView)

    override fun initView() {
        val fieldUiHeader = binding.droplay
        fillHeaderData(fieldUiHeader.key_txv,fieldUiHeader.desc_txv)

        errStatus.observe(lifeCycleOwner){
            fieldBackgroundUI(binding.spinnerValueLay,it)
            if (it == true){
                displayErrLay(binding.errLay, binding.errLay.error_txv)

            }
        }

        val dropAdapter = DropDownItemsAdapter(form)

        binding.valueSpinner.apply {
            adapter = dropAdapter
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    dropAdapter.getItem(position)?.slug?.let { slug ->
                        viewmodel.addKeyValueToReq(field.slug!!, slug)
                    }
                }

            }

        }

        dropAdapter.listItemsTxt=field.choice_items?.toList()?: listOf()

        if (fromEdit) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                binding.valueSpinner.suppressLayout(true)
            }
        } else {

        }

    }


}
