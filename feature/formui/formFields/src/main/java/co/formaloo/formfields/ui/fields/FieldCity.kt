package co.formaloo.formfields.ui.fields

import android.content.Context
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import co.formaloo.common.smallPadding
import co.formaloo.common.xsPadding
import co.formaloo.model.form.Fields
import co.formaloo.model.form.Form
import co.formaloo.model.submit.RenderedData
import co.formaloo.formCommon.vm.UIViewModel
import co.formaloo.formCommon.listener.ViewsListener
import co.formaloo.formfields.ui.createFieldErr
import co.formaloo.formfields.ui.fieldContainer
import co.formaloo.formfields.ui.fieldContainerBorder
import co.formaloo.formfields.ui.fieldTextView

private val _t = MutableLiveData<Boolean>().apply { value = null }
val t: LiveData<Boolean> = _t



fun fieldCity(
    context: Context,
    field: Fields,
    form: Form,
    viewmodel: UIViewModel,
    fromEdit: Boolean,
    listener: ViewsListener,
    position_: Int,
    renderedData: RenderedData?
): View {

    return fieldContainer(context, field, form).apply {
        val fieldErr = createFieldErr(context, field, viewmodel)
        val fieldContainerBorder = fieldContainerBorder(context, field, form, viewmodel)
        fieldContainerBorder.apply {
            addView(fieldTextView(context, form).apply {
              setPadding(
                    xsPadding(context),
                    smallPadding(context),
                    xsPadding(context),
                    smallPadding(context)
                )

                setOnClickListener {
                    listener.openCityListDialog(viewmodel,field)
                }
                text = renderedData?.value?.toString()?:""

                viewmodel.selectedCity.observe(context as LifecycleOwner) {
                    it?.let {
                        text = it.title ?: ""
                        listener.closeCityDialog()
                    }
                }
            })

        }

        addView(fieldContainerBorder)

        addView(fieldErr)

    }

}



