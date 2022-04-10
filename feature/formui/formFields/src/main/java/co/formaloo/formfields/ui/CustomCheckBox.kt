package co.formaloo.formfields.ui

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatCheckBox
import co.formaloo.model.form.Form
import co.formaloo.formfields.*

fun createCheckBox(
    context: Context,
    initValue: Boolean,
    fromEdit: Boolean,
    form: Form,
    ): AppCompatCheckBox {
    return AppCompatCheckBox(context).apply {

        setBackgroundResource(R.color.white)
        isChecked = initValue


        val lp = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        lp.bottomMargin = 24


        layoutParams = lp
        setPadding(24, 24, 24, 24)
        minLines = 2

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
