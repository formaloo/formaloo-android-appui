package co.formaloo.formfields.ui.fields

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import co.formaloo.formCommon.vm.UIViewModel
import co.formaloo.formCommon.listener.ViewsListener
import co.formaloo.model.form.ChoiceItem
import co.formaloo.model.form.Fields
import co.formaloo.model.form.Form
import co.formaloo.model.submit.RenderedData
import co.formaloo.formfields.ui.*

private var matrixRawValue = HashMap<String, HashMap<String, Any>>()

fun fieldMatrix(
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
        val fieldTableContainer = fieldTableContainer(context, field, form, viewmodel)

        val choiceGroups = field.choice_groups ?: arrayListOf()
        val choiceItems = field.choice_items ?: arrayListOf()

        fieldTableContainer.apply {
            matrixView(
                this,
                form,
                field,
                renderedData,
                viewmodel,
                choiceItems,
                choiceGroups,
                lp,
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

fun matrixView(
    tableLayout: TableLayout,
    form: Form,
    field: Fields,
    renderedData: RenderedData?,
    viewmodel: UIViewModel,
    choiceItems: ArrayList<ChoiceItem>,
    choiceGroups: ArrayList<ChoiceItem>,
    lp: TableRow.LayoutParams,
    fieldErr: TextView,
    fromEdit: Boolean,
    listener: ViewsListener,
    position_: Int
) {
    with(tableLayout) {

        renderedData?.raw_value?.let {
            matrixRawValue = it as HashMap<String, HashMap<String, Any>>
        }

        //createHeaderRow
        addView(createTableHeaderRow(context, form, lp, choiceItems))

        choiceGroups.forEach { groupChoice ->

            addView(createTableRow(context, form).apply {
                addView(createTableCell(context, form, lp, groupChoice))

                choiceItems.forEach { choiceItem ->

                    var initValue: Any? = null
                    if (!matrixRawValue.containsKey(groupChoice.slug) && groupChoice.slug != null) {
                        matrixRawValue[groupChoice.slug!!] = HashMap()
                    } else {
                        val values = matrixRawValue[groupChoice.slug ?: ""]?.values
                        if (values is HashMap<*, *> && values.containsKey(choiceItem.slug)) {
                            initValue = values[choiceItem.slug!!]
                        }
                    }

//                            fillMap(
//                                matrixRawValue,
//                                groupChoice,
//                                choiceItem,
//                                false,
//                                viewmodel,
//                                field
//                            )


                    val status = if (initValue is Boolean) {
                        initValue
                    } else false

                    addView(createTableCheckBoxCell(context, lp, status, fromEdit, form).apply {
                        setOnCheckedChangeListener { compoundButton, b ->
//                                    fillMap(
//                                        matrixRawValue,
//                                        groupChoice,
//                                        choiceItem,
//                                        b,
//                                        viewmodel,
//                                        field
//                                    )

                        }
                    })

                }

            })
        }
    }

}






