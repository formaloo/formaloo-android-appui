package co.formaloo.formfields.adapter.holder

import android.R
import android.content.res.ColorStateList
import android.view.View
import co.formaloo.common.*
import co.formaloo.formfields.databinding.LayoutUiStarItemBinding
import co.formaloo.formfields.getHexColor
import kotlinx.android.synthetic.main.layout_ui_field_footer.view.*
import kotlinx.android.synthetic.main.layout_ui_field_header.view.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class StarViewHolder(itemView: View) : BaseVH(itemView), KoinComponent {

    val binding = LayoutUiStarItemBinding.bind(itemView)
    val baseMethod: BaseMethod by inject()

    override fun initView() {
        val fieldUiHeader = binding.starLay
        fillHeaderData(fieldUiHeader.key_txv,fieldUiHeader.desc_txv)

        errStatus.observe(lifeCycleOwner) {
            fieldBackgroundUI(binding.starLay, it)
            if (it == true) {

                displayErrLay(binding.errLay, binding.errLay.error_txv)

            }
        }
        if (fromEdit) {
            binding.starRating.isEnabled = false
        } else {

        }
        getHexColor(form.text_color)?.let {
            val colorStateList = ColorStateList(
                arrayOf(
                    intArrayOf(-R.attr.state_enabled),
                    intArrayOf(R.attr.state_enabled)
                ), intArrayOf(
                    baseMethod.parseColor(it) //disabled
                    , baseMethod.parseColor(it) //enabled
                )
            )
            binding.starRating.progressTintList = colorStateList
        }

        binding.starRating.setOnRatingBarChangeListener { ratingBar, fl, b ->
            viewmodel.addKeyValueToReq(field.slug!!, fl)
            hideErr(binding.errLay, viewmodel)

        }

        fieldRenderedData?.value?.let {
            if (it is Double) {
                binding.starRating.rating = it.toFloat()

            }

        }

    }


}
