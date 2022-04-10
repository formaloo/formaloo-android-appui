package co.formaloo.formfields

import android.R
import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.text.Html
import android.text.InputType
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.annotation.ColorInt
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import co.formaloo.common.Constants
import co.formaloo.common.GlideImageGetter
import co.formaloo.common.MyTagHandler
import co.formaloo.common.extension.invisible
import co.formaloo.common.*
import co.formaloo.common.getDateOnLocale
import co.formaloo.model.form.Fields
import co.formaloo.model.form.Form
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.github.gcacace.signaturepad.views.SignaturePad
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.squareup.picasso.Picasso
import timber.log.Timber
import java.lang.reflect.Field
import java.net.URI
import java.net.URISyntaxException
import java.net.URLEncoder
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.math.roundToInt

fun fieldDesc(view: TextView, field: Fields) {
    val context = view.context

    var description = field.description ?: ""

//        field.description?.let {
//            description = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                Html.fromHtml(it, Html.FROM_HTML_MODE_LEGACY).toString()
//            } else {
//                Html.fromHtml(it).toString()
//            }
//        }

    val type = if (field.sub_type != null) {
        field.sub_type
    } else {
        field.type
    }

    val descPlus = when (type) {

        Constants.FILE -> {
            if (field.max_size != null) {
                context.getString(co.formaloo.common.R.string.max_file_size) + " : " + field.max_size

            } else {
                ""
            }
        }

        Constants.TIME -> {
            "${context.getString(co.formaloo.common.R.string.from)} : ${field?.from_time ?: "-"}" +
                    "  " +
                    "${context.getString(co.formaloo.common.R.string.to)} : ${field?.to_time ?: "-"}"

        }

        Constants.DATE -> {
            "${context.getString(co.formaloo.common.R.string.from_date)} : ${
                getDateOnLocale(
                    field.from_date ?: ""
                ) ?: "-"}" +
                    "  " +
                    "${context.getString(co.formaloo.common.R.string.to_date)} : ${
                        getDateOnLocale(
                            field.to_date ?: ""
                        ) ?: "-"
                    }"
        }
        Constants.SHORT_TEXT -> {
            if (field.max_length != null) {
                context.getString(co.formaloo.common.R.string.max_char_lenght) + " : " + field.max_length

            } else {
                ""
            }
        }
        else -> {

            ""
        }
    }

    val finalTxt = if (descPlus.isNotEmpty()) {
        "$description ( $descPlus )"

    } else {
        description
    }
    view.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(
            finalTxt, Html.FROM_HTML_MODE_LEGACY, GlideImageGetter(view),
            MyTagHandler()
        )
    } else {
        Html.fromHtml(finalTxt, GlideImageGetter(view), MyTagHandler())
    }
}


fun selectedFieldBackground(view: View, form: Form?) {
    val shapedrawable = GradientDrawable()
    val errdrawable = GradientDrawable()

    form?.text_color?.let {
        val fieldColor = getHexColor(it) ?: convertRgbToHex("242", "242", "242")
        shapedrawable.setColor(parseColor(fieldColor))

    }
    form?.text_color?.let {
        val borderColor = getHexColor(it) ?: convertRgbToHex("255", "255", "255")
        shapedrawable.setStroke(4, parseColor(borderColor))

    }


    shapedrawable.cornerRadius = cornerRadius

    errdrawable.setColor(parseColor("#1BFB9B9B"))
    errdrawable.setStroke(strokeWidth, parseColor("#F43A3B"))

    view.background = shapedrawable


}

const val cornerRadius=9f
const val strokeWidth=2

fun setSelectedTextColor(view: TextView, form: Form?) {
    val txtColor = getHexColor(form?.background_color) ?: convertRgbToHex("55", "55", "55")
    view.setTextColor(parseColor(txtColor))

}

