package co.formaloo.formfields.ui

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import co.formaloo.model.form.Form
import co.formaloo.formfields.*

fun createRadioBtn(
    context: Context,
    initValue: Boolean,
    fromEdit: Boolean,
    form: Form,
    ): RadioButton {
    return RadioButton(context).apply {

        setBackgroundResource(R.color.white)

        isChecked = initValue


        val lp = RadioGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        lp.bottomMargin = 24


        layoutParams = lp
        setPadding(24, 24, 24, 24)

        fieldBackground(this, form)
        setTxtClr(this, form.text_color)

        setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            context.resources.getDimension(R.dimen.font_xlarge)
        )

        id = View.generateViewId()


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
                    setTxtClr(this, form.text_color)

                }
            }
        }


    }
}
