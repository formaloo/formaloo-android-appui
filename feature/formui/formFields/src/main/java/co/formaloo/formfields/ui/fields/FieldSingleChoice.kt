package co.formaloo.formfields.ui.fields

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.RadioGroup
import android.widget.TableRow
import android.widget.TextView
import co.formaloo.common.extension.invisible
import co.formaloo.formCommon.vm.UIViewModel
import co.formaloo.model.form.ChoiceItem
import co.formaloo.model.form.Fields
import co.formaloo.model.form.Form
import co.formaloo.model.submit.RenderedData
import co.formaloo.formCommon.listener.ViewsListener
import co.formaloo.formfields.ui.createFieldErr
import co.formaloo.formfields.ui.createRadioBtn
import co.formaloo.formfields.ui.fieldContainer
import co.formaloo.formfields.ui.fieldRadioContainerBorder


fun fieldSingleChoice(
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
        val lp = TableRow.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )


        val fieldErr = createFieldErr(context, field, viewmodel)

        val hs = HorizontalScrollView(context).apply {
            isHorizontalScrollBarEnabled = false
        }
        val fieldTableContainer = fieldRadioContainerBorder(context, field, form, viewmodel)

        val choiceItems = field.choice_items ?: arrayListOf()

        fieldTableContainer.apply {
            singleView(
                this,
                form,
                field,
                renderedData,
                viewmodel,
                choiceItems,
                fieldErr,
                fromEdit,
                listener,
                position_
            )
        }

        hs.addView(fieldTableContainer)

        addView(hs)

        addView(fieldErr)

    }

}

fun singleView(
    container: RadioGroup,
    form: Form,
    field: Fields,
    renderedData: RenderedData?,
    viewmodel: UIViewModel,
    choiceItems: ArrayList<ChoiceItem>,
    fieldErr: TextView,
    fromEdit: Boolean,
    listener: ViewsListener,
    position_: Int
) {
    with(container) {
        choiceItems.forEach { choiceItem ->
            var initValue = false

            renderedData?.value?.let {
                if (it.equals(choiceItem.title)) {
                    choiceItem.slug?.let { slug ->
                        viewmodel.addKeyValueToReq(field.slug!!, slug)
                    }
                    initValue = true
                } else {

                }

            }
            val radioBtn = createRadioBtn(context, initValue, fromEdit, form).also {
                it.text = choiceItem.title ?: ""
                if (initValue) {
                    it.performClick()
                } else {

                }
            }

            radioBtn.setOnClickListener {
                choiceItem.slug?.let { slug ->
                    viewmodel.addKeyValueToReq(field.slug!!, slug)
                }
                fieldErr.invisible()

            }
            addView(radioBtn)

        }

    }

}






