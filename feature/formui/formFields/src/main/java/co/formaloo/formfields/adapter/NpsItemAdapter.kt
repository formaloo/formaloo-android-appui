package co.formaloo.formfields.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import co.formaloo.common.extension.invisible
import co.formaloo.formCommon.vm.UIViewModel
import co.formaloo.model.form.Fields
import co.formaloo.model.form.Form
import co.formaloo.model.submit.RenderedData
import co.formaloo.formfields.R
import co.formaloo.formfields.databinding.LayoutUiNpsBtnItemBinding
import co.formaloo.formfields.nps_color
import co.formaloo.formfields.setTextColor
import timber.log.Timber


class NpsItemAdapter(
    private val fields: Fields,
    private val form: Form,
    private val viewmodel: UIViewModel,
    private val errLay: RelativeLayout,
    private val fieldRenderedData: RenderedData?,
    private val fromEdit: Boolean
) : RecyclerView.Adapter<NpsItemAdapter.NPSItemViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NPSItemViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_ui_nps_btn_item, parent, false);
        return NPSItemViewHolder(itemView)


    }

    override fun onBindViewHolder(holder: NPSItemViewHolder, position_: Int) {
        holder.bindItems(fields, position_, form, viewmodel, errLay, fieldRenderedData, fromEdit)

        holder.setIsRecyclable(false)
    }


    override fun getItemCount(): Int {
        return 11
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    class NPSItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var lastSelectedItem: AppCompatButton? = null
        val binding = LayoutUiNpsBtnItemBinding.bind(itemView)

        fun bindItems(
            field: Fields,
            position_: Int,
            form: Form,
            viewmodel: UIViewModel,
            errLay: RelativeLayout,
            fieldRenderedData: RenderedData?,
            fromEdit: Boolean

        ) {
            val lifecycleOwner = itemView.context as LifecycleOwner

            viewmodel.npsPos.observe(lifecycleOwner) {
                if (it != null) {
                    nps_color(binding.npsBtn, form, it == adapterPosition)

                } else {
                    nps_color(binding.npsBtn, form, false)

                }
            }
            setTextColor(binding.npsBtn, form.submit_text_color)

            binding.npsBtn.text = "$adapterPosition"

            if (fromEdit) {


            } else {
                binding.npsBtn.setOnClickListener {
                    viewmodel.npsBtnClicked(field, adapterPosition)
                }
                Timber.e("fieldRenderedData ${fieldRenderedData?.value}")
                fieldRenderedData?.value?.let {
                    if (it is Double && it.toInt() == absoluteAdapterPosition) {
                        itemView.performClick()


                    }
                }

                if (field.required == true) {
                    viewmodel.reuiredField(field)

                } else {

                }
            }
        }

        private fun hideErr(errLay: RelativeLayout) {
            errLay.invisible()
        }


    }
}


