package co.formaloo.formfields.ui.fields

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import co.formaloo.common.Constants
import co.formaloo.common.extension.invisible
import co.formaloo.common.smallPadding
import co.formaloo.common.xsPadding
import co.formaloo.formCommon.listener.ViewsListener
import co.formaloo.formCommon.vm.UIViewModel
import co.formaloo.model.form.ChoiceItem
import co.formaloo.model.form.Fields
import co.formaloo.model.form.Form
import co.formaloo.model.submit.RenderedData
import co.formaloo.formfields.R
import co.formaloo.formfields.fieldCellBackground
import co.formaloo.formfields.ui.*
import org.json.JSONObject
import timber.log.Timber


private var tableRawValue = HashMap<String, HashMap<String, Any>>()
var groupJson = JSONObject()

fun fieldTable(
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
        val columnGroups = field.column_groups ?: arrayListOf()

        fieldTableContainer.apply {
            tableView(
                this,
                form,
                field,
                renderedData,
                viewmodel,
                columnGroups,
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

fun tableView(
    tableLayout: TableLayout,
    form: Form,
    field: Fields,
    renderedData: RenderedData?,
    viewmodel: UIViewModel,
    columnGroups: ArrayList<ChoiceItem>,
    choiceGroups: ArrayList<ChoiceItem>,
    lp: TableRow.LayoutParams,
    fieldErr: TextView,
    fromEdit: Boolean,
    listener: ViewsListener,
    position_: Int
) {

    with(tableLayout) {

        renderedData?.raw_value?.let {
            try {
                val dataStr = it.toString()
                groupJson = JSONObject(dataStr)
            } catch (e: Exception) {
                Timber.e("groupJsonException $e")
            }

        }

        //createHeaderRow
        addView(createTableHeaderRow(context, form, lp, columnGroups))

        choiceGroups.forEach { groupChoice ->
            val rowJson = JSONObject()

            addView(createTableRow(context, form).apply {
                addView(createTableCell(context, form, lp, groupChoice))

                columnGroups.forEach { choiceItem ->

                    var initValue: Any? = null

                    if (!groupJson.has(groupChoice.slug) && groupChoice.slug != null) {

                    } else if (groupJson.has(groupChoice.slug)){

                        val values = groupJson[groupChoice.slug ?: ""]
                        if (values is JSONObject && values.has(choiceItem.slug)) {
                            initValue = values[choiceItem.slug!!]
                        }
                    }

                    val myTextWatcher = createtaxtWatcher(
                        context,
                        viewmodel,
                        field,
                        fieldErr,
                        rowJson,
                        choiceItem,
                        groupChoice
                    )

                    when (choiceItem.type) {
                        co.formaloo.common.Constants.TABLE_COLUMN_TYPE_BOOLEAN -> {
                            val status = if (initValue is Boolean) {
                                initValue
                            } else false
                            addView(
                                createTableCheckBoxCell(
                                    context,
                                    lp,
                                    status,
                                    fromEdit,
                                    form
                                ).apply {
                                    fieldCellBackground(this, form)

                                    setOnCheckedChangeListener { compoundButton, b ->
                                        if (b) {
                                            Timber.e("rowJson1 $rowJson")
                                            rowJson.put(choiceItem.slug ?: "", b)
                                            Timber.e("rowJson2 $rowJson")

                                        } else {
                                            rowJson.remove(choiceItem.slug ?: "")

                                        }

                                        fillJson(
                                            groupJson,
                                            groupChoice,
                                            rowJson,
                                            viewmodel,
                                            field
                                        )

                                    }
                                })

                        }

                        Constants.TABLE_COLUMN_TYPE_NUMBER -> {

                            addView(
                                createEdtView(
                                    context,
                                    field,
                                    form,
                                    if (initValue is String) {
                                        initValue
                                    } else {
                                        ""
                                    },
                                    viewmodel,
                                    fromEdit,
                                    listener,
                                    position_,
                                    fieldErr,
                                    myTextWatcher
                                ).apply {

                                    setBackgroundResource(co.formaloo.formfields.R.color.white)
                                    lp.setMargins(
                                        1,
                                        1,
                                        1,
                                        1,
                                    )
                                    setPadding(
                                        xsPadding(context),
                                        smallPadding(context),
                                        xsPadding(context),
                                        smallPadding(context)
                                    )
                                    layoutParams = lp


                                }
                            )

                        }

                        Constants.TABLE_COLUMN_TYPE_TEXT -> {

                            addView(
                                createEdtView(
                                    context,
                                    field,
                                    form,
                                    if (initValue is String) {
                                        initValue
                                    } else {
                                        ""
                                    },
                                    viewmodel,
                                    fromEdit,
                                    listener,
                                    position_,
                                    fieldErr,
                                    myTextWatcher
                                ).apply {


                                    setBackgroundResource(co.formaloo.formfields.R.color.white)
                                    lp.setMargins(
                                        1,
                                        1,
                                        1,
                                        1,
                                    )
                                    setPadding(
                                        xsPadding(context),
                                        smallPadding(context),
                                        xsPadding(context),
                                        smallPadding(context)
                                    )
                                    layoutParams = lp

                                }
                            )

                        }

                    }

                    groupJson.put(groupChoice.slug ?: "", rowJson)

                }

            })
        }
    }

}



fun fillJson(
    groupJson: JSONObject,
    groupChoice: ChoiceItem,
    rowJson: JSONObject,
    viewmodel: UIViewModel,
    field: Fields
) {
    Timber.e("rowJson $rowJson")
    Timber.e("groupJson1 $groupJson")

    groupJson.put(groupChoice.slug ?: "", rowJson)

    Timber.e("groupJson2 $groupJson")

    viewmodel.addKeyValueToReq(field.slug ?: "", groupJson)


}


fun createtaxtWatcher(
    context: Context,
    viewmodel: UIViewModel,
    field: Fields,
    fieldErr: View,
    jsonRow: JSONObject,
    choiceItem: ChoiceItem,
    groupChoice: ChoiceItem
): TextWatcher {

    return object : TextWatcher {
        override fun beforeTextChanged(
            charSequence: CharSequence,
            i: Int,
            i2: Int,
            i3: Int
        ) {
        }

        override fun onTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {
            fieldErr.invisible()

        }

        override fun afterTextChanged(editable: Editable) {
            val input = editable.toString()
            if (input.isNotEmpty()) {
                if (checkNumberInput(input)) {

                    jsonRow.put(choiceItem.slug ?: "", input)
                    fillJson(
                        groupJson,
                        groupChoice,
                        jsonRow,
                        viewmodel,
                        field
                    )

                } else {
                    viewmodel.setErrorToField(
                        field,
                        "${context.getString(R.string.min_value)} : ${field.min_value ?: "-"}" +
                                "  " +
                                "${context.getString(R.string.max_value)} : ${field.max_value ?: ""}"
                    )
                }
            }
        }

        private fun checkNumberInput(input: String): Boolean {
            val maxValue = field.max_value
            val minValue = field.min_value
            val isBigger = maxValue != null && input.toInt() > maxValue.toInt()
            val isSmaller = minValue != null && input.toInt() < minValue.toInt()

            return !(field.type == Constants.NUMBER && (isBigger || isSmaller))

        }


    }

}



