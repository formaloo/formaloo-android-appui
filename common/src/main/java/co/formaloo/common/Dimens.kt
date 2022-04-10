package co.formaloo.common

import android.content.Context
import android.util.TypedValue
import co.formaloo.common.R

fun scannerHeight(context:Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        context.resources.getDimension(R.dimen.edt_h_full),
        context.resources.displayMetrics
    ).toInt()
}

fun edtHeight(context:Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        context.resources.getDimension(R.dimen.edt_h),
        context.resources.displayMetrics
    ).toInt()
}

fun normalPadding(context:Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        context.resources.getDimension(R.dimen.padding_standard),
        context.resources.displayMetrics
    ).toInt()
}


fun smallPadding(context:Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        context.resources.getDimension(R.dimen.padding_small),
        context.resources.displayMetrics
    ).toInt()
}


fun xsPadding(context:Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        context.resources.getDimension(R.dimen.padding_xsmall),
        context.resources.displayMetrics
    ).toInt()
}


fun xLargePadding(context:Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        context.resources.getDimension(R.dimen.padding_xlarge),
        context.resources.displayMetrics
    ).toInt()
}


fun normalFont(context:Context): Int {
    return  TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        context.resources.getDimension(R.dimen.font_standard),
        context.resources.displayMetrics
    ).toInt()
}

fun cellWith(context:Context): Int {
    return  TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        context.resources.getDimension(R.dimen.category_item_width),
        context.resources.displayMetrics
    ).toInt()
}
fun cellWithSmall(context:Context): Int {
    return  TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        context.resources.getDimension(R.dimen.img_size_xs),
        context.resources.displayMetrics
    ).toInt()
}
