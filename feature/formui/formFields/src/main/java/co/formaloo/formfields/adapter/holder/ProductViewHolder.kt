package co.formaloo.formfields.adapter.holder

import android.os.Build
import android.view.View
import android.widget.AdapterView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import co.formaloo.common.*
import co.formaloo.model.form.Image
import co.formaloo.formfields.adapter.ProductChangeListener
import co.formaloo.formfields.adapter.ProductImageShaowCaseAdapter
import co.formaloo.formfields.adapter.StringItemsAdapter
import co.formaloo.formfields.databinding.LayoutUiProductItemBinding
import co.formaloo.formfields.loadCustomImageUrl
import co.formaloo.formfields.setTextColor
import co.formaloo.formfields.setpricetxt
import kotlinx.android.synthetic.main.layout_ui_field_footer.view.*
import kotlinx.android.synthetic.main.layout_ui_field_header.view.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ProductViewHolder(itemView: View) : BaseVH(itemView), KoinComponent {

    val binding = LayoutUiProductItemBinding.bind(itemView)
    private val baseMethod: BaseMethod by inject()


    override fun initView() {
        val fieldUiHeader = binding.productLay
        fillHeaderData(fieldUiHeader.key_txv,fieldUiHeader.desc_txv)

        val dropAdapter = StringItemsAdapter()
        val amgAdapter = ProductImageShaowCaseAdapter(field, object : ProductChangeListener {
            override fun changePhoto(item: Image) {
                baseMethod.loadImage(item.image, binding.productImgV)

            }

        })
        binding.productImgRec.apply {
            adapter = amgAdapter
            layoutManager = StaggeredGridLayoutManager(1, RecyclerView.HORIZONTAL)
        }

        val fieldImages = field.images ?: arrayListOf()
        (fieldImages.size > 0).also {
            binding.productImgV.isVisible = it
            binding.productImgRec.isVisible = it
            if (it) {
                val image = fieldImages[0].image
                loadCustomImageUrl(binding.productImgV, image)
                amgAdapter.setCollection(fieldImages)

            } else {

            }
        }

        val unitP = field.unit_price?.toDouble()?.toInt() ?: 1
        setpricetxt(binding.priceTxv, "$unitP")
        setTextColor(binding.priceTxv, form.text_color)

        errStatus.observe(lifeCycleOwner) {
            fieldBackgroundUI(binding.prLay, it)
            if (it == true){

                displayErrLay(binding.errLay, binding.errLay.error_txv)

            }
        }


        binding.productCheckBox.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                binding.valueSpinner.setSelection(1)

            } else {
                binding.valueSpinner.setSelection(0)

            }
        }

        binding.valueSpinner.apply {
            adapter = dropAdapter
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    if (position > 0) {
                        viewmodel.addKeyValueToReq(field.slug!!, position)
                        val count = dropAdapter.listItemsTxt[position].toInt()
                        val total = unitP * count

                        setpricetxt(binding.priceTxv, "$total")

                    } else {
                        setpricetxt(binding.priceTxv, "$unitP")

                        viewmodel.removeKeyValueFromReq(field.slug!!)


                    }
                    binding.productCheckBox.isChecked = position > 0

                }

            }

        }

        if (fromEdit) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                binding.valueSpinner.suppressLayout(true)
            }
        } else {

        }




        rowRenderedData?.forEach {
            if (it.key.equals(field.slug)) {
                val productData = it.value
                val count = productData.raw_value as Double

                binding.valueSpinner.setSelection(count.toInt())

            } else {

            }
            if (it.key.equals("payment_amount")) {
                val payment_amount = it.value
                val totalPrice = payment_amount.raw_value

                setpricetxt(binding.priceTxv, "$totalPrice")

            } else {

            }

        }

        rowRenderedData?.let {
            "payment_amount"

        }

    }


}