fun setSelectedTextColor(view: CheckBox, form: Form?) {
    val txtColor = getHexColor(form?.background_color) ?: convertRgbToHex("55", "55", "55")
    view.setTextColor(parseColor(txtColor))

}


fun setEdtInputType(view: TextInputEditText, type: String?) {
    val context = view.context
    view.inputType = when (type) {

        context.getString(co.formaloo.common.R.string.choice_type_text) -> {
            InputType.TYPE_CLASS_TEXT

        }
        context.getString(co.formaloo.common.R.string.choice_type_number) -> {
            InputType.TYPE_CLASS_NUMBER

        }
        else -> {
            InputType.TYPE_CLASS_TEXT
        }
    }
}


fun imageTintList(view: ImageButton, color: String?) {
    view.imageTintList = getColorStateList(color)
}

@ColorInt
fun darkenColor(@ColorInt color: Int): Int {
    return Color.HSVToColor(FloatArray(3).apply {
        Color.colorToHSV(color, this)
        this[2] *= 0.8f
    })
}


fun field_desc(view: TextView, field: Fields) {
    val context = view.context

    var description = field.description ?: ""

//        field.description?.let {
//            description = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                Html.fromHtml(it, Html.FROM_HTML_MODE_LEGACY).toString()
//            } else {
//                Html.fromHtml(it).toString()
//            }
//        }

    val type = if (field.sub_type != null) {
        field.sub_type
    } else {
        field.type
    }

    val descPlus = when (type) {

        Constants.FILE -> {
            if (field.max_size != null) {
                context.getString(co.formaloo.formfields.R.string.max_file_size) + " : " + field.max_size

            } else {
                ""
            }
        }

        Constants.TIME -> {
            "${context.getString(co.formaloo.formfields.R.string.from)} : ${field?.from_time ?: "-"}" +
                    "  " +
                    "${context.getString(co.formaloo.formfields.R.string.to)} : ${field?.to_time ?: "-"}"

        }

        Constants.DATE -> {
            "${context.getString(co.formaloo.formfields.R.string.from_date)} : ${getDateOnLocale(field.from_date ?: "") ?: "-"}" +
                    "  " +
                    "${context.getString(co.formaloo.formfields.R.string.to_date)} : ${
                        getDateOnLocale(
                            field.to_date ?: ""
                        ) ?: "-"
                    }"
        }
        Constants.SHORT_TEXT -> {
            if (field.max_length != null) {
                context.getString(co.formaloo.formfields.R.string.max_char_lenght) + " : " + field.max_length

            } else {
                ""
            }
        }
        else -> {

            ""
        }
    }

    val finalTxt = if (descPlus.isNotEmpty()) {
        "$description ( $descPlus )"

    } else {
        description
    }
    view.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(
            finalTxt, Html.FROM_HTML_MODE_LEGACY, GlideImageGetter(view),
            MyTagHandler()
        )
    } else {
        Html.fromHtml(finalTxt, GlideImageGetter(view), MyTagHandler())
    }
}



fun convertStringToDate(time: String): String? {
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    val sdf2 = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    var date: Date? = null
    var datestr: String? = null

    if (time.isNotEmpty())
        try {
            date = sdf.parse(time)
            datestr = sdf2.format(date)

        } catch (e: Exception) {


        }

    return datestr

}
fun setHtmlTxt(txv: TextView, txt: String?) {
    txt?.let {
        txv.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(
                txt, Html.FROM_HTML_MODE_LEGACY, GlideImageGetter(txv),
                MyTagHandler()
            )
        } else {
            Html.fromHtml(txt, GlideImageGetter(txv), MyTagHandler())
        }
    }

}

fun setTextColor(view: TextView, color: String?) {
    val txtColor = getHexColor(color) ?: convertRgbToHex("55", "55", "55")
    view.setTextColor(parseColor(txtColor))

}

