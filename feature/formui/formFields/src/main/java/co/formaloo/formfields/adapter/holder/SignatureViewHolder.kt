package co.formaloo.formfields.adapter.holder

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import co.formaloo.common.FileUtils
import co.formaloo.formfields.R
import co.formaloo.formfields.databinding.LayoutUiSignatureItemBinding
import co.formaloo.formfields.setPenColor
import co.formaloo.formfields.setTextColor
import com.github.gcacace.signaturepad.views.SignaturePad
import kotlinx.android.synthetic.main.layout_ui_field_footer.view.*
import timber.log.Timber
import java.io.ByteArrayOutputStream
import kotlin.random.Random
import kotlinx.android.synthetic.main.layout_ui_field_header.view.*
import co.formaloo.common.extension.invisible
import co.formaloo.common.extension.visible

class SignatureViewHolder(itemView: View) : BaseVH(itemView) {

    val binding = LayoutUiSignatureItemBinding.bind(itemView)

    override fun initView(){
        val context = itemView.context

        val fieldUiHeader = binding.signatureLay
        fillHeaderData(fieldUiHeader.key_txv,fieldUiHeader.desc_txv)

        errStatus.observe(lifeCycleOwner){
            fieldBackgroundUI(binding.signaturePadLay,it)
            if (it == true){

                displayErrLay(binding.errLay, binding.errLay.error_txv)

            }
        }

        binding.clearButton.setOnClickListener {
            binding.signaturePad.clear()
            viewmodel.removeFileFromReq(field)

        }

        binding.saveButton.setOnClickListener {
             getImageUri(context,binding.signaturePad.signatureBitmap)?.let { uri->
                 FileUtils(context).getPath(uri)?.let { path ->
                     Timber.e("Path $path");
                     viewmodel.addFileToReq(field, path)
                     binding.saveButton.text = context.getString(R.string.signature_confirmed)

                 }
             }


        }

        binding.signaturePad.setOnSignedListener(object : SignaturePad.OnSignedListener {
            override fun onStartSigning() {
                hideErr(binding.errLay,viewmodel)

            }

            override fun onSigned() {
                binding.saveButton.visible()
                binding.clearButton.isEnabled = true;
            }

            override fun onClear() {
                binding.saveButton.invisible()
                binding.saveButton.text = context.getString(R.string.confirm_signature)
                binding.clearButton.isEnabled = false;
            }

        })

        setPenColor(binding.signaturePad,form.text_color)
        setTextColor(binding.clearButton,form.text_color)
        setTextColor(binding.saveButton,form.text_color)

        fieldRenderedData?.value?.let {

        }

    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path: String = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Signature${Random.nextInt()}", null)
        return Uri.parse(path)
    }


}
