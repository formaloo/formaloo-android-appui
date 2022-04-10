package co.formaloo.formfields.ui

import android.content.Context
import androidx.appcompat.widget.AppCompatButton
import co.formaloo.formfields.reverseFieldBackground
import co.formaloo.model.form.Form


fun createFieldButtonWithReverseBack(context: Context, form:Form): AppCompatButton {
    return createFieldButton(context, form).apply {
        reverseFieldBackground(this,form)
    }
}
