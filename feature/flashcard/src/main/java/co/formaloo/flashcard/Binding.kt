package co.formaloo.flashcard

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.text.Html
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatRatingBar
import androidx.appcompat.widget.AppCompatSpinner
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import co.formaloo.flashcard.lesson.adapter.LessonFieldsAdapter
import co.formaloo.flashcard.lesson.adapter.holder.DropDownItemsAdapter
import com.bumptech.glide.Glide
import com.google.android.material.progressindicator.LinearProgressIndicator
import org.koin.core.component.KoinComponent
import timber.log.Timber
import java.net.URI
import java.net.URISyntaxException
import java.util.*


object Binding : KoinComponent {


    @BindingAdapter("app:formBack")
    @JvmStatic
    fun loadFormBack(view: ImageView, form: co.formaloo.model.form.Form?) {
        val backgroundImage = form?.background_image
        val backgroundColor = form?.background_color

        if (backgroundImage?.isNotEmpty() == true) {
            Glide.with(view.context).load(getUrlWithoutParameters(backgroundImage)).into(view)

        } else if (backgroundColor?.isNotEmpty() == true) {
            getHexColor(backgroundColor)?.let { txtColor ->
                view.setColorFilter(Color.parseColor(txtColor))
            }
        }

    }

    @Throws(URISyntaxException::class)
    private fun getUrlWithoutParameters(url: String): String {
        val uri = URI(url)
        return URI(
            uri.scheme,
            uri.authority,
            uri.path,
            null,  // Ignore the query part of the input url
            uri.fragment
        ).toString()
    }

    @BindingAdapter("app:htmlSimpeTxt")
    @JvmStatic
    fun setSimpleHtmlTxt(txv: TextView, txt: String?) {
        txt?.let {
            txv.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(txt, Html.FROM_HTML_MODE_LEGACY)
            } else {
                Html.fromHtml(txt)
            }
        }

    }

    @BindingAdapter("app:loadData")
    @JvmStatic
    fun loadData(webView: WebView, txt: String?) {
        webView.setBackgroundColor(Color.TRANSPARENT)
        txt?.let {
            webView.loadData(txt, "text/html", "UTF-8");

        }

    }

    @BindingAdapter("app:movementMethod")
    @JvmStatic
    fun setMovementMethod(txv: TextView, status: Boolean?) {
        txv.movementMethod = LinkMovementMethod.getInstance()

    }

    @JvmStatic
    @BindingAdapter("app:progressTint")
    fun progressTint(view: LinearProgressIndicator, color: String?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.progressTintList = getColorStateList(color)
        }
    }

    @JvmStatic
    @BindingAdapter("app:progressTint")
    fun progressTint(view: AppCompatRatingBar, color: String?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.progressTintList = getColorStateList(color)
        }
    }

    @JvmStatic
    @BindingAdapter("app:backgroundTintList")
    fun backgroundTintList(view: View, color: String?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.backgroundTintList = getColorStateList(color)
        }
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


    @BindingAdapter("app:items")
    @JvmStatic
    fun setItems(
        spinner: AppCompatSpinner,
        items: ArrayList<co.formaloo.model.form.ChoiceItem>
    ) {
        Timber.d("items ${items.size}")
        if (spinner.adapter is DropDownItemsAdapter)
            with(spinner.adapter as DropDownItemsAdapter) {
                listItemsTxt = items
                (spinner.adapter as DropDownItemsAdapter).notifyDataSetChanged()

            }

    }

//    @BindingAdapter("app:items")
//    @JvmStatic
//    fun setFormItems(recyclerView: RecyclerView, resource: ArrayList<HashMap<Int, co.idearun.model.form.Form>>?) {
//        if (recyclerView.adapter is co.idearun.home.SortedLessonListAdapter)
//            with(recyclerView.adapter as co.idearun.home.SortedLessonListAdapter) {
//                resource?.let {
//                    collection = it
//                }
//            }
//    }

    @BindingAdapter("app:items")
    @JvmStatic
    fun setFieldItems(recyclerView: RecyclerView, resource: co.formaloo.model.form.Form?) {
        if (recyclerView.adapter is LessonFieldsAdapter)
            with(recyclerView.adapter as LessonFieldsAdapter) {
                resource?.fields_list?.let {
                    val titleField = it.find {
                        it.slug == "form_title_logo"
                    }
                    if (titleField==null){
                        val firstField = co.formaloo.model.form.Fields("form_title_logo")
                        firstField.title = resource.title
                        firstField.description = resource.description
                        firstField.logo = resource.logo
                        firstField.type = "meta"
                        firstField.sub_type = "section"
                        it.add(0, firstField)

                    }else{

                    }



                    collection = it
                }
            }
    }


    fun getHexHashtagColorFromRgbStr(color: String?): String? {
        return try {
            when {
                color != null -> {
                    val rgbToInt = getIntColorFromRgbStr(color)
                    when {
                        rgbToInt != null -> {
                            return convertIntColorToHashtagHex(rgbToInt)
                        }

                        else -> {
                            null
                        }
                    }
                }

                else -> {
                    null
                }
            }
        } catch (e: Exception) {
            Log.e("Binding.TAG", "getHexColor: $e")
            null
        }

    }

    fun getIntColorFromRgbStr(color_: String?): Int? {
        return try {
            var color = color_
            if (color != null) {
                color = color.replace("{\"", "")
                color = color.replace("\"", "")

                val a = color.substring(color.indexOf("a:") + 2, color.indexOf("}"))
                val r = color.substring(color.indexOf("r:") + 2, color.indexOf(",g"))
                val g = color.substring(color.indexOf("g:") + 2, color.indexOf(",b"))
                val b = color.substring(color.indexOf("b:") + 2, color.indexOf(",a"))

                return convertRgbToInt(r, g, b)


            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("Binding.TAG", "getHexColor: $e")
            null
        }

    }

    fun convertIntColorToHex(color: Int): String {
        return Integer.toHexString(color)
    }

    fun convertIntColorToHashtagHex(color: Int): String {
        return "#${convertIntColorToHex(color)}"
    }

    fun convertRgbToInt(r: String, g: String, b: String): Int {
        return Color.rgb(Integer.parseInt(r), Integer.parseInt(g), Integer.parseInt(b))
    }

    fun getColorStateList(color: String?): ColorStateList? {
        getHexHashtagColorFromRgbStr(color)?.let {
            return ColorStateList(
                arrayOf(
                    intArrayOf(-android.R.attr.state_enabled),
                    intArrayOf(android.R.attr.state_enabled)
                ), intArrayOf(
                    Color.parseColor(it) //disabled
                    , Color.parseColor(it) //enabled
                )
            )

        }
        return null
    }

}
