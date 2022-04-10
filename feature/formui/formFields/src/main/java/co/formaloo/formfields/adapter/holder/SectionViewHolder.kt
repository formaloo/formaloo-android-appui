package co.formaloo.formfields.adapter.holder

import android.view.View
import co.formaloo.formfields.databinding.LayoutUiSectionItemBinding
import co.formaloo.formfields.sectionBackgroundShape
import kotlinx.android.synthetic.main.layout_ui_field_header.view.*

class SectionViewHolder(itemView: View) : BaseVH(itemView) {

    val binding = LayoutUiSectionItemBinding.bind(itemView)

    override fun initView() {
        val fieldUiHeader = binding.sectionLay
        fillHeaderData(fieldUiHeader.key_txv,fieldUiHeader.desc_txv)

        sectionBackgroundShape(binding.seclay, form)
    }


}
