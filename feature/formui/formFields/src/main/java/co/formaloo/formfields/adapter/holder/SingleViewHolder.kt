package co.formaloo.formfields.adapter.holder

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.FOCUS_BLOCK_DESCENDANTS
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.lifecycle.LifecycleOwner
import co.formaloo.common.Constants
import co.formaloo.formCommon.listener.ViewsListener
import co.formaloo.formCommon.vm.UIViewModel
import co.formaloo.model.form.ChoiceItem
import co.formaloo.model.form.Fields
import co.formaloo.model.form.Form
import co.formaloo.model.submit.RenderedData
import co.formaloo.formfields.*
import co.formaloo.formfields.databinding.LayoutUiSignleItemBinding
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.android.synthetic.main.layout_ui_field_footer.view.*
import kotlinx.android.synthetic.main.layout_ui_field_header.view.*
import timber.log.Timber


class SingleViewHolder(itemView: View) : BaseVH(itemView) {

    val binding = LayoutUiSignleItemBinding.bind(itemView)

    override fun initView() {
        val fieldUiHeader = binding.singleLay
        fillHeaderData(fieldUiHeader.key_txv,fieldUiHeader.desc_txv)

        errStatus.observe(lifeCycleOwner) {
//            fieldBackgroundUI(binding.barlay,it)
            if (it == true) {

                displayErrLay(binding.errLay, binding.errLay.error_txv)

            }
        }
        val context = binding.valueRg.context
        binding.lifecycleOwner = context as LifecycleOwner


        val type = field.type
        if (type != null && type.equals(Constants.YESNO)) {
            field.choice_items = arrayListOf(
                ChoiceItem(
                    context.resources.getString(R.string.yes),
                    null,
                    "yes"
                ), ChoiceItem(
                    context.resources.getString(
                        R.string.no
                    ), null, "no"
                )
            )
        }

        if (fromEdit) {
            binding.valueRg.descendantFocusability = FOCUS_BLOCK_DESCENDANTS

        } else {

        }

        field.choice_items?.let {
            addRadioButtons(
                field,
                binding.valueRg,
                ArrayList(it),
                listener,
                form,
                viewmodel,
                fieldRenderedData, fromEdit
            )

        }
    }

    fun addRadioButtons(
        field: Fields,
        value_rg: RadioGroup,
        items: ArrayList<ChoiceItem>,
        listener: ViewsListener,
        form: Form,
        viewmodel: UIViewModel,
        fieldRenderedData: RenderedData?,
        fromEdit: Boolean
    ) {
        val context = value_rg.context
        val type = field.type
        value_rg.orientation = if (type != null && type.equals(Constants.YESNO)) {
            LinearLayout.HORIZONTAL
        } else {
            LinearLayout.VERTICAL
        }
        for (i in 1..items.size) {
            value_rg.addView(
                createRadioButton(
                    form,
                    viewmodel,
                    items,
                    field,
                    i,
                    context,
                    fieldRenderedData,
                    fromEdit
                )
            )
        }
    }


    private fun createRadioButton(
        form: Form,
        uiViewModel: UIViewModel,
        items: java.util.ArrayList<ChoiceItem>,
        field: Fields,
        i: Int,
        context: Context,
        fieldRenderedData: RenderedData?,
        fromEdit: Boolean
    ): RadioButton {


        val rdbtn = RadioButton(context)

        rdbtn.apply {
            val lp = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            lp.bottomMargin = 24


            layoutParams = lp
            setPadding(24, 24, 24, 24)
            minLines = 2

            fieldBackground(this, form)
            setTextColor(this, form.text_color)

            setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                context.resources.getDimension(R.dimen.font_xlarge)
            )

            id = View.generateViewId()
            text = items[i - 1].title ?: ""

            items[i - 1].image?.let {

            }
            if (fromEdit) {
                isFocusable = false
                isClickable = false
            } else {
                setOnCheckedChangeListener { compoundButton, b ->
                    if (b) {
                        selectedFieldBackground(this, form)
                        setSelectedTextColor(this, form)

                    } else {
                        fieldBackground(this, form)
                        setTextColor(this, form.text_color)

                    }
                }
                fieldRenderedData?.value?.let {
                    if (it.equals(items[i - 1].title)) {
                        items[i - 1].slug?.let { slug ->
                            uiViewModel.addKeyValueToReq(field.slug!!, slug)
                        }
                        rdbtn.isChecked = true
                        rdbtn.performClick()
                    } else {

                    }

                }

                if (field.required == true) {
                    uiViewModel.reuiredField(field)

                } else {

                }
                setOnClickListener {
                    items[i - 1].slug?.let { slug ->
                        uiViewModel.addKeyValueToReq(field.slug!!, slug)
                    }
                    hideErr(binding.errLay, uiViewModel)

                }
            }
        }

        return rdbtn
    }


    private fun setDrawable(drawable: String, btn: RadioButton) {
        Timber.e("setDrawable $drawable")
        Picasso.get().load(drawable).into(object : Target {
            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                btn.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    null,
                    BitmapDrawable(bitmap)
                )

            }

            override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {
                Log.e("TAG", "onBitmapFailed: $e")
            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
            }

        })
    }

}