fun setTxtClr(view: TextView, color: String?) {
    val txtColor = getHexColor(color) ?: convertRgbToHex("55", "55", "55")
    view.setTextColor(parseColor(txtColor))

}


fun setPenColor(view: SignaturePad, color: String?) {
    val txtColor = getHexColor(color) ?: convertRgbToHex("55", "55", "55")
    view.setPenColor(parseColor(txtColor))

}

fun loadImageColorRounded(view: ImageView, color: String?) {
    val btnColor = if (color != null && color.isNotEmpty()) {
        getHexColor(color) ?: convertRgbToHex("55", "55", "55")
    } else {
        convertRgbToHex("55", "55", "55")
    }
    view.setImageDrawable(ColorDrawable(parseColor(btnColor)))

}

fun loadImageRounded(view: ImageView, url: String?) {
    url?.let {
        val link = getUrlWithoutParameters(url)
        Glide.with(view.context).load(link).apply(RequestOptions.circleCropTransform())
            .into(view)

    }
}

fun nps_color(view: AppCompatButton, form: Form, status: Boolean?) {
    val txtColor = getHexColor(form.text_color) ?: convertRgbToHex("55", "55", "55")
    val fieldColor = getHexColor(form.field_color) ?: convertRgbToHex("242", "242", "242")

    view.background = null
    view.setTextColor(parseColor(txtColor))

    status?.let {

        if (it) {
            view.background = ColorDrawable(parseColor(txtColor))
            view.setTextColor(parseColor(fieldColor))

        } else {
            view.background = null
            view.setTextColor(parseColor(txtColor))

        }
    }

}


fun pin_color(view: AppCompatEditText?, form: Form?) {
    val txtColor = getHexColor(form?.text_color) ?: convertRgbToHex("55", "55", "55")
    val fieldColor = getHexColor(form?.field_color) ?: convertRgbToHex("242", "242", "242")

//        val shapedrawable = GradientDrawable()
//        shapedrawable.setStroke(strokeWidth, parseColor(txtColor))
//        shapedrawable.cornerRadius = cornerRadius
//        view.background = shapedrawable

    view?.setTextColor(parseColor(txtColor))
//    view?.setLineColor(parseColor(txtColor))
//    view?.cursorColor = parseColor(txtColor)

}


fun progress_color(view: ProgressBar, form: Form?) {
    val txtColor = getHexColor(form?.text_color) ?: convertRgbToHex("55", "55", "55")

    val colorList = ColorStateList(
        arrayOf(
            intArrayOf(-R.attr.state_enabled),
            intArrayOf(R.attr.state_enabled)
        ), intArrayOf(
            parseColor(txtColor) //disabled
            , parseColor(txtColor) //enabled
        )
    )

    view.progressTintList = colorList
}


fun divider_background(view: View, color: String?) {
    val txtColor = getHexColor(color) ?: convertRgbToHex("55", "55", "55")
    view.setBackgroundColor(parseColor(txtColor))

}


fun setTextColor(view: AppCompatButton, color: String?) {
    val txtColor = getHexColor(color) ?: convertRgbToHex("55", "55", "55")
    view.setTextColor(parseColor(txtColor))

}


fun fieldBackground(view: TextInputEditText, color: String?) {
    val backColor = getHexColor(color) ?: convertRgbToHex("242", "242", "242")
    view.setBackgroundColor(parseColor(backColor))
}


