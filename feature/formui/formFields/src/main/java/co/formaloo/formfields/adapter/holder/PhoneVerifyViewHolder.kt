package co.formaloo.formfields.adapter.holder

import android.os.Build
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import co.formaloo.common.Constants
import co.formaloo.formCommon.vm.UIViewModel
import co.formaloo.model.form.Fields
import co.formaloo.model.form.Form
import co.formaloo.formfields.*
import co.formaloo.formfields.databinding.LayoutUiPhoneVerifyItemBinding
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.layout_ui_field_footer.view.*
import kotlinx.android.synthetic.main.layout_ui_field_header.view.*
import co.formaloo.common.extension.invisible
import co.formaloo.common.extension.visible

class PhoneVerifyViewHolder(itemView: View) : BaseVH(itemView) {
    val binding = LayoutUiPhoneVerifyItemBinding.bind(itemView)
    private var uuid: String? = null

    override fun initView() {
        val context = itemView.context
        val fieldUiHeader = binding.phoneLay
        fillHeaderData(fieldUiHeader.key_txv,fieldUiHeader.desc_txv)

        textCursorDrawable(binding.phoneEdt, form.text_color)
        setTextColor(binding.phoneEdt, form.text_color)

        btnBackgroundShape(binding.verifyBtn, form.text_color)
        setTextColor(binding.verifyBtn, form.submit_text_color)

        pin_color(binding.pinView, form)
        progress_color(binding.phoneProgress, form)

        binding.verifyBtn.setOnClickListener {
            viewmodel.verifyBtnClicked(field)
        }

        viewmodel.verifyLoading.observe(lifeCycleOwner) {
            binding.phoneProgress.isVisible = it ?: false
        }

        viewmodel.phoneToVerify.observe(lifeCycleOwner) {
            it?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    binding.verifyBtn.foreground = if (it.isNotEmpty()) {
                        ContextCompat.getDrawable(context, R.drawable.back_border_gray)
                    } else {
                        ContextCompat.getDrawable(context, R.drawable.back_fill_accent)

                    }
                }
            }
        }

        errStatus.observe(lifeCycleOwner) {
            fieldBackgroundUI(binding.verifyLay, it)
            if (it == true) {
                displayErrLay(binding.errLay, binding.errLay.error_txv)

            }
        }

        val lifecycleOwner = context as LifecycleOwner
        initData(viewmodel, lifecycleOwner, binding, field, form)

        fieldRenderedData?.value?.let {
            binding.phoneEdt.setText(it.toString())
            viewmodel.initPhoneNumber(it.toString())

        }
        if (field.required == true) {
            viewmodel.reuiredField(field)

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
                viewmodel.initPhoneNumber(charSequence.toString())

            }

            override fun afterTextChanged(editable: Editable) {

            }
        }

        myTextWatcher.updatePosition(position_)
        binding.phoneEdt.addTextChangedListener(myTextWatcher)

        val pinTextWatcher = object : TextWatcher {
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
                val length = charSequence.length

                if (length == 6) {
                    viewmodel.sendpinCode(binding.pinView.getText().toString())
                }
            }

            override fun afterTextChanged(editable: Editable) {

            }
        }

        binding.pinView.addTextChangedListener(pinTextWatcher)


    }

    private fun initData(
        viewmodel: UIViewModel,
        lifecycleOwner: LifecycleOwner,
        binding: LayoutUiPhoneVerifyItemBinding,
        fields: Fields,
        form: Form?
    ) {


        viewmodel.verifyLoading.observe(lifecycleOwner) {
            it?.let {
                if (it) {
                    binding.pinView.invisible()
                    binding.verifyBtn.invisible()
                } else {

                }
            }
        }

        viewmodel.requestPhoneData.observe(lifecycleOwner) {
            it?.let {
                binding.phoneProgress.invisible()
                uuid = it.uuid
                binding.pinView.visible()
                binding.verifyBtn.invisible()
            }
        }


        viewmodel.verifyPhoneData.observe(lifecycleOwner) {
            it?.let {
                binding.phoneProgress.invisible()
                binding.pinView.invisible()
                binding.verifyBtn.visible()

                binding.verifyBtn.setText(R.string.phone_has_verified)

                binding.verifyBtn.isClickable = false
                binding.verifyBtn.isFocusable = false

                viewmodel.addKeyValueToReq(fields.slug!!, binding.phoneEdt.text.toString())
                viewmodel.addKeyValueToReq("uuid", uuid ?: "")
                viewmodel.addKeyValueToReq("code", binding.pinView.text.toString())

            }
        }

    }

    private fun checkDataValueType(edt: TextInputEditText, type: String?) {

        type?.let { fieldType ->
            when (fieldType) {
                Constants.EMAIL -> {
                    setInputType(edt, InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)

                }
                Constants.PHONE -> {
                    setInputType(edt, InputType.TYPE_CLASS_PHONE)

                }
                Constants.LONG_TEXT -> {
                    setInputType(edt, InputType.TYPE_TEXT_FLAG_MULTI_LINE)

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

    }

    private fun setInputType(edt: TextInputEditText, type: Int) {
        edt.inputType = type
        edt.text?.length?.let {
            edt.setSelection(it)
        }
    }

}
