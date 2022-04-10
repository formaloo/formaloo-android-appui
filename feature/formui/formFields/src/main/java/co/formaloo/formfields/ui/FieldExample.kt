package co.formaloo.formfields.ui

import android.content.Context
import android.view.View
import co.formaloo.model.form.Fields
import co.formaloo.model.form.Form
import co.formaloo.formCommon.vm.UIViewModel
import co.formaloo.formCommon.listener.ViewsListener

fun fieldExp(
    context: Context,
    field: Fields,
    form: Form,
    answer: String,
    viewmodel: UIViewModel,
    fromEdit: Boolean,
    listener: ViewsListener,
    position_: Int
): View {
    return fieldContainer(context, field, form).apply {
        val fieldErr = createFieldErr(context,field,viewmodel)

        val fieldContainerBorder = fieldContainerBorder(context, field, form,viewmodel)
        fieldContainerBorder.apply {


        }

        addView(fieldContainerBorder)

        addView(fieldErr)

    }

}