fun openVideoLink(webview: WebView, url: String?) {
    if (url != null) {
        webview.settings.userAgentString = "Android"
        webview.settings.loadWithOverviewMode = true
        webview.settings.setJavaScriptEnabled(true)
        webview.settings.useWideViewPort = true
        webview.settings.databaseEnabled = true
        webview.settings.allowContentAccess = true
        webview.settings.allowFileAccessFromFileURLs = true
        webview.settings.domStorageEnabled = true
        webview.settings.allowFileAccess = true
        webview.settings.setGeolocationEnabled(true)
        webview.settings.setAppCacheEnabled(true)
        webview.settings.setSupportMultipleWindows(true)
        webview.settings.cacheMode = WebSettings.LOAD_DEFAULT
        webview.setInitialScale(1)

        /*
        directUrl: `https://www.aparat.com/v/${videoId}/` // what you get from aparat
        regex: '/(?:http[s]?:\/\/)?(?:www.)?aparat\.com\/v\/([^\/\?\&]+)\/?/'
        embedUrl: `https://www.aparat.com/video/video/embed/videohash/${videoId}/vt/frame`,
        html: '<iframe src={embedUrl} width="100%" height="400" frameborder="0"></iframe>',
        */

        /*
        directUrl: `https://www.youtube.com/watch?v=${videoId}/` // what you get from youtube
        regex: '/(?:https?:\/\/)?(?:www\.)?(?:(?:youtu\.be\/)|(?:youtube\.com)\/(?:v\/|u\/\w\/|embed\/|watch))(?:(?:\?v=)?([^#&?=]*))?((?:[?&]\w*=\w*)*)/'
        embedUrl: `https://www.youtube.com/embed/${videoId}`,
        html: '<iframe src={embedUrl} style="width:100%;" height="400px" frameborder="0"></iframe>',
        */

        val youtuRegex =
            "/(?:https?://)?(?:www\\.)?(?:(?:youtu\\.be/)|(?:youtube\\.com)/(?:v/|u/\\w/|embed/|watch))(?:(?:\\?v=)?([^#&?=]*))?((?:[?&]\\w*=\\w*)*)/"
        val aparatRegex =
            "/(?:http[s]?://)?(?:www.)?aparat\\.com/v/([^/?&]+)/?/"

        val isAparat = url.contains("aparat")

        val pattern = when {
            isAparat -> {
                Pattern.compile(
                    aparatRegex,
                    Pattern.CASE_INSENSITIVE
                )
            }
            else -> {
                Pattern.compile(
                    youtuRegex,
                    Pattern.CASE_INSENSITIVE
                )
            }
        }

        val matcher = pattern.matcher(url)
        if (matcher.find()) {
            val group = matcher.group(1)

            val link = when {
                isAparat -> {
                    "https://www.aparat.com/video/video/embed/videohash/${group}/vt/frame"
                }
                else -> {
                    "https://www.youtube.com/embed/${group}"
                }
            }
            Timber.e("link $link")
            webview.loadUrl(link)
            webview.webViewClient = WebViewClient()
        }


    } else {
        webview.invisible()
    }

}


fun fieldBackground(view: View, form: Form?) {
    val shapedrawable = GradientDrawable()
    val errdrawable = GradientDrawable()

    form?.field_color?.let {
        val fieldColor = getHexColor(it) ?: convertRgbToHex("242", "242", "242")
        shapedrawable.setColor(parseColor(fieldColor))

    }
    form?.border_color?.let {
        val borderColor = getHexColor(it) ?: convertRgbToHex("255", "255", "255")
        shapedrawable.setStroke(strokeWidth, parseColor(borderColor))

    }


    shapedrawable.cornerRadius = cornerRadius

    errdrawable.setColor(parseColor("#1BFB9B9B"))
    errdrawable.setStroke(strokeWidth, parseColor("#F43A3B"))

    view.background = shapedrawable


}

fun fieldCellBackground(view: View, form: Form?) {
    val shapedrawable = GradientDrawable()
    val errdrawable = GradientDrawable()

    form?.field_color?.let {
        val fieldColor = getHexColor(it) ?: convertRgbToHex("242", "242", "242")
        shapedrawable.setColor(parseColor(fieldColor))

    }
    form?.border_color?.let {
        val borderColor = getHexColor(it) ?: convertRgbToHex("255", "255", "255")
        shapedrawable.setStroke(strokeWidth /2, parseColor(borderColor))

    }
    errdrawable.setColor(parseColor("#1BFB9B9B"))
    errdrawable.setStroke(strokeWidth /2, parseColor("#F43A3B"))

    view.background = shapedrawable


}


