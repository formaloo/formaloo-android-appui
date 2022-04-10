package co.formaloo.formfields.adapter.holder

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.FOCUS_BLOCK_DESCENDANTS
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.CheckBox
import android.widget.LinearLayout
import co.formaloo.common.extension.invisible
import co.formaloo.formCommon.listener.ViewsListener
import co.formaloo.formCommon.vm.UIViewModel
import co.formaloo.model.form.ChoiceItem
import co.formaloo.model.form.Fields
import co.formaloo.model.form.Form
import co.formaloo.model.submit.RenderedData
import co.formaloo.formfields.*
import co.formaloo.formfields.databinding.LayoutUiMultiItemBinding
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.android.synthetic.main.layout_ui_field_footer.view.*
import kotlinx.android.synthetic.main.layout_ui_field_header.view.*
import org.apache.commons.lang3.StringUtils

class MultiViewHolder(itemView: View) : BaseVH(itemView) {
    private var valuesList = arrayListOf<String>()
    val binding = LayoutUiMultiItemBinding.bind(itemView)

    override fun initView(){
        val fieldUiHeader = binding.multilay
        fillHeaderData(fieldUiHeader.key_txv,fieldUiHeader.desc_txv)

        errStatus.observe(lifeCycleOwner){
            if (it == true){
                displayErrLay(binding.errLay,binding. errLay.error_txv)

            }
        }
        fieldRenderedData?.value?.let {
            if (it is ArrayList<*>) {
                valuesList = it as ArrayList<String>
            } else {
            }

        }


        if (fromEdit) {
            binding.multilay.descendantFocusability = FOCUS_BLOCK_DESCENDANTS

        } else {

        }
        field.choice_items?.let { choiceList ->
            for (choice in choiceList) {
                createNewChoice(
                    choice,
                    field,
                    binding.choicesListLay,
                    listener,
                    form,
                    viewmodel,
                    fieldRenderedData, fromEdit
                )

            }
        }
    }

    private fun createNewChoice(
        choice: ChoiceItem,
        field: Fields,
        choicesListLay: LinearLayout,
        listener: ViewsListener,
        form: Form,
        viewmodel: UIViewModel,
        fieldRenderedData: RenderedData?,
        fromEdit: Boolean
    ) {
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.bottomMargin = 24

        val ll = LinearLayout(choicesListLay.context)
        ll.layoutParams = layoutParams
        ll.orientation = LinearLayout.HORIZONTAL
        val boxLay =
            createCheckBox(
                choice,
                field,
                choicesListLay,
                listener,
                valuesList,
                form,
                viewmodel,
                fieldRenderedData, fromEdit
            )
        (ll as ViewGroup).addView(boxLay)

        (choicesListLay as ViewGroup).addView(ll)

    }

    private fun createCheckBox(
        choice: ChoiceItem,
        field: Fields,
        ll: LinearLayout,
        listener: ViewsListener,
        valuesList: ArrayList<String>,
        form: Form,
        viewmodel: UIViewModel,
        fieldRenderedData: RenderedData?,
        fromEdit: Boolean
    ): View {

        val lp = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        lp.bottomMargin = 20

        val checkBox = CheckBox(ll.context)

        fieldRenderedData?.raw_value?.let { rawValue ->
            if (rawValue is ArrayList<*>) {
                val arrayList = rawValue as ArrayList<String>
                arrayList.forEach {
                    if (it.equals(choice.slug)) {
                        checkBox.isChecked = true
                    }
                }

            }
        }

        checkBox.apply {
            layoutParams = lp
            setPadding(24, 24, 24, 24)
            minLines = 2

            choice.title?.let {
                text = it
            }
            choice.image?.let {
                setImage(it, this)
            }
            isChecked = valuesList.contains(choice.slug)
            val context = context
            setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                context.resources.getDimension(R.dimen.font_xlarge)
            )

            fieldBackground(this, form)
            setTextColor(this, form.text_color)

            if (fromEdit) {
                isFocusable = false
                isClickable = false
            } else {
                setOnCheckedChangeListener { compoundButton, b ->
                    hideErr(binding.errLay, viewmodel)

                    if (b) {
                        selectedFieldBackground(this, form)
                        setSelectedTextColor(this, form)

                    } else {
                        fieldBackground(this, form)
                        setTextColor(this, form.text_color)

                    }
                    choice.slug?.let { choiceSlug ->
                        if (b) {
                            valuesList.add(choiceSlug)

                        } else {
                            if (valuesList.contains(choiceSlug)) {
                                valuesList.remove(choiceSlug)

                            } else {

                            }
                        }

                        viewmodel.addKeyValueToReq(field.slug!!, StringUtils.join(valuesList, ","))

                    }

                }
            }
        }

        return checkBox
    }

    private fun hideErr(binding: LayoutUiMultiItemBinding, viewmodel: UIViewModel) {
        binding.errLay.invisible()
        viewmodel.setErrorToField(Fields(), "")

    }

    private fun setImage(it: String, box: CheckBox) {
        Picasso.get().load(it).into(object : Target {
            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                bitmap?.let {
                    try {

                    val padding =
                        box.resources.getDimensionPixelSize(R.dimen.padding_2xsmall)
                    val height = box.resources.getDimensionPixelSize(R.dimen.btn_h)

                    val drawable = BitmapDrawable(
                        box.resources,
                        Bitmap.createScaledBitmap(bitmap, 150, height, false)
                    )


                            box.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)


                    box.compoundDrawablePadding = padding

                    }catch (e:Exception){

                    }
                }
            }

            override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {
                Log.e("TAG", "onBitmapFailed: $e")
            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
            }

        })

    }

}
