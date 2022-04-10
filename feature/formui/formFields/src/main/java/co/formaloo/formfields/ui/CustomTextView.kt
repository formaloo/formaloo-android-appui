package co.formaloo.formfields.ui

import android.content.Context
import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity.CENTER_VERTICAL
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.view.ContextThemeWrapper
import co.formaloo.common.parseColor
import co.formaloo.model.form.Form
import co.formaloo.formfields.R
import co.formaloo.formfields.getHexColor


fun fieldTextView(context: Context, form: Form): TextView {
    return TextView(ContextThemeWrapper(context,R.style.Regular), null, 0).apply {

        val lp = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams = lp

        setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            context.resources.getDimension(R.dimen.font_large)
        )

        setLineSpacing(0f, 1.33f)

        textAlignment= View.TEXT_ALIGNMENT_GRAVITY

        gravity=CENTER_VERTICAL

        form.text_color?.let {
            getHexColor(form.text_color)?.let {
                setTextColor(parseColor(it))

            }

        }
        form.background_color?.let {
            getHexColor(form.background_color)?.let {
//                setBackgroundColor(darkenColor(parseColor(it)))

            }

        }

        setTypeface(typeface, Typeface.NORMAL)



    }

}

