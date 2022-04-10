package co.formaloo.formfields.adapter.holder

import android.view.View
import co.formaloo.common.*
import co.formaloo.formfields.databinding.LayoutUiMatrixItemSecondBinding
import co.formaloo.formfields.ui.fields.fieldMatrix
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class MatrixViewHolder(itemView: View) : BaseVH(itemView),KoinComponent {

    val binding = LayoutUiMatrixItemSecondBinding.bind(itemView)
    val checkedAnswer = HashMap<String, String>()
    val baseMethod: BaseMethod by inject()

    override fun initView(){

        errStatus.observe(lifeCycleOwner){
//            fieldBackgroundUI(binding.barlay,it)
            if (it == true){

//                displayErrLay(binding.errLay, binding.errLay.error_txv)

            }
        }
        val context = itemView.context

        binding.matrixuilay.addView(
            fieldMatrix(
                context,
                field,
                form,
                viewmodel,
                fromEdit ?: false,
                listener,
                position_,
                fieldRenderedData
            )
        )
    }



}
