package co.formaloo.formfields.ui

import android.content.Context
import android.graphics.Typeface
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import co.formaloo.common.extension.invisible
import co.formaloo.formfields.R
import co.formaloo.formCommon.vm.UIViewModel
import co.formaloo.model.form.Fields


fun createFieldErr(context: Context, field:Fields,uiViewModel: UIViewModel): TextView {
    return TextView(context).apply {
        val lp = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        layoutParams = lp

        setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            context.resources.getDimension(R.dimen.font_large)
        )
        setLineSpacing(0f, 1.33f)


        setTextColor(context.getColor(R.color.colorRed))

        invisible()


        setTypeface(typeface, Typeface.NORMAL)

        uiViewModel.errorField.observe(context as LifecycleOwner) {

            it?.let {errField->
                val slug = errField.slug

                if (slug ==field.slug ||slug== context.getString(R.string.phone_)){
                    text = errField.title?:""
                }
            }

        }

    }
}
