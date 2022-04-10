package co.formaloo.formfields

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import androidx.core.content.ContextCompat
import co.formaloo.formCommon.listener.SelectDateListener
import java.util.*


@SuppressLint("ResourceType")
fun getEnDatePicker(
    context: Context,
    cal: Calendar,
    listener: SelectDateListener
): DatePickerDialog {
    val enDatePicker = DatePickerDialog(
        context, { p0, year, month, day ->
            listener.enDateSelected(year, month, day)

        },
        cal.get(Calendar.YEAR),
        cal.get(Calendar.MONTH),
        cal.get(Calendar.DAY_OF_MONTH)
    )

    enDatePicker.setOnShowListener {
        enDatePicker.getButton(DatePickerDialog.BUTTON_NEGATIVE)
            ?.setTextColor(ContextCompat.getColor(context, R.color.gray2))
        enDatePicker.getButton(DatePickerDialog.BUTTON_POSITIVE)
            ?.setTextColor(ContextCompat.getColor(context, android.R.attr.colorAccent))
        enDatePicker.getButton(DatePickerDialog.BUTTON_NEUTRAL)
            ?.setTextColor(ContextCompat.getColor(context, android.R.attr.colorPrimary))

    }

    return enDatePicker
}



