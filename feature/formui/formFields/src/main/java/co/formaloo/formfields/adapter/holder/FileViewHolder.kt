package co.formaloo.formfields.adapter.holder

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import co.formaloo.common.Constants
import co.formaloo.common.Constants.all
import co.formaloo.common.Constants.document
import co.formaloo.common.Constants.image
import co.formaloo.common.*
import co.formaloo.formfields.R
import co.formaloo.formfields.databinding.LayoutUiFileItemBinding
import co.formaloo.formfields.getHexColor
import kotlinx.android.synthetic.main.layout_ui_field_footer.view.*
import kotlinx.android.synthetic.main.layout_ui_field_header.view.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber
import co.formaloo.common.extension.invisible
import co.formaloo.common.extension.visible
class FileViewHolder(itemView: View) : BaseVH(itemView), KoinComponent {

    val binding = LayoutUiFileItemBinding.bind(itemView)
    val baseMethod: BaseMethod by inject()

    @SuppressLint("ResourceType")
    override fun initView() {
        val fieldUiHeader = binding.fileLay
        fillHeaderData(fieldUiHeader.key_txv,fieldUiHeader.desc_txv)

        errStatus.observe(lifeCycleOwner) {
            fieldBackgroundUI(binding.fileBtn, it)
            if (it == true){
                displayErrLay(binding.errLay, binding.errLay.error_txv)

            }
        }
        val context = binding.keyTxv.context
        setViewTxtColorFormTextColor(binding.keyTxv)
        binding.removeFileBtn.setOnClickListener {
    viewmodel.removeFile(field.slug)
        }
        viewmodel.fieldFileName.observe(lifeCycleOwner) {
            it?.let { fieldFileName ->
                binding.fileTxv.text = if (fieldFileName.slug == field.slug) {
                    fieldFileName.title ?: ""
                } else {
                    ""
                }
            }
        }
        fieldRenderedData?.let {
            when (val value = it.value) {
                is String -> {
                    if (value.contains(Constants.HREF)) {
                        var v = value
                        v = v.substring(v.indexOf("\"") + 1)
                        v = v.substring(0, v.indexOf("\""))

                        viewmodel.initFileName(field.slug, context.getString(R.string.open_file))

                        binding.fileTxv.setOnClickListener {
                            listener.openUrl(v)

                        }

                        binding.fileTxv.setTextColor(
                            ContextCompat.getColor(
                                context,
                                android.R.attr.colorPrimary
                            )
                        )
                    }
                }
            }
        }



        field.file_type?.let {
            when (it) {
                image -> {
                    binding.descTxv.text = context.getString(R.string.upload_file_image_Desc)
                    binding.descTxv.visible()
                }
                document -> {
                    binding.descTxv.visible()
                    binding.descTxv.text = context.getString(R.string.upload_file_image_doc)
                }
                all -> {
                    ""
                }
                else -> {
                    ""
                }
            }
        }

        if (fromEdit) {

        } else {
            binding.fileBtn.setOnClickListener {
                hideErr(binding.errLay, viewmodel)
                Timber.e("setOnClickListener file")
                when (field.file_type) {
                    image -> {
                        listener.openFileCameraPicker(field, "image/*", Intent.ACTION_GET_CONTENT)
                    }
                    document -> {
                        listener.openFileCameraPicker(
                            field,
                            "application/*",
                            Intent.ACTION_OPEN_DOCUMENT
                        )
                    }
                    all -> {
                        listener.openFileCameraPicker(field, "*/*", Intent.ACTION_GET_CONTENT)

                    }
                    else -> {
                        listener.openFileCameraPicker(field, "*/*", Intent.ACTION_GET_CONTENT)
                    }
                }

                binding.removeFileBtn.visible()
            }

        }

        getHexColor(form.text_color)?.let {
            val colorStateList = ColorStateList(
                arrayOf(
                    intArrayOf(-android.R.attr.state_enabled),
                    intArrayOf(android.R.attr.state_enabled)
                ), intArrayOf(
                    baseMethod.parseColor(it) //disabled
                    , baseMethod.parseColor(it)//enabled
                )
            )
//            binding.starRating.progressTintList = colorStateList
        }

        viewmodel.resetData.observe(context as LifecycleOwner) {
            it?.let {
                if (it) {
                    binding.fileTxv.invisible()
                    binding.removeFileBtn.invisible()
                } else {

                }
            }
        }

        viewmodel.fieldFileName.observe(context as LifecycleOwner) {
            it?.let {
                if (it.slug == field.slug) {
                    binding.fileTxv.visible()
                    binding.fileTxv.text = it.title
                } else {

                }
            }
        }


    }

}
