package co.formaloo.flashcard.lesson.adapter.holder

import android.R
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import co.formaloo.common.MainBinding
import co.formaloo.flashcard.databinding.LayoutFlashCardMultiItemBinding

import co.formaloo.flashcard.viewmodel.UIViewModel
import co.formaloo.formCommon.Binding.fieldBackground
import co.formaloo.formCommon.Binding.selectedFieldBackground
import co.formaloo.formCommon.Binding.setSelectedTextColor
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import org.apache.commons.lang3.StringUtils

class MultiHolder(view: View) : RecyclerView.ViewHolder(view) {
    val binding = LayoutFlashCardMultiItemBinding.bind(view)
    private var valuesList = arrayListOf<String>()

    fun bindItems(
        item: co.formaloo.model.form.Fields,
        pos: Int,

        form: co.formaloo.model.form.Form,
        uiViewModel: UIViewModel
    ) {
        binding.field = item
        binding.form = form

        binding.fieldUiHeader.field = item
        binding.fieldUiHeader.form = form
        binding.viewmodel = uiViewModel
        binding.lifecycleOwner = binding.choicesListLay.context as LifecycleOwner


        if (item.required == true) {
            uiViewModel.reuiredField(item)

        } else {

        }

        item.choice_items?.let { choiceList ->
            for (choice in choiceList) {
                createNewChoice(
                    choice,
                    item,
                    binding.choicesListLay,
                    form, uiViewModel
                )

            }
        }

    }

    private fun createNewChoice(
        choice: co.formaloo.model.form.ChoiceItem,
        field: co.formaloo.model.form.Fields,
        choicesListLay: LinearLayout,
        form: co.formaloo.model.form.Form,
        uiViewModel: UIViewModel
    ) {
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.bottomMargin = 24

        val ll = LinearLayout(choicesListLay.context)
        ll.layoutParams = layoutParams
        ll.orientation = LinearLayout.HORIZONTAL
        val boxLay =
            createCheckBox(
                choice,
                field,
                choicesListLay,
                form, uiViewModel
            )
        (ll as ViewGroup).addView(boxLay)

        (binding.choicesListLay as ViewGroup).addView(ll)

    }

    private fun createCheckBox(
        choice: co.formaloo.model.form.ChoiceItem,
        item: co.formaloo.model.form.Fields,
        ll: LinearLayout,
        form: co.formaloo.model.form.Form,
        uiViewModel: UIViewModel
    ): View {

        val lp = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        lp.bottomMargin = 48

        val checkBox = CheckBox(ll.context)
        checkBox.apply {
            layoutParams = lp
            setPadding(48, 48, 48, 48)
            minLines = 2
            setButtonDrawable(R.color.transparent);

            choice.title?.let {
                text = it
            }
            choice.image?.let {
                setImage(it, this)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                textAlignment = View.TEXT_ALIGNMENT_CENTER
            }

            val context = context
            setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                context.resources.getDimension(co.formaloo.flashcard.R.dimen.font_xlarge)
            )

            fieldBackground(this, form)
            MainBinding.setTextColor(this, form.text_color)

            setOnCheckedChangeListener { compoundButton, b ->
                if (b) {
                    selectedFieldBackground(this, form)
                    setSelectedTextColor(this, form)

                } else {
                    fieldBackground(this, form)
                    MainBinding.setTextColor(this, form.text_color)

                }

                choice.slug?.let { choiceSlug ->
                    if (b) {
                        valuesList.add(choiceSlug)

                    } else {
                        if (valuesList.contains(choiceSlug)) {
                            valuesList.remove(choiceSlug)

                        } else {

                        }
                    }

                    uiViewModel.addKeyValueToReq(item.slug!!, StringUtils.join(valuesList, ","))

                }

            }
        }


        return checkBox
    }

    private fun setImage(it: String, box: CheckBox) {
        Picasso.get().load(it).into(object : Target {
            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                bitmap?.let {
                    val padding =
                        box.resources.getDimensionPixelSize(co.formaloo.flashcard.R.dimen.padding_2xsmall)
                    val height =
                        box.resources.getDimensionPixelSize(co.formaloo.flashcard.R.dimen.btn_h)

                    val drawable = BitmapDrawable(
                        box.resources,
                        Bitmap.createScaledBitmap(bitmap, 150, height, false)
                    )
                    box.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)

                    box.compoundDrawablePadding = padding

                }
            }

            override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {
                Log.e("TAG", "onBitmapFailed: $e")
            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
            }

        })

    }
}