fun reverseFieldBackground(view: View, form: Form?) {
    val shapedrawable = GradientDrawable()
    val errdrawable = GradientDrawable()

    form?.text_color?.let {
        val fieldColor = getHexColor(it) ?: convertRgbToHex("242", "242", "242")
        shapedrawable.setColor(parseColor(fieldColor))

    }

    shapedrawable.cornerRadius = cornerRadius

    errdrawable.setColor(parseColor("#1BFB9B9B"))
    errdrawable.setStroke(strokeWidth, parseColor("#F43A3B"))

    view.background = shapedrawable


}


fun textCursorDrawable(view: TextInputEditText, color: String?) {
    val txtColor = getHexColor(color) ?: convertRgbToHex("242", "242", "242")
    setCursorColor(view, parseColor(txtColor))
}

fun fieldBackgroundShape(view: View?, form: Form?) {
    val shapedrawable = GradientDrawable()

    val fieldColor = getHexColor(form?.field_color) ?: convertRgbToHex("242", "242", "242")
    shapedrawable.setColor(parseColor(fieldColor))

    val borderColor = getHexColor(form?.border_color) ?: convertRgbToHex("255", "255", "255")
    shapedrawable.setStroke(strokeWidth, parseColor(borderColor))

    shapedrawable.cornerRadius = cornerRadius

    view?.background = shapedrawable

}

fun fieldBackground(view: View, form: Form?, hasErr: Boolean?) {
    val shapedrawable = GradientDrawable()
    val errdrawable = GradientDrawable()

    form?.field_color?.let {
        val fieldColor = getHexColor(it) ?: convertRgbToHex("242", "242", "242")
        shapedrawable.setColor(parseColor(fieldColor))

    }

    form?.border_color?.let {
        val borderColor = getHexColor(it) ?: convertRgbToHex("255", "255", "255")
        shapedrawable.setStroke(strokeWidth, parseColor(borderColor))

    }


    shapedrawable.cornerRadius = cornerRadius

    errdrawable.setColor(parseColor("#1BFB9B9B"))
    errdrawable.setStroke(strokeWidth, parseColor("#F43A3B"))

    if (hasErr == true) {
        view.background = errdrawable

    } else {
        view.background = shapedrawable
    }


}

fun fieldErrorBackground(view: View) {
    val errdrawable = GradientDrawable()
    errdrawable.setColor(parseColor("#1BFB9B9B"))
    errdrawable.setStroke(strokeWidth, parseColor("#F43A3B"))
    view.background = errdrawable


}

fun btnBackgroundShape(view: View, color: String?) {
    val shapedrawable = GradientDrawable()

    val fieldColor = getHexColor(color) ?: convertRgbToHex("242", "242", "242")
    shapedrawable.setColor(parseColor(fieldColor))

    shapedrawable.cornerRadius = cornerRadius

    view.background = shapedrawable

}

fun npsBackgroundShape(view: View, color: String?) {
    val shapedrawable = GradientDrawable()

    val fieldColor = getHexColor(color) ?: convertRgbToHex("242", "242", "242")
    shapedrawable.setColor(parseColor(fieldColor))

    view.background = shapedrawable

}

fun sectionBackgroundShape(view: View, form: Form?) {
    val shapedrawable = GradientDrawable()

    val borderColor = getHexColor(form?.border_color) ?: convertRgbToHex("255", "255", "255")
    shapedrawable.setStroke(strokeWidth, parseColor(borderColor))

    shapedrawable.cornerRadius = cornerRadius

    view.background = shapedrawable

}

