package co.formaloo.formCommon

import android.content.res.ColorStateList
import android.graphics.Color.parseColor
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.text.Html
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.databinding.BindingAdapter
import co.formaloo.common.*
import co.formaloo.common.MainBinding.convertRgbToHex
import co.formaloo.common.MainBinding.getDateOnLocale
import co.formaloo.common.MainBinding.getHexColor
import co.formaloo.common.extension.invisible
import co.formaloo.model.form.Fields
import co.formaloo.model.form.Form
import com.google.android.material.textfield.TextInputLayout
import com.squareup.picasso.Picasso
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber
import java.util.regex.Pattern


object Binding : KoinComponent {
    //RGBA:
// btn_txt_color=255,255,255,100 {"r":255,"g":255,"b":255,"a":1}
// btn_color=229,105,112,100 {"r":229,"g":105,"b":112,"a":1}
// back_color=255,255,255,100 {"r":255,"g":255,"b":255,"a":1}
// border_color=255,255,255,100 {"r":255,"g":255,"b":255,"a":1}
// fields_color=242,242,242,100 {"r":242,"g":242,"b":242,"a":1}
// text_color=55,55,55,100 {"r":55,"g":55,"b":55,"a":1}
    const val cornerRadius = 9f
    const val strokeWidth = 2
    val baseMethod: BaseMethod by inject()

    @JvmStatic
    @BindingAdapter("progress_color")
    fun progress_color(view: ProgressBar, form: Form?) {
        val txtColor = MainBinding.getHexColor(form?.text_color)
            ?: MainBinding.convertRgbToHex("55", "55", "55")

        val colorList = ColorStateList(
            arrayOf(
                intArrayOf(-android.R.attr.state_enabled),
                intArrayOf(android.R.attr.state_enabled)
            ), intArrayOf(
                parseColor(txtColor) //disabled
                , parseColor(txtColor) //enabled
            )
        )

        view.progressTintList = colorList
    }


    @JvmStatic
    @BindingAdapter("selectedFieldBackground")
    fun selectedFieldBackground(view: View, form: Form?) {
        val shapedrawable = GradientDrawable()
        val errdrawable = GradientDrawable()

        form?.text_color?.let {
            val fieldColor = getHexColor(it) ?: convertRgbToHex("242", "242", "242")
            shapedrawable.setColor(baseMethod.parseColor(fieldColor))

        }
        form?.text_color?.let {
            val borderColor = getHexColor(it) ?: convertRgbToHex("255", "255", "255")
            shapedrawable.setStroke(strokeWidth, baseMethod.parseColor(borderColor))

        }


        shapedrawable.cornerRadius = cornerRadius

        errdrawable.setColor(baseMethod.parseColor("#1BFB9B9B"))
        errdrawable.setStroke(strokeWidth, baseMethod.parseColor("#F43A3B"))

        view.background = shapedrawable


    }


