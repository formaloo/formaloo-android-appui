package co.formaloo.formfields.ui

import android.content.Context
import androidx.appcompat.widget.AppCompatButton
import co.formaloo.common.parseColor
import co.formaloo.formfields.darkenColor
import co.formaloo.formfields.getHexColor
import co.formaloo.model.form.Form


fun createFieldButtonWithBack(context: Context, form:Form): AppCompatButton {
    return createFieldButton(context, form).apply {
        form.background_color?.let {
            getHexColor(form.background_color)?.let {
                setBackgroundColor(darkenColor(parseColor(it)))
            }

        }

    }
}