fun TextInputLayoutStyle(view: TextInputLayout, form: Form?) {

    val borderColor = getHexColor(form?.border_color) ?: convertRgbToHex("255", "255", "255")
    val borderColorList = ColorStateList(
        arrayOf(
            intArrayOf(-R.attr.state_enabled),
            intArrayOf(R.attr.state_enabled)
        ), intArrayOf(
            parseColor(borderColor) //disabled
            , parseColor(borderColor) //enabled
        )
    )


    val fieldColor = getHexColor(form?.field_color) ?: convertRgbToHex("242", "242", "242")
    val fieldColorList = ColorStateList(
        arrayOf(
            intArrayOf(-R.attr.state_enabled),
            intArrayOf(R.attr.state_enabled)
        ), intArrayOf(
            parseColor(fieldColor) //disabled
            , parseColor(fieldColor) //enabled
        )
    )


    view.setBoxStrokeColorStateList(borderColorList)
    view.boxBackgroundColor = parseColor(fieldColor)
}

fun setTextColor(view: TextInputEditText, color: String?) {
    val txtColor = getHexColor(color) ?: convertRgbToHex("55", "55", "55")
    view.setTextColor(parseColor(txtColor))

}

fun getHexColor(color_: String?): String? {

    var hexString: String? = null
    var color = color_
    try {
        if (color != null) {
            color = color.replace("{\"", "")
            color = color.replace("\"", "")

            val a = color.substring(color.indexOf("a:") + 2, color.indexOf("}"))
            val r = color.substring(color.indexOf("r:") + 2, color.indexOf(",g"))
            val g = color.substring(color.indexOf("g:") + 2, color.indexOf(",b"))
            val b = color.substring(color.indexOf("b:") + 2, color.indexOf(",a"))

            hexString = convertRgbToHex(r, g, b)

        }
    } catch (e: Exception) {
    }

    return hexString
}

fun convertRgbToHex(r: String, g: String, b: String): String {
    return "#${
        Integer.toHexString(
            Color.rgb(
                Integer.parseInt(r),
                Integer.parseInt(g),
                Integer.parseInt(b)
            )
        )
    }"
}

@SuppressLint("SoonBlockedPrivateApi")
fun setCursorColor(view: TextInputEditText, color: Int) {
    try {
        // Get the cursor resource id
        var field: Field = TextView::class.java.getDeclaredField("mCursorDrawableRes")
        field.setAccessible(true)
        val drawableResId: Int = field.getInt(view)

        // Get the editor
        field = TextView::class.java.getDeclaredField("mEditor")
        field.isAccessible = true
        val editor: Any = field.get(view)

        // Get the drawable and set a color filter
        val drawable: Drawable? = AppCompatResources.getDrawable(view.context, drawableResId)
        drawable?.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        val drawables = arrayOf(drawable, drawable)

        // Set the drawables
        field = editor.javaClass.getDeclaredField("mCursorDrawable")
        field.setAccessible(true)
        field.set(editor, drawables)
    } catch (ignored: java.lang.Exception) {
    }
}

fun loadCustomImageUrl(view: ImageView, url: String?) {
    if (url != null && url.isNotEmpty()) {
        val link = getUrlWithoutParameters(url)
        Picasso.get().load(link).into(view)

    }

}

@Throws(URISyntaxException::class)
private fun getUrlWithoutParameters(thePath: String): String {
    val url = URLEncoder.encode(thePath, "UTF-8")

    val uri = URI(url)
    return URI(
        uri.scheme,
        uri.authority,
        uri.path,
        null,  // Ignore the query part of the input url
        uri.fragment
    ).toString()
}

fun setpricetxt(view: EditText, txt: Any?) {

    view.setText("$txt")
}

fun setpricetxt(view: TextView, price: String?) {
    if (price != null && price != "null" && price.isNotEmpty()) {
        val roundToInt = price.toDouble().roundToInt()
        val formattedPrice = DecimalFormat("##,###,###").format(roundToInt)

        view.text = "$formattedPrice"

    }else{

    }
}


