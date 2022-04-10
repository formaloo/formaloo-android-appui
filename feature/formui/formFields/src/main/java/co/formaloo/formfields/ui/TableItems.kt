package co.formaloo.formfields.ui

import android.content.Context
import android.graphics.Typeface
import android.view.View
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.widget.AppCompatCheckBox
import co.formaloo.common.Constants
import co.formaloo.common.cellWith
import co.formaloo.common.smallPadding
import co.formaloo.common.xsPadding
import co.formaloo.model.form.ChoiceItem
import co.formaloo.model.form.Form
import co.formaloo.formfields.R
import co.formaloo.formfields.fieldCellBackground

const val CELL_MARGIN=1
fun createTableHeaderRow(
    context: Context,
    form: Form,
    lp: TableRow.LayoutParams,
    items: ArrayList<ChoiceItem>
): View {
    return createTableRow(context, form).apply {
        addView(createTableFixCell(context, form, lp))

        items.forEach { item ->
            addView(createTableCell(context, form, lp, item))
        }

    }

}

fun createTableFixCell(context: Context, form: Form, lp: TableRow.LayoutParams): View {

    return fieldTextView(context, form).apply {
        fieldCellBackground(this,form)

        lp.setMargins(
            5,
            5,
            5,
            5,
        )
        setPadding(
            xsPadding(context),
            smallPadding(context),
            xsPadding(context),
            smallPadding(context)
        )
        text = context.getString(R.string.quesions)
        layoutParams = lp
    }
}

fun createTableCell(
    context: Context,
    form: Form,
    lp: TableRow.LayoutParams,
    item: ChoiceItem
): TextView {
    return fieldTextView(context, form).apply {

        setTypeface(typeface, Typeface.BOLD)

        text = item.title
        tag = item.slug

        fieldCellBackground(this,form)
//        setBackgroundResource(R.color.white)

//        lp.setMargins(
//            CELL_MARGIN,
//            CELL_MARGIN,
//            CELL_MARGIN,
//            CELL_MARGIN,
//        )

        setPadding(
            xsPadding(context),
            smallPadding(context),
            xsPadding(context),
            smallPadding(context)
        )

        when (item.type) {
            Constants.TABLE_COLUMN_TYPE_BOOLEAN -> {


            }
            Constants.TABLE_COLUMN_TYPE_NUMBER -> {
                lp.width = cellWith(context)

            }
            Constants.TABLE_COLUMN_TYPE_TEXT -> {
                lp.width = cellWith(context)

            }

            else -> {

            }

        }

        layoutParams = lp

    }

}
fun createTableCheckBoxCell(
    context: Context,
    lp: TableRow.LayoutParams,
    initValue: Boolean,
    fromEdit: Boolean,
    form: Form
): AppCompatCheckBox {
    return createCheckBox(context, initValue,fromEdit,form).apply {
        lp.setMargins(
            CELL_MARGIN,
            CELL_MARGIN,
            CELL_MARGIN,
            CELL_MARGIN,
        )

        setPadding(
            xsPadding(context),
            smallPadding(context),
            xsPadding(context),
            smallPadding(context)
        )
        layoutParams = lp


    }}


