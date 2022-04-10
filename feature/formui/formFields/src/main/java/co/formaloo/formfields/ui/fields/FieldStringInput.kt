package co.formaloo.formfields.ui.fields

import android.content.Context
import android.view.View
import co.formaloo.model.form.Fields
import co.formaloo.model.form.Form
import co.formaloo.formCommon.vm.UIViewModel
import co.formaloo.formCommon.listener.ViewsListener
import co.formaloo.formfields.ui.createEdtView
import co.formaloo.formfields.ui.createFieldErr
import co.formaloo.formfields.ui.fieldContainer
import co.formaloo.formfields.ui.fieldContainerBorder

fun fieldStringInput(
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
        val fieldErr = createFieldErr(context, field, viewmodel)

        val fieldContainerBorder = fieldContainerBorder(context, field, form, viewmodel)
        fieldContainerBorder.apply {
            addView(
                createEdtView(
                    context,
                    field,
                    form,
                    answer,
                    viewmodel,
                    fromEdit,
                    listener,
                    position_,
                    fieldErr,
                    null
                ).apply {

                }
            )
        }

        addView(fieldContainerBorder)

        addView(fieldErr)

    }

}


