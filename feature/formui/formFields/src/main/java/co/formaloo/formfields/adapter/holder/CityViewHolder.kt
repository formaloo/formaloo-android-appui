package co.formaloo.formfields.adapter.holder

import android.view.View
import co.formaloo.formfields.databinding.LayoutUiCityItemSecondBinding
import co.formaloo.formfields.ui.fields.fieldCity


class CityViewHolder(itemView: View) : BaseVH(itemView) {

    val binding = LayoutUiCityItemSecondBinding.bind(itemView)


    override fun initView() {

        binding.droplay.addView(
            fieldCity(
                itemView.context,
                field,
                form,
                viewmodel,
                fromEdit ?: false,
                listener,
                position_,
                fieldRenderedData
            )
        )

        viewmodel.initFieldSlug(field.slug ?: "")
        viewmodel.getCityFieldChoices()


        errStatus.observe(lifeCycleOwner){
//            fieldBackgroundUI(binding.barlay,it)
            if (it == true){
//                displayErrLay(binding.errLay, binding.errLay.error_txv)

            }
        }

    }


}
