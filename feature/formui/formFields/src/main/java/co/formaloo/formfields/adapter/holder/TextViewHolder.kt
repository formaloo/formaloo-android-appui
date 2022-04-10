package co.formaloo.formfields.adapter.holder

import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import co.formaloo.common.Constants

import co.formaloo.common.getDeviceName
import co.formaloo.model.form.Fields
import co.formaloo.formfields.R
import co.formaloo.formfields.TextInputLayoutStyle
import co.formaloo.formfields.databinding.LayoutUiEdtItemBinding
import co.formaloo.formfields.setTextColor
import co.formaloo.formfields.textCursorDrawable
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.layout_ui_field_footer.view.*
import kotlinx.android.synthetic.main.layout_ui_field_header.view.*

class TextViewHolder(itemView: View) : BaseVH(itemView) {

    val binding = LayoutUiEdtItemBinding.bind(itemView)

    val ANDROID_VERSION_SLUG_FA = "vKrbp6DM"
    val DEVICE_MODEL_SLUG_FA  = "WV28Sf4V"
    val ANDROID_VERSION_SLUG_EN = "MLsUKuAC"
    val DEVICE_MODEL_SLUG_EN = "lzQeDDnk"

    override fun initView(){

        textCursorDrawable(binding.valueEdt,form.text_color)
        setTextColor(binding.valueEdt,form.text_color)

        TextInputLayoutStyle(binding.edtValueLayout,form)

        errStatus.observe(lifeCycleOwner){
            if (it == true){

                displayErrLay(binding.errLay, binding.errLay.error_txv)

            }
        }
        val fieldUiHeader = binding.editLay
        fillHeaderData(fieldUiHeader.key_txv,fieldUiHeader.desc_txv)

        checkDataValueType(binding.valueEdt, field)

        val context = binding.errLay.context
        fieldRenderedData?.value?.let {
            binding.valueEdt.setText(it.toString())
            viewmodel.addKeyValueToReq(field.slug!!, it.toString())

        }

        if (fromEdit) {
            binding.valueEdt.keyListener = null
            binding.valueEdt.movementMethod = null
        } else {

        }


        val myTextWatcher = object : TextWatcher {
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
                hideErr(binding.errLay, viewmodel)

            }

            override fun afterTextChanged(editable: Editable) {
                val input = editable.toString()
                if (input.isNotEmpty()) {
                    if (checkNumberInput(input)) {
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

        myTextWatcher.updatePosition(position_)
        binding.valueEdt.addTextChangedListener(myTextWatcher)

        if (field.type == Constants.Hidden) {
            var device=""
            var version=""

            device=DEVICE_MODEL_SLUG_EN
            version=ANDROID_VERSION_SLUG_EN

            if (field.slug == device) {
                val deviceName = getDeviceName()
                binding.valueEdt.setText(deviceName)
                viewmodel.addKeyValueToReq(field.slug!!, deviceName?:"")

            } else if (field.slug == version) {
                val release = android.os.Build.VERSION.RELEASE
                binding.valueEdt.setText(release)
                viewmodel.addKeyValueToReq(field.slug!!, release)

            }
        } else {

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

}
