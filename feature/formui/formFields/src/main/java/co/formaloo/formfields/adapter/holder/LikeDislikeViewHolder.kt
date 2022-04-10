package co.formaloo.formfields.adapter.holder

import android.view.View
import android.widget.ImageButton
import co.formaloo.common.*
import co.formaloo.model.form.Form
import co.formaloo.formfields.R
import co.formaloo.formfields.databinding.LayoutUiLikeDislikeItemBinding
import co.formaloo.formfields.fieldBackground
import co.formaloo.formfields.getHexColor
import co.formaloo.formfields.imageTintList
import kotlinx.android.synthetic.main.layout_ui_field_footer.view.*
import kotlinx.android.synthetic.main.layout_ui_field_header.view.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LikeDislikeViewHolder(itemView: View) : BaseVH(itemView),KoinComponent {

    val binding = LayoutUiLikeDislikeItemBinding.bind(itemView)
    val baseMethod: BaseMethod by inject()

    override fun initView(){
        val fieldUiHeader = binding.likedesLay
        fillHeaderData(fieldUiHeader.key_txv,fieldUiHeader.desc_txv)

        errStatus.observe(lifeCycleOwner){
            fieldBackgroundUI(binding.ldlay,it)
            if (it == true){
                displayErrLay(binding.errLay, binding.errLay.error_txv)

            }
        }

        imageTintList(binding.likeBtn,form.text_color)
        imageTintList(binding.dislikeBtn,form.text_color)

        fieldBackground(binding.likeBtn,form)
        fieldBackground(binding.dislikeBtn,form)

        if (fromEdit){

        }else{
            binding.dislikeBtn.setOnClickListener {
                it.isSelected = true
                binding.likeBtn.isSelected = false
                viewmodel.addKeyValueToReq(field.slug?:"",-1)

            }

            binding.likeBtn.setOnClickListener {
                it.isSelected = true
                binding.dislikeBtn.isSelected = false
                viewmodel.addKeyValueToReq(field.slug?:"",1)
            }
        }


        fieldRenderedData?.raw_value?.let {
            when {
                it.equals(-1.0) -> {
                    binding.dislikeBtn.performClick()
                }
                it.equals(1.0) -> {
                    binding.likeBtn.performClick()
                }
                else -> {

                }
            }


        }

        if (field.required==true){
            viewmodel.reuiredField(field)

        }else{

        }
    }


    private fun changeBtnActivity(
        activeBtn: ImageButton,
        deActiveBtn: ImageButton,
        form: Form,
        likeBtn: Boolean
    ) {

        activeBtn.apply {
            if (likeBtn) {
                setImageResource(R.drawable.ic_like)

            } else {
                setImageResource(R.drawable.ic_dislike)

            }
            getHexColor(form.text_color)?.let {
                setColorFilter(baseMethod.parseColor(it))
            }

        }

        deActiveBtn.apply {
            if (likeBtn) {
                setImageResource(R.drawable.ic_dislike_border)

            } else {
                setImageResource(R.drawable.ic_like_border)

            }
            getHexColor(form.text_color)?.let {
                setColorFilter(baseMethod.parseColor(it))
            }
        }
    }


}
