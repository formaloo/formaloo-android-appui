package co.formaloo.formfields.ui

import android.content.Context
import android.graphics.Typeface
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.AppCompatButton
import co.formaloo.common.extension.invisible
import co.formaloo.common.parseColor
import co.formaloo.formfields.R
import co.formaloo.formfields.darkenColor
import co.formaloo.formfields.getHexColor
import co.formaloo.model.form.Form


fun createFieldButton(context: Context, form:Form): AppCompatButton {
    return AppCompatButton(ContextThemeWrapper(context,R.style.Regular), null, 0).apply {
        val lp = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        layoutParams = lp

        setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            context.resources.getDimension(R.dimen.font_large)
        )

        form.text_color?.let {
            getHexColor(form.text_color)?.let {
                setTextColor(parseColor(it))

            }

        }

        form.background_color?.let {
            getHexColor(form.background_color)?.let {
                setBackgroundColor(darkenColor(parseColor(it)))

            }

        }

        invisible()


        setTypeface(typeface, Typeface.NORMAL)


    }
}
