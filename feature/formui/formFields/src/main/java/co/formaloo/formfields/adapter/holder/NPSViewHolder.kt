package co.formaloo.formfields.adapter.holder

import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.formaloo.formfields.adapter.NpsItemAdapter
import co.formaloo.formfields.databinding.LayoutUiNpsItemBinding
import kotlinx.android.synthetic.main.layout_ui_field_footer.view.*
import kotlinx.android.synthetic.main.layout_ui_field_header.view.*

class NPSViewHolder(itemView: View) : BaseVH(itemView) {

    val binding = LayoutUiNpsItemBinding.bind(itemView)

    override fun initView(){
        val fieldUiHeader = binding.npsLay
        fillHeaderData(fieldUiHeader.key_txv,fieldUiHeader.desc_txv)

        errStatus.observe(lifeCycleOwner){
            fieldBackgroundUI(binding.npsRec,it)
            if (it == true){

                displayErrLay(binding.errLay, binding.errLay.error_txv)

            }
        }

        binding.npsRec.apply {
            adapter = NpsItemAdapter(field, form, viewmodel, binding.errLay, fieldRenderedData,fromEdit)
            layoutManager = GridLayoutManager(context, 11)
            addItemDecoration(DividerItemDecoration(context, RecyclerView.HORIZONTAL))
        }


        if (fromEdit){
            binding.npsRec.suppressLayout(true)
        }else{

        }
    }

}