    @JvmStatic
    @BindingAdapter("field_desc")
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
                    ) ?: "-"
                }" +
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

    @JvmStatic
    @BindingAdapter("nps_color", "nps_data")
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


    @JvmStatic
    @BindingAdapter("section_background")
    fun sectionBackgroundShape(view: View, form: Form?) {
        val shapedrawable = GradientDrawable()

        val borderColor = getHexColor(form?.border_color) ?: convertRgbToHex("255", "255", "255")
        shapedrawable.setStroke(MainBinding.strokeWidth, parseColor(borderColor))

        shapedrawable.cornerRadius = MainBinding.cornerRadius

        view.background = shapedrawable

    }

    @JvmStatic
    @BindingAdapter("TextInputLayout_style")
    fun TextInputLayoutStyle(view: TextInputLayout, form: Form?) {

        val borderColor = getHexColor(form?.border_color) ?: convertRgbToHex("255", "255", "255")
        val borderColorList = ColorStateList(
            arrayOf(
                intArrayOf(-android.R.attr.state_enabled),
                intArrayOf(android.R.attr.state_enabled)
            ), intArrayOf(
                parseColor(borderColor) //disabled
                , parseColor(borderColor) //enabled
            )
        )


        val fieldColor = getHexColor(form?.field_color) ?: convertRgbToHex("242", "242", "242")
        val fieldColorList = ColorStateList(
            arrayOf(
                intArrayOf(-android.R.attr.state_enabled),
                intArrayOf(android.R.attr.state_enabled)
            ), intArrayOf(
                parseColor(fieldColor) //disabled
                , parseColor(fieldColor) //enabled
            )
        )


        view.setBoxStrokeColorStateList(borderColorList)
        view.boxBackgroundColor = parseColor(fieldColor)
    }


    @JvmStatic
    @BindingAdapter("openVideoLink")
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

    @JvmStatic
    @BindingAdapter("text_color")
    fun setSelectedTextColor(view: CheckBox, form: Form?) {
        val txtColor = getHexColor(form?.background_color) ?: convertRgbToHex("55", "55", "55")
        view.setTextColor(baseMethod.parseColor(txtColor))

    }

    @JvmStatic
    @BindingAdapter("text_color")
    fun setSelectedTextColor(view: TextView, form: Form?) {
        val txtColor = getHexColor(form?.background_color) ?: convertRgbToHex("55", "55", "55")
        view.setTextColor(baseMethod.parseColor(txtColor))

    }

    @BindingAdapter("app:formBack")
    @JvmStatic
    fun formBack(view: ImageView, form: Form?) {
        form?.let {
            val backgroundImage = it.background_image
            val backgroundColor = it.background_color

            if (backgroundImage != null && backgroundImage.isNotEmpty()) {
                getUrlWithoutParameters(backgroundImage)?.let {link->
                Picasso.get().load(link).into(view)}

            } else if (backgroundColor?.isNotEmpty() == true) {
                getHexColor(backgroundColor)?.let {color->
                    view.setBackgroundColor(baseMethod.parseColor(color))

                }
            }else{

            }
        }


    }

    @JvmStatic
    @BindingAdapter("app:field_background")
    fun fieldViewBackground(view: View, form: Form?) {
        val shapedrawable = GradientDrawable()
        val errdrawable = GradientDrawable()

        form?.field_color?.let {
            val fieldColor = getHexColor(it) ?: convertRgbToHex("242", "242", "242")
            shapedrawable.setColor(baseMethod.parseColor(fieldColor))

        }
        form?.border_color?.let {
            val borderColor = getHexColor(it) ?: convertRgbToHex("255", "255", "255")
            shapedrawable.setStroke(strokeWidth, baseMethod.parseColor(borderColor))

        }


        shapedrawable.cornerRadius = cornerRadius

        errdrawable.setColor(baseMethod.parseColor("#1BFB9B9B"))
        errdrawable.setStroke(strokeWidth, baseMethod.parseColor("#F43A3B"))

        view.background = shapedrawable


    }

    @JvmStatic
    @BindingAdapter("field_background")
    fun fieldBackground(view: View, form: Form?) {
        val shapedrawable = GradientDrawable()
        val errdrawable = GradientDrawable()

        form?.field_color?.let {
            val fieldColor = getHexColor(it) ?: convertRgbToHex("242", "242", "242")
            shapedrawable.setColor(baseMethod.parseColor(fieldColor))

        }
        form?.border_color?.let {
            val borderColor = getHexColor(it) ?: convertRgbToHex("255", "255", "255")
            shapedrawable.setStroke(strokeWidth, baseMethod.parseColor(borderColor))

        }


        shapedrawable.cornerRadius = cornerRadius

        errdrawable.setColor(baseMethod.parseColor("#1BFB9B9B"))
        errdrawable.setStroke(strokeWidth, baseMethod.parseColor("#F43A3B"))

        view.background = shapedrawable


    }

    @JvmStatic
    @BindingAdapter("reverse_field_background")
    fun reverseFieldBackground(view: View, form: Form?) {
        val shapedrawable = GradientDrawable()
        val errdrawable = GradientDrawable()

        form?.text_color?.let {
            val fieldColor = getHexColor(it) ?: convertRgbToHex("242", "242", "242")
            shapedrawable.setColor(baseMethod.parseColor(fieldColor))

        }

        shapedrawable.cornerRadius = cornerRadius

        errdrawable.setColor(baseMethod.parseColor("#1BFB9B9B"))
        errdrawable.setStroke(strokeWidth, baseMethod.parseColor("#F43A3B"))

        view.background = shapedrawable


    }

    @JvmStatic
    @BindingAdapter("field_background")
    fun fieldBackgroundShape(view: View?, form: Form?) {
        val shapedrawable = GradientDrawable()

        val fieldColor = getHexColor(form?.field_color) ?: convertRgbToHex("242", "242", "242")
        shapedrawable.setColor(baseMethod.parseColor(fieldColor))

        val borderColor = getHexColor(form?.border_color) ?: convertRgbToHex("255", "255", "255")
        shapedrawable.setStroke(strokeWidth, baseMethod.parseColor(borderColor))

        shapedrawable.cornerRadius = cornerRadius

        view?.background = shapedrawable

    }

    @JvmStatic
    @BindingAdapter("field_background", "field_has_err")
    fun fieldBackground(view: View, form: Form?, hasErr: Boolean?) {
        val shapedrawable = GradientDrawable()
        val errdrawable = GradientDrawable()

        form?.field_color?.let {
            val fieldColor = getHexColor(it) ?: convertRgbToHex("242", "242", "242")
            shapedrawable.setColor(baseMethod.parseColor(fieldColor))

        }
        form?.border_color?.let {
            val borderColor = getHexColor(it) ?: convertRgbToHex("255", "255", "255")
            shapedrawable.setStroke(strokeWidth, baseMethod.parseColor(borderColor))

        }


        shapedrawable.cornerRadius = cornerRadius

        errdrawable.setColor(baseMethod.parseColor("#1BFB9B9B"))
        errdrawable.setStroke(strokeWidth, baseMethod.parseColor("#F43A3B"))

        if (hasErr == true) {
            view.background = errdrawable

        } else {
            view.background = shapedrawable
        }


    }


}
