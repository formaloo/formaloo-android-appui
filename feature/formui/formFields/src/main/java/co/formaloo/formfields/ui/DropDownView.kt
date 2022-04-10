package co.formaloo.formfields.ui

import android.content.Context
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout


fun createDropDown(context: Context, ddAdapter:BaseAdapter): CustomSpinner {
    return CustomSpinner(context).apply {
        val lp = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        layoutParams = lp

        adapter=ddAdapter


    }
}
