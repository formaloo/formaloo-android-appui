package co.formaloo.common

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Color.parseColor
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.text.Html
import android.text.InputType
import android.util.Base64
import android.view.View
import android.webkit.URLUtil
import android.widget.*
import androidx.annotation.ColorInt
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatButton
import androidx.core.widget.addTextChangedListener
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
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
import kotlin.math.roundToInt

object MainBinding {
    const val cornerRadius = 9f
    const val strokeWidth = 2



    @BindingAdapter("app:imageUrlRounded")
    @JvmStatic
    fun loadImageRounded(view: ImageView, url: String?) {
        url?.let {
            val link = getUrlWithoutParameters(url)
            Glide.with(view.context).load(link).apply(RequestOptions.circleCropTransform())
                .into(view)

        }
    }

    @BindingAdapter("app:imageUrlRounded")
    @JvmStatic
    fun loadImageRounded(view: ImageView, url: Int?) {
        url?.let {
            Glide.with(view.context).load(url).apply(RequestOptions.circleCropTransform())
                .into(view)

        }
    }

    @BindingAdapter("app:imageUrl")
    @JvmStatic
    fun loadImage(view: ImageView, url: String?) {
        Timber.e("loadImage $url")

        if (url != null && url.isNotEmpty()) {

            Picasso.get().load(url).into(view)

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

    @BindingAdapter("app:imageUrl")
    @JvmStatic
    fun loadImage(view: ImageView, url: Int?) {
        Timber.e("loadImage $url")
        Glide.with(view.context).load(url).into(view)
    }



    @SuppressLint("UseCompatLoadingForDrawables")
    @BindingAdapter("app:htmlTxt")
    @JvmStatic
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


    @JvmStatic
    @BindingAdapter("nps_background")
    fun npsBackgroundShape(view: View, color: String?) {
        val shapedrawable = GradientDrawable()

        val fieldColor = getHexColor(color) ?: convertRgbToHex("242", "242", "242")
        shapedrawable.setColor(parseColor(fieldColor))

        view.background = shapedrawable

    }

    @JvmStatic
    @BindingAdapter("textCursorDrawable")
    fun textCursorDrawable(view: TextInputEditText, color: String?) {
        val txtColor = getHexColor(color) ?: convertRgbToHex("242", "242", "242")
        setCursorColor(view, parseColor(txtColor))
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

    @JvmStatic
    @BindingAdapter("app:text_color")
    fun setTextColor(view: TextInputEditText, color: String?) {
        val txtColor = getHexColor(color) ?: convertRgbToHex("55", "55", "55")
        view.setTextColor(parseColor(txtColor))

    }

    @JvmStatic
    @BindingAdapter("btn_background")
    fun btnBackgroundShape(view: View, color: String?) {
        val shapedrawable = GradientDrawable()

        val fieldColor = getHexColor(color) ?: convertRgbToHex("242", "242", "242")
        shapedrawable.setColor(parseColor(fieldColor))

        shapedrawable.cornerRadius = cornerRadius

        view.background = shapedrawable

    }

    @BindingAdapter("app:imageColorRounded")
    @JvmStatic
    fun loadImageColorRounded(view: ImageView, color: String?) {
        val btnColor = if (color != null && color.isNotEmpty()) {
            getHexColor(color) ?: convertRgbToHex("55", "55", "55")
        } else {
            convertRgbToHex("55", "55", "55")
        }
        view.setImageDrawable(ColorDrawable(parseColor(btnColor)))

    }


    @JvmStatic
    @BindingAdapter("text_color")
    fun setTextColor(view: TextView, color: String?) {
        val txtColor = getHexColor(color) ?: convertRgbToHex("55", "55", "55")
        view.setTextColor(parseColor(txtColor))

    }

    fun getDateOnLocale(time: String): String? {
        return convertStringToDate(time)


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


    @JvmStatic
    @BindingAdapter("edt_inputType")
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

    @JvmStatic
    @BindingAdapter("app:imageTintList")
    fun imageTintList(view: ImageButton, color: String?) {
        view.imageTintList = getColorStateList(color)
    }    @JvmStatic
    @BindingAdapter("app:imageTintList")
    fun imageTintList(view: ImageView, color: String?) {
        view.imageTintList = getColorStateList(color)
    }

    @ColorInt
    fun darkenColor(@ColorInt color: Int): Int {
        return Color.HSVToColor(FloatArray(3).apply {
            Color.colorToHSV(color, this)
            this[2] *= 0.8f
        })
    }


    @JvmStatic
    @BindingAdapter("divider_background")
    fun divider_background(view: View, color: String?) {
        val txtColor = getHexColor(color) ?: convertRgbToHex("55", "55", "55")
        view.setBackgroundColor(parseColor(txtColor))

    }

    @JvmStatic
    @BindingAdapter("text_color")
    fun setTextColor(view: AppCompatButton, color: String?) {
        val txtColor = getHexColor(color) ?: convertRgbToHex("55", "55", "55")
        view.setTextColor(parseColor(txtColor))

    }

    @JvmStatic
    @BindingAdapter("edt_background")
    fun fieldBackground(view: TextInputEditText, color: String?) {
        val backColor = getHexColor(color) ?: convertRgbToHex("242", "242", "242")
        view.setBackgroundColor(parseColor(backColor))
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

    @BindingAdapter("app:numberTxt")
    @JvmStatic
    fun numberTxt(txv: TextView, txt: Int?) {
        txv.text = "0"
        formatter(txt)?.let {
            txv.text = it
        }

    }

    @BindingAdapter("app:setPriceTxt")
    @JvmStatic
    fun setpricetxt(view: TextView, price: String?) {
        if (price != null && price != "null" && price.isNotEmpty()) {
            val roundToInt = price.toDouble().roundToInt()
            val formattedPrice = DecimalFormat("##,###,###").format(roundToInt)

            view.text = "$formattedPrice"

        } else {

        }
    }

    @BindingAdapter("app:setTxt")
    @JvmStatic
    fun setpricetxt(view: EditText, txt: Any?) {

        view.setText("$txt")
    }

    @BindingAdapter("app:errorText")
    @JvmStatic
    fun setErrorMessage(view: TextInputLayout, errorMessage: String?) {
        view.error = errorMessage
        view.editText?.addTextChangedListener {
            it?.let {
                view.error = ""
            }
        }
    }

    @BindingAdapter("app:imageCustomUrl")
    @JvmStatic
    fun loadCustomImageUrl(view: ImageView, url: String?) {
        if (url != null && url.isNotEmpty()) {
            val link = getUrlWithoutParameters(url)
            Picasso.get().load(link).into(view)

        }

    }

    @BindingAdapter("app:choiceImage")
    @JvmStatic
    fun choiceImage(view: ImageView, url: String?) {
        view.setImageResource(R.drawable.ic_pic)
        url?.let {
            when {
                URLUtil.isValidUrl(url) -> {
                    Picasso.get().load(url).into(view)
                }
                url.contains(".jpg") -> {
                    Picasso.get().load(url).into(view)
                }
                else -> {
                    try {
                        val decodedString = Base64.decode(url, Base64.DEFAULT)
                        view.setImageBitmap(
                            BitmapFactory.decodeByteArray(
                                decodedString,
                                0,
                                decodedString.size
                            )
                        )
                    } catch (e: Exception) {

                    }
                }
            }

        }


    }


}
