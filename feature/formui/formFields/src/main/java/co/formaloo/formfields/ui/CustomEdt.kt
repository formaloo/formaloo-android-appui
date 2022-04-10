package co.formaloo.formfields.ui

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.view.ContextThemeWrapper
import co.formaloo.common.extension.invisible
import co.formaloo.common.Constants
import co.formaloo.common.getDeviceName
import co.formaloo.model.form.Fields
import co.formaloo.model.form.Form
import co.formaloo.formfields.R
import co.formaloo.formfields.TextInputLayoutStyle
import co.formaloo.formCommon.vm.UIViewModel
import co.formaloo.formCommon.listener.ViewsListener
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

fun createEdtView(
    context: Context,
    field: Fields,
    form: Form,
    answer: String,
    viewmodel: UIViewModel,
    fromEdit: Boolean,
    listener: ViewsListener,
    position_: Int,
    fieldErr: TextView,
    textWatcher:TextWatcher?
): TextInputLayout {
    checkFieldIsRequired(field, viewmodel)

    var autoFillTxt = answer

    viewmodel.addKeyValueToReq(field.slug!!, answer)

    if (field.type == Constants.Hidden) {
        var device = ""
        var version = ""


        if (field.slug == device) {
            val deviceName = getDeviceName()
            autoFillTxt = deviceName ?: answer
            viewmodel.addKeyValueToReq(field.slug!!, deviceName ?: "")

        } else if (field.slug == version) {
            val release = android.os.Build.VERSION.RELEASE
            autoFillTxt = release ?: answer
            viewmodel.addKeyValueToReq(field.slug!!, release)

        }
    }

    return TextInputLayout(ContextThemeWrapper(context,R.style.TextInputLayoutStyle), null, 0).apply {
        val lp = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT

        )
        layoutParams = lp

        TextInputLayoutStyle(this, form)

        val dynamicTextInputEditText = TextInputEditText(context)
        dynamicTextInputEditText.apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
           checkDataValueType(this, field)

            setText(autoFillTxt)

            if (fromEdit) {
                keyListener = null
                movementMethod = null
            }

            val myTextWatcher =     if (textWatcher==null){
                object : TextWatcher {
                    private var position: Int = position_

                    fun updatePosition(position: Int) {
                        this.position = position
                    }

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
                                //TODO EDT CHANGE FOR DIFFERENT INPUT
                                viewmodel.addKeyValueToReq(field.slug!!, input)

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

            }else{
                textWatcher
            }

            addTextChangedListener(myTextWatcher)

        }

        addView(dynamicTextInputEditText)
    }

}


private fun checkDataValueType(edt: TextInputEditText, field: Fields) {

    field.type?.let { fieldType ->
        when (fieldType) {
            Constants.EMAIL -> {
                setInputType(edt, InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)

            }
            Constants.PHONE -> {
                setInputType(edt, InputType.TYPE_CLASS_PHONE)

            }
            Constants.LONG_TEXT -> {
                setInputType(edt, InputType.TYPE_TEXT_FLAG_MULTI_LINE)
                edt.minLines = 5
            }
            Constants.SHORT_TEXT -> {
                setInputType(edt, InputType.TYPE_CLASS_TEXT)

            }
            Constants.WEBSITE -> {
                setInputType(edt, InputType.TYPE_TEXT_VARIATION_WEB_EDIT_TEXT)

            }
            Constants.NUMBER -> {
                setInputType(edt, InputType.TYPE_CLASS_NUMBER)

            }

        }

    }

    if (field.json_key == "national_number") {
        setInputType(edt, InputType.TYPE_CLASS_NUMBER)

    }
}

private fun setInputType(edt: TextInputEditText, type: Int) {
    edt.inputType = type
    edt.textAlignment = View.TEXT_ALIGNMENT_GRAVITY
    edt.gravity = Gravity.TOP or Gravity.START
    edt.text?.length?.let {
        edt.setSelection(it)
    }

}
