package co.formaloo.formCommon.base

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat.is24HourFormat
import android.widget.TimePicker
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import co.formaloo.formCommon.R
import co.formaloo.formCommon.listener.SelectTimeListener
import java.util.*

class TimePickerFragment(val timeListener: SelectTimeListener) : DialogFragment(),
    TimePickerDialog.OnTimeSetListener {
    @SuppressLint("ResourceType")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current time as the default values for the picker
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(requireContext(), this, hour, minute, is24HourFormat(activity))
        timePickerDialog.setOnShowListener {
            timePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)
                ?.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorMediumGray))
            timePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE)
                ?.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorAccent2))
            timePickerDialog.getButton(DatePickerDialog.BUTTON_NEUTRAL)
                ?.setTextColor(ContextCompat.getColor(requireContext(), android.R.attr.colorPrimary))

        }
        // Create a new instance of TimePickerDialog and return it
        return timePickerDialog


    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        // Do something with the time chosen by the user
        timeListener.timeSelected(hourOfDay, minute)

    }
}
