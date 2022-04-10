package co.formaloo.formfields.ui

import android.content.Context
import android.graphics.Typeface
import android.widget.TextView
import co.formaloo.common.extension.invisible
import co.formaloo.model.form.Fields
import co.formaloo.model.form.Form
import co.formaloo.formfields.fieldDesc


fun fieldTitle(context: Context, form: Form, field: Fields): TextView {
    return fieldTextView(context, form).apply {
        setTypeface(typeface, Typeface.BOLD)
        text = field.title
    }

}

fun createFieldDesc(context: Context, form: Form, field: Fields): TextView {
    return TextView(context).apply {
        if (field.description?.isNotEmpty() == true) {
            fieldDesc(this, field)
        } else {
            invisible()
        }
    }
}
