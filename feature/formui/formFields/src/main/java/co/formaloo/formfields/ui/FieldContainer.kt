package co.formaloo.formfields.ui

import android.content.Context
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.LinearLayout.VERTICAL
import android.widget.RadioGroup
import android.widget.TableLayout
import androidx.lifecycle.LifecycleOwner
import co.formaloo.common.smallPadding
import co.formaloo.model.form.Fields
import co.formaloo.model.form.Form
import co.formaloo.formCommon.vm.UIViewModel
import co.formaloo.formfields.fieldBackground
import co.formaloo.formfields.fieldErrorBackground

fun fieldContainer(context: Context, field: Fields, form: Form): LinearLayout {
    return LinearLayout(context).apply {
        orientation = VERTICAL

        val lp = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        lp.setMargins(
            0,
            smallPadding(context),
            0,
            smallPadding(context)
        )

        layoutParams = lp

        val title = fieldTitle(context, form, field)

        val desc = createFieldDesc(context, form, field)

        addView(title)
        addView(desc)
    }
}

fun fieldContainerBorder(
    context: Context,
    field: Fields,
    form: Form,
    viewmodel: UIViewModel
): LinearLayout {
    return LinearLayout(context).apply {
        val lp = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        lp.setMargins(
            0,
            smallPadding(context),
            0,
            smallPadding(context)
        )
        layoutParams = lp
        orientation = VERTICAL
//        fieldBackground(this, form)

        viewmodel.errorField.observe(context as LifecycleOwner) {
            it?.let { errField ->
                if (errField.slug == field.slug) {
                    fieldErrorBackground(this)

                }
            }
        }
    }
}

fun fieldRadioContainerBorder(
    context: Context,
    field: Fields,
    form: Form,
    viewmodel: UIViewModel
): RadioGroup {
    return RadioGroup(context).apply {
        val lp = RadioGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        lp.setMargins(
            0,
            smallPadding(context),
            0,
            smallPadding(context)
        )
        layoutParams = lp
        orientation = VERTICAL
//        fieldBackground(this, form)

        viewmodel.errorField.observe(context as LifecycleOwner) {
            it?.let { errField ->
                if (errField.slug == field.slug) {
//                    fieldErrorBackground(this)

                }
            }
        }
    }
}

fun fieldTableContainer(
    context: Context,
    field: Fields,
    form: Form,
    viewmodel: UIViewModel
): TableLayout {
    return TableLayout(context).apply {
        val lp = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        setPadding(
            2,
            2,
            2,
            2,
        )

        lp.setMargins(
            0,
            smallPadding(context),
            0,
            smallPadding(context)
        )

        layoutParams = lp
        orientation = VERTICAL
        fieldBackground(this, form)

        viewmodel.errorField.observe(context as LifecycleOwner) {
            it?.let { errField ->
                if (errField.slug == field.slug) {
                    fieldErrorBackground(this)

                }
            }
        }
    }
}
