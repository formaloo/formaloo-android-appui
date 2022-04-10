package co.formaloo.formfields.ui

import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TableRow
import co.formaloo.model.form.Form


fun createTableRow(context: Context, form: Form): TableRow {
    return TableRow(context).apply {
        val lp = TableRow.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            0
        )
        lp.weight=1f

        lp.setMargins(
            1,
            1,
            1,
            1
        )
        layoutParams = lp

        gravity= Gravity.CENTER
    }
}
