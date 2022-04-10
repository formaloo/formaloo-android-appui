package co.formaloo.common

import android.R
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.app.NotificationManager
import android.content.*
import android.content.ClipboardManager
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.content.res.Resources
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.media.MediaScannerConnection
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.ContactsContract
import android.provider.MediaStore
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Base64
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.FileProvider
import androidx.core.widget.doAfterTextChanged
import androidx.loader.content.CursorLoader
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.net.URI
import java.net.URISyntaxException
import java.net.URLEncoder
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

const val TAG = "METHOD"

fun openSelectDialog(
    msgTxt: String,
    okTxt: String,
    calcelTxt: String,
    listener: DialogListener,context: Context
) {
    AlertDialog.Builder(context).setTitle(msgTxt)
//            .setMessage(msgTxt)
        .setPositiveButton(okTxt) { _, _ ->
            listener.performOkButton()
        }

        .setNegativeButton(calcelTxt, null).show()

}

fun getColors(): ArrayList<String> {
    return arrayListOf(
        "3394CC", "61BD4F", "FFB248", "3BC0D5",
        "51E898", "FF78CB", "0065DC", "727272",
        "D934A8", "EF7B6B", "C377E0", "F2D600"
    )
}

fun getDeviceName(): String? {
    val manufacturer = Build.MANUFACTURER
    val model = Build.MODEL
    return if (model.toLowerCase().startsWith(manufacturer.toLowerCase())) {
        capitalize(model)
    } else {
        capitalize(manufacturer) + " " + model
    }
}


fun capitalize(s: String?): String {
    if (s == null || s.length == 0) {
        return ""
    }
    val first = s[0]
    return if (Character.isUpperCase(first)) {
        s
    } else {
        Character.toUpperCase(first).toString() + s.substring(1)
    }
}

fun getScreenWidth(): Int {
    return Resources.getSystem().displayMetrics.widthPixels
}

/**
 * This method converts dp unit to equivalent pixels, depending on device density.
 *
 * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
 * @param context Context to get resources and device specific display metrics
 * @return A float value to represent px equivalent to dp depending on device density
 */
fun convertDpToPixel(dp: Float, context: Context): Float {
    return dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

/**
 * This method converts device specific pixels to density independent pixels.
 *
 * @param px A value in px (pixels) unit. Which we need to convert into db
 * @param context Context to get resources and device specific display metrics
 * @return A float value to represent dp equivalent to px value
 */
fun convertPixelsToDp(px: Float): Float {

    return px / (Resources.getSystem().displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

fun getColorStateList(color: String?): ColorStateList? {
    getHexHashtagColorFromRgbStr(color)?.let {
        return ColorStateList(
            arrayOf(
                intArrayOf(-R.attr.state_enabled),
                intArrayOf(R.attr.state_enabled)
            ), intArrayOf(
                parseColor(it) //disabled
                , parseColor(it) //enabled
            )
        )

    }
    return null
}


fun TextView.makeLinks(vararg links: Pair<String, View.OnClickListener>) {
    val spannableString = SpannableString(this.text)
    var startIndexOfLink = -1
    for (link in links) {
        val clickableSpan = object : ClickableSpan() {
            override fun updateDrawState(textPaint: TextPaint) {
                // use this to change the link color
                textPaint.color = textPaint.linkColor
                // toggle below value to enable/disable
                // the underline shown below the clickable text
                textPaint.isUnderlineText = true
            }

            override fun onClick(view: View) {
                Selection.setSelection((view as TextView).text as Spannable, 0)
                view.invalidate()
                link.second.onClick(view)
            }
        }
        startIndexOfLink = this.text.toString().indexOf(link.first, startIndexOfLink + 1)
//      if(startIndexOfLink == -1) continue // todo if you want to verify your texts contains links text
        spannableString.setSpan(
            clickableSpan, startIndexOfLink, startIndexOfLink + link.first.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    this.movementMethod =
        LinkMovementMethod.getInstance() // without LinkMovementMethod, link can not click
    this.setText(spannableString, TextView.BufferType.SPANNABLE)
}

fun changeBackGroundWithAnimation(colorFrom: Int, colorTo: Int, view: View) {
    val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
    colorAnimation.duration = 250 // milliseconds

    colorAnimation.addUpdateListener { animator ->
        view.setBackgroundColor(animator.animatedValue as Int)
    }
    colorAnimation.start()
}

fun contryCode(context: Context): String {
    val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        context.resources.configuration.locales[0]
    } else {
        context.resources.configuration.locale
    }
    return locale.country

}

fun contryName(context: Context): String {
    val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        context.resources.configuration.locales[0]
    } else {
        context.resources.configuration.locale
    }
    return locale.displayCountry

}

fun contryISO(context: Context): String {
    val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        context.resources.configuration.locales[0]
    } else {
        context.resources.configuration.locale
    }
    return locale.isO3Country

}

fun rotateView(view: View?, deg: Float, duration: Long) {
    val rotate: ObjectAnimator = ObjectAnimator.ofFloat(view, "rotation", 0f, deg)
    //        rotate.setRepeatCount(10);
    rotate.setDuration(duration)
    rotate.start()
}

@SuppressLint("Range")
fun getContactList(context: Context) {
    val cr: ContentResolver = context.contentResolver
    val cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)
    if (cur?.count ?: 0 > 0) {
        while (cur != null && cur.moveToNext()) {
            val id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID))
            val name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
            if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                val pCur = cr.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                    arrayOf(id), null
                )
                while (pCur!!.moveToNext()) {
                    val phoneNo =
                        pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    Log.i(TAG, "Name: $name")
                    Log.i(TAG, "Phone Number: $phoneNo")
                }
                pCur.close()
            }
        }
    }
    cur?.close()
}


fun getActionBarView(window: Window, context: Context): View? {
    val v = window.decorView
    val resId: Int = context.resources.getIdentifier("action_bar_container", "id", "android")
    return v.findViewById(resId)
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
        Log.e(TAG, "getHexColor: $e")
        null
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
        Log.e(TAG, "getHexColor: $e")
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

public fun setLocale(context: Context, langCode: String) {
    val locale = Locale(langCode);
    val config = Configuration(context.resources.configuration);
    Locale.setDefault(locale)
    config.setLocale(locale)
//        context.createConfigurationContext(config)
    context.resources.updateConfiguration(config, context.resources.displayMetrics)
}

fun hideNotificationBar(activity: Activity) {
    //Remove notification bar
    activity.getWindow().setFlags(
        WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN
    );

}

fun openLogo(supportActionBar: ActionBar?, logo: Int) {
    supportActionBar?.setLogo(logo)
    supportActionBar?.setDisplayUseLogoEnabled(true)
}

fun hideLogo(supportActionBar: ActionBar?) {
    supportActionBar?.setDisplayUseLogoEnabled(false)
    supportActionBar?.setLogo(null)

}

fun hideABTitle(supportActionBar: ActionBar?) {
    supportActionBar?.setDisplayShowTitleEnabled(false)

}

fun showABTitle(supportActionBar: ActionBar?) {
    supportActionBar?.setDisplayShowTitleEnabled(true)

}

fun showBackBtn(supportActionBar: ActionBar?) {
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    supportActionBar?.setDisplayShowHomeEnabled(true)

}

fun showBackBtn(supportActionBar: android.app.ActionBar?) {
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    supportActionBar?.setDisplayShowHomeEnabled(true)

}

fun hideBackBtn(supportActionBar: ActionBar?) {
    supportActionBar?.setDisplayHomeAsUpEnabled(false)
    supportActionBar?.setDisplayShowHomeEnabled(false)

}

fun hideBackBtn(supportActionBar: android.app.ActionBar?) {
    supportActionBar?.setDisplayHomeAsUpEnabled(false)
    supportActionBar?.setDisplayShowHomeEnabled(false)

}

fun hideAB(supportActionBar: ActionBar?) {
    supportActionBar?.hide()

}

fun showAB(supportActionBar: ActionBar?) {
    supportActionBar?.show()

}

fun setTitle(supportActionBar: ActionBar?, title: Int) {
    supportActionBar?.setTitle(title)
    supportActionBar?.setDisplayShowTitleEnabled(true)

}

fun setTitle(supportActionBar: ActionBar?, title: String) {
    supportActionBar?.setTitle(title)
    supportActionBar?.setDisplayShowTitleEnabled(true)

}

fun loadImageFromFile(drawable: String?, imageView: ImageView) {
    if (drawable != null)
        Picasso.get()
            .load(File(drawable))
//                .resize(50, 50)
//                .centerCrop()

            .into(imageView)

}


fun getDateOnLocale(time: String): String? {
    return convertStringToDate(time)


}


@Throws(URISyntaxException::class)
 fun getUrlWithoutParameters(thePath: String): String? {
    var str:String?=null
    try {
        val url = URLEncoder.encode(thePath, "UTF-8")

        val uri = URI(url)
        str = URI(
            uri.scheme,
            uri.authority,
            uri.path,
            null,  // Ignore the query part of the input url
            uri.fragment
        ).toString()
    }catch (e:Exception){

    }


    return str
}
fun loadImageRounded(view: ImageView, url: String?) {
    url?.let {
        val link = getUrlWithoutParameters(url)
        Glide.with(view.context).load(link).apply(RequestOptions.circleCropTransform())
            .into(view)

    }
}

fun loadImage(drawable: String?, imageView: ImageView) {
    if (drawable != null) {
        getUrlWithoutParameters(drawable)?.let {link->


        try {
            Picasso.get()
                .load(link)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(imageView, object : com.squareup.picasso.Callback {

                    override fun onSuccess() {

                    }

                    override fun onError(e: java.lang.Exception?) {
                        Picasso.get()
                            .load(drawable)
                            .into(imageView)
                    }
                })
        } catch (e: Exception) {
            Log.e("loadImage", "Exception $e")

        }
    }
    }
}

fun loadImage(drawable: String?, window: Window, hexColor: String?) {
    if (drawable != null) {
        getUrlWithoutParameters(drawable)?.let { link ->

            try {
                Picasso.get().load(link).into(object : Target {
                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                        val bitmapDrawable = BitmapDrawable(window.context.resources, bitmap)
                        bitmapDrawable.gravity = Gravity.FILL_VERTICAL
                        window.setBackgroundDrawable(bitmapDrawable)
                    }

                    override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {
                        Log.e(TAG, "onBitmapFailed: $e")
                        hexColor?.let {
                            window.setBackgroundDrawable(ColorDrawable(parseColor(it)))

                        }
                    }

                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                        hexColor?.let {
                            window.setBackgroundDrawable(ColorDrawable(parseColor(it)))

                        }
                    }

                })
            } catch (e: Exception) {
                Log.e("loadImage", "Exception $e")

            }
        }
    }
}


fun isProbablyArabic(s: String): Boolean {
    try {
        var j = 0
        for (i in j..s.length) {
            val c = s.codePointAt(i)
            if (c in 0x0600..0x06E0) {
                return true

            }
            j += Character.charCount(c)


        }
    } catch (e: Exception) {

    }
    return false

}

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }
    })
}

fun showMsg(v: View, msg: String) {
    val snack = Snackbar.make(v, msg, Snackbar.LENGTH_LONG)
    val view = snack.getView()

    val params = FrameLayout.LayoutParams(view.layoutParams)
    params.gravity = Gravity.TOP
    params.gravity = Gravity.CENTER_HORIZONTAL

    params.width = ViewGroup.LayoutParams.MATCH_PARENT

    view.setLayoutParams(params)

    val snackbarView = snack.getView()

    val snackTextView = snackbarView.findViewById<View>(co.formaloo.common.R.id.snackbar_text)

    snackTextView.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY)
    snack.show()
}


@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
fun showMsgCustom(v: View, msg: String) {
    //        Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).show();
    val snack = Snackbar.make(v, msg, Snackbar.LENGTH_LONG)
    val view = snack.getView()

    val params = view.getLayoutParams() as FrameLayout.LayoutParams
    params.gravity = Gravity.TOP
    params.gravity = Gravity.CENTER_HORIZONTAL

    params.width = ViewGroup.LayoutParams.MATCH_PARENT
//        view.setBackground(context.resources.getDrawable(R.color.black_warm))
    //        view.setBackgroundColor(context.getResources().getColor(R.color.green));
    //        params.setMargins(0, 100, 0, 0);
    view.setLayoutParams(params)

    val snackbarView = snack.getView()

    val snackTextView = snackbarView.findViewById<View>(co.formaloo.common.R.id.snackbar_text)

    snackTextView.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY)
//        snackTextView.foregroundGravity(View.FOCUS_RIGHT)

    //        snack.setAction("بستن", new View.OnClickListener() {
    //            @Override
    //            public void onClick(View v) {
    //                snack.dismiss();
    //            }
    //        });


    snack.show()

}

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
fun showMsgCustom(v: View, msg: Int) {
    //        Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).show();
    val snack = Snackbar.make(v, msg, Snackbar.LENGTH_LONG)
    val view = snack.getView()

    val params = view.getLayoutParams() as FrameLayout.LayoutParams
    params.gravity = Gravity.TOP
    params.gravity = Gravity.CENTER_HORIZONTAL

    params.width = ViewGroup.LayoutParams.MATCH_PARENT
//        view.setBackground(context.resources.getDrawable(R.color.black_warm))
    //        view.setBackgroundColor(context.getResources().getColor(R.color.green));
    //        params.setMargins(0, 100, 0, 0);
    view.setLayoutParams(params)

    val snackbarView = snack.getView()

    val snackTextView = snackbarView.findViewById<View>(co.formaloo.common.R.id.snackbar_text)

    snackTextView.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY)
//        snackTextView.foregroundGravity(View.FOCUS_RIGHT)

    //        snack.setAction("بستن", new View.OnClickListener() {
    //            @Override
    //            public void onClick(View v) {
    //                snack.dismiss();
    //            }
    //        });


    snack.show()

}


fun showInternetMsg(v: View, msg: String, context: Context) {
    //        Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).show();
    val snack = Snackbar.make(v, msg, Snackbar.LENGTH_LONG)
    val view = snack.getView()

    val params = view.getLayoutParams() as FrameLayout.LayoutParams
    params.gravity = Gravity.TOP
    params.gravity = Gravity.CENTER_HORIZONTAL

    params.width = ViewGroup.LayoutParams.MATCH_PARENT
//        view.setBackground(context.resources.getDrawable(R.color.black_warm))
    //        view.setBackgroundColor(context.getResources().getColor(R.color.green));
    //        params.setMargins(0, 100, 0, 0);
    view.setLayoutParams(params)

    val snackbarView = snack.getView()

    val snackTextView = snackbarView.findViewById<View>(co.formaloo.common.R.id.snackbar_text)

    snackTextView.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY)
//        snackTextView.foregroundGravity(View.FOCUS_RIGHT)

//        snack.setAction(mContext.getString(R.string.try_again)) {
//            snack.dismiss();
//        }
//

    snack.show()

}

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
fun showMsgCustom(v: View, msg: Int, context: Context) {
    //        Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).show();
    val snack = Snackbar.make(v, msg, Snackbar.LENGTH_LONG)
    val view = snack.getView()

    val params = view.getLayoutParams() as FrameLayout.LayoutParams
    params.gravity = Gravity.BOTTOM
    params.gravity = Gravity.CENTER_HORIZONTAL

    params.width = ViewGroup.LayoutParams.MATCH_PARENT
//        view.setBackground(context.resources.getDrawable(R.color.black_warm))
//        view.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
//                params.setMargins(0, 0, 0, 100);
    view.setLayoutParams(params)

    val snackbarView = snack.getView()
    val snackTextView = snackbarView.findViewById<View>(co.formaloo.common.R.id.snackbar_text)

    snackTextView.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY)
//        snackTextView.setGravity(View.FOCUS_RIGHT)

    //        snack.setAction("بستن", new View.OnClickListener() {
    //            @Override
    //            public void onClick(View v) {
    //                snack.dismiss();
    //            }
    //        });


    snack.show()

}


fun onTouch(view: View, event: MotionEvent, dX: Float, dY: Float): Boolean {
    var dX = dX
    var dY = dY

    when (event.action) {

        MotionEvent.ACTION_DOWN -> {

            dX = view.x - event.rawX
            dY = view.y - event.rawY
        }

        MotionEvent.ACTION_MOVE ->

            view.animate()
                .x(event.rawX + dX)
                .y(event.rawY + dY)
                .setDuration(0)
                .start()
        else -> return false
    }
    return true
}

fun showKeyboard(activity: Activity) {
    val inputMethodManager =
        activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
}

fun showKeyboard(editText: EditText) {
    val inputMethodManager =
        editText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
}

fun hideKeyboard(activity: Activity) {
    val view = activity.findViewById<View>(android.R.id.content)
    if (view != null) {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        assert(imm != null)
        imm!!.hideSoftInputFromWindow(view!!.windowToken, 0)
    }
}

fun openCameraIntent(activity: Activity): File? {
    var photoFile: File? = null

    val pictureIntent = Intent(
        MediaStore.ACTION_IMAGE_CAPTURE
    )
    photoFile = getOutputMediaFile(1, activity)

    photoFile?.let {

        val fileProvider =
            FileProvider.getUriForFile(activity, "co.idearun.formaloo.provider", it)
        pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

        activity.startActivityForResult(pictureIntent, Constants.REQUEST_CAPTURE_IMAGE)
    }

    return photoFile

}

fun openCameraIntent(activity: AppCompatActivity, authority: String): File? {
    var photoFile: File? = null

    val pictureIntent = Intent(
        MediaStore.ACTION_IMAGE_CAPTURE
    )
//        if (pictureIntent.resolveActivity(mContext.packageManager) != null) {
    //Create a file to store the image
    photoFile = getOutputMediaFile(1, activity)

    //            imageFilePath = photoFile.getAbsolutePath();
    //
    //            picUri = Uri.fromFile(photoFile);
    photoFile?.let {

        val fileProvider = FileProvider.getUriForFile(activity, authority, it)
        pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

        activity.startActivityForResult(pictureIntent, Constants.REQUEST_CAPTURE_IMAGE)
    }
//        }

    return photoFile

}

fun openCameraIntentInvoice(activity: AppCompatActivity): File? {
    var photoFile: File? = null

    val pictureIntent = Intent(
        MediaStore.ACTION_IMAGE_CAPTURE
    )
//        if (pictureIntent.resolveActivity(mContext.packageManager) != null) {
    //Create a file to store the image
    photoFile = getOutputMediaFile(1, activity)

    //            imageFilePath = photoFile.getAbsolutePath();
    //
    //            picUri = Uri.fromFile(photoFile);
    photoFile?.let {

        val fileProvider =
            FileProvider.getUriForFile(activity, "co.idearun.invoicehome.provider", it)
        pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

        activity.startActivityForResult(pictureIntent, Constants.REQUEST_CAPTURE_IMAGE)
    }
//        }

    return photoFile

}


fun openFilePickerInten(activity: Activity, type: String, action: String) {

    val chooseFile = Intent(action)
    chooseFile.addCategory(Intent.CATEGORY_OPENABLE)
    chooseFile.type = type
    activity.startActivityForResult(
        Intent.createChooser(chooseFile, ""),
        Constants.PICKFILE_RESULT_CODE
    )

}

fun openGalleryInten(activity: AppCompatActivity) {
    val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    pickPhoto.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
    pickPhoto.action = Intent.ACTION_GET_CONTENT
    activity.startActivityForResult(
        pickPhoto,
        Constants.REQUEST_GALLREY
    )//one can be replaced with any action code

}

fun getPathFromUri(uri: Uri, context: Context): String? {
    var result: String? = null
    val column_index: Int

    val projection = arrayOf(MediaStore.Images.Media.DATA)

    val cursorLoader = CursorLoader(context, uri, projection, null, null, null)

    val cursor = cursorLoader.loadInBackground()

    if (cursor != null) {
        column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        result = cursor.getString(column_index)
        cursor.close()
    }


    return result
}

fun getPath(context: Context, uri: Uri): String? {
    if ("content".equals(uri.scheme, ignoreCase = true)) {
        val projection = arrayOf("_data")
        var cursor: Cursor? = null
        try {
            cursor = context.contentResolver.query(uri, projection, null, null, null)
            cursor?.let {
                val column_index: Int = cursor.getColumnIndexOrThrow("_data")
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index)
                }
            }

        } catch (e: java.lang.Exception) {
            // Eat it
        }
    } else if ("file".equals(uri.scheme, ignoreCase = true)) {
        return uri.path
    }
    return null
}

fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
    val bytes = ByteArrayOutputStream()
    inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
    val path =
        MediaStore.Images.Media.insertImage(
            inContext.contentResolver,
            inImage,
            "Title${Random().nextInt()}",
            null
        )

    return Uri.parse(path)
}


@Throws(FileNotFoundException::class)
fun decodeUri(c: Context, uri: Uri?, requiredSize: Int): Bitmap? {
    val o = BitmapFactory.Options()
    o.inJustDecodeBounds = true

    uri?.let {
        BitmapFactory.decodeStream(c.contentResolver.openInputStream(uri), null, o)

    }

    var width_tmp = o.outWidth
    var height_tmp = o.outHeight
    var scale = 1

    while (true) {
        if (width_tmp / 2 < requiredSize || height_tmp / 2 < requiredSize)
            break
        width_tmp /= 2
        height_tmp /= 2
        scale *= 2
    }

    val o2 = BitmapFactory.Options()
    o2.inSampleSize = scale

    return uri?.let {
        BitmapFactory.decodeStream(c.contentResolver.openInputStream(uri), null, o2)
    }
}


fun getOutputMediaFile(type: Int, context: Context): File? {
    val mediaStorageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

    /**Create the storage directory if it does not exist */
    if (!mediaStorageDir!!.exists()) {
        if (!mediaStorageDir.mkdirs()) {
            return null
        }
    }

    /**Create a media file name */
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val mediaFile: File
    if (type == 1) {
        mediaFile = File(
            mediaStorageDir.path + File.separator +
                    "IMG_" + timeStamp + ".png"
        )

        //            imageFilePath = mediaFile.getAbsolutePath();
    } else {
        return null
    }

    return mediaFile
}

fun convertBitmapToBase64(bitmap: Bitmap): String {

    val baos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos) //bm is the bitmap object
    val byteArrayImage = baos.toByteArray()
    return Base64.encodeToString(byteArrayImage, Base64.DEFAULT)


}

fun convertBase64ToBitmap(str: String): Bitmap {
    val decodedString = Base64.decode(str, Base64.DEFAULT)

    return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
}

fun captureImageResultOK(
    picUri: Uri?,
    context: Context
): Bitmap? {

    var bitmap: Bitmap? = null

    try {

        bitmap = decodeUri(context, picUri, Constants.requeiredSize)


    } catch (e: FileNotFoundException) {
        Log.e(TAG, "onActivityResult:FileNotFoundException $e")
    }

    return bitmap
}

fun getPathFromselectedImage(bitmap: Bitmap, context: Context): String? {

    val selectedImage = getImageUri(context, bitmap)

    return getPathFromUri(selectedImage, context)

}

fun getPathListFromBitmapList(
    bitmapList: ArrayList<Bitmap>,
    context: Context
): ArrayList<String>? {
    val paths = arrayListOf<String>()
    for (bitmap in bitmapList) {
        val selectedImage = getImageUri(context, bitmap)
        getPathFromUri(selectedImage, context)?.let {
            paths.add(it)

        }
    }

    return paths

}

fun galleryResultOk(data: Intent, mContext: Context): Bitmap? {
    var bitmap: Bitmap? = null

    if (data.data != null) {

        val mImageUri = data.data
        try {
            bitmap = decodeUri(mContext, mImageUri, Constants.requeiredSize)

        } catch (e: FileNotFoundException) {
        }


    }

    return bitmap
}

fun galleryResultOk(mImageUri: Uri, mContext: Context): Bitmap? {
    var bitmap: Bitmap? = null
    try {
        bitmap = decodeUri(mContext, mImageUri, Constants.requeiredSize)

    } catch (e: FileNotFoundException) {
    }

    return bitmap
}

//    fun galleryResultOkMulti(mClipData: ClipData): ArrayList<Multi64String>? {
//        val images = arrayListOf<Multi64String>()
//
//        for (i in 0 until mClipData.itemCount) {
//
//            val item = mClipData.getItemAt(i)
//            val uri = item.uri

//            try {
//                images.add(
//                    Multi64String(
//                        getPathFromselectedImage(decodeUri(mContext, uri, requeiredSize)!!)!!,
//                        convertBitmapToBase64(decodeUri(mContext, uri, requeiredSize)!!)
//                    )
//                )
//            } catch (e: FileNotFoundException) {
//
//            }
//
//
//        }
//
//        return images
//    }

fun convertMultiBitmapTo64(list: ArrayList<Bitmap>): ArrayList<String> {
    val images = arrayListOf<String>()

    for (item in list) {

        try {
            images.add(convertBitmapToBase64(item))
        } catch (e: FileNotFoundException) {
        }


    }

    return images
}

fun galleryResultOkMultiBitMap(mClipData: ClipData, mContext: Context): ArrayList<Bitmap> {
    val images = arrayListOf<Bitmap>()

    for (i in 0 until mClipData.itemCount) {

        val item = mClipData.getItemAt(i)
        val uri = item.uri

        try {
            decodeUri(mContext, uri, Constants.requeiredSize)?.let {
                images.add(it)

            }
        } catch (e: FileNotFoundException) {
        }


    }

    return images
}


fun shareVia(extraTxt: String, title: String, mContext: Context) {
    val sendIntent = Intent()
    sendIntent.type = "text/plain"
    sendIntent.action = Intent.ACTION_SEND
    sendIntent.putExtra(Intent.EXTRA_TEXT, extraTxt)
    val createChooser = Intent.createChooser(sendIntent, title)
    createChooser.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    mContext.startActivity(createChooser)
}

//    fun btnProgress(
//        btn: CircularProgressButton,
//        boolean: Boolean
//    ) {
//        if (boolean) {
//
//        } else {
//        }
//
//
//    }

fun focusOnView(scrollview: ScrollView, view: View) {
//        scrollview.post {
//                        scrollview.scrollTo(0, view.bottom)
//            scrollview.smoothScrollTo(view.left + view.right - scrollview.width, 0)


//        }

    view.parent.requestChildFocus(view, view)

}

fun cancelNotifications(context: Context) {
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.cancelAll()

}


fun isNotificationChannelEnabled(channelId: String, context: Context): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        if (!TextUtils.isEmpty(channelId)) {
            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = manager.getNotificationChannel(channelId)
            return channel.importance != NotificationManager.IMPORTANCE_NONE
        }
        return false
    } else {
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

}


fun lonDateDiff(date1: Date?, date2: Date?, timeUnit: TimeUnit?): Long {
    val l = date1!!.time - date2!!.time
    val convert =
        try {
            timeUnit?.convert(l, TimeUnit.MILLISECONDS)
        } catch (e: java.lang.Exception) {
            0
        }
    return convert as Long
}

fun openIntent(activity: Activity, intent: Intent) {
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    activity.startActivity(intent)

}

fun openIntent(activity: Activity, intent: Intent, bundle: Bundle) {
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    activity.startActivity(intent, bundle)

}

fun openIntent(activity: Activity, intent: Intent, options: ActivityOptions) {
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    activity.startActivity(intent, options.toBundle())

}


fun openUri(activity: Activity, uri: String) {
    if (uri.isNotEmpty()) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(uri)
        activity.startActivity(intent)
    }

}

fun openUri(context: Context, uri: String) {
    if (uri.isNotEmpty()) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(uri)
        context.startActivity(intent)
    }

}

//
//    fun openIntentForResult(activity: Activity, intent: Intent, code: Int) {
//        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//        activity.startActivityForResult(intent, code)
//        activity.overridePendingTransition(R.anim.enter_bottom, R.anim.fade_out)
//
//    }
//
//    fun closeAnimIntent(activity: Activity) {
//        activity.overridePendingTransition(R.anim.fade_in, R.anim.exit_bottom)
//
//    }
//
fun retrieveErr(it: JSONObject, key: String): String {
    return when {
        it.has(key) -> {
            it.getJSONArray(key)[0].toString()
        }
        else -> ""
    }

}

fun retrieveGeneralErr(gErrors: JSONArray): String {
    gErrors.let {
        return if (it.length() > 0) {
            it[0].toString()
        } else ""
    }
}

fun getGeneralErrors(it: JSONObject): JSONArray {

    if (it.has(Constants.ERRORS)) {
        val errors = it.getJSONObject(Constants.ERRORS)
        if (errors.has(Constants.GENERAL_ERRORS)) {
            return errors.getJSONArray(Constants.GENERAL_ERRORS)

        }

    }
    return JSONArray()

}

fun getFormErrors(it: JSONObject): JSONObject {
    var jsonObject = JSONObject()
    if (it.has(Constants.ERRORS)) {
        val errors = it.getJSONObject(Constants.ERRORS) as JSONObject
        if (errors.has(Constants.FORM_ERRORS)) {
            jsonObject = errors.getJSONObject(Constants.FORM_ERRORS)
            return jsonObject
        }

    }
    return jsonObject
}

fun getJSONObject(it: JSONObject, key: String): JSONObject {
    return if (it.has(key)) {
        it.getJSONObject(key) as JSONObject
    } else {
        JSONObject()
    }
}

fun getJSONArray(it: JSONObject, key: String): JSONArray {
    return if (it.has(key)) {
        it.getJSONArray(key)
    } else {
        JSONArray()
    }
}

fun retrieveJSONArrayFirstItem(gErrors: JSONArray): String {
    gErrors.let {
        return if (it.length() > 0) {
            it[0].toString()
        } else ""
    }
}

//
//    fun loadImage(drawable: String?, imageView: ImageView) {
//        if (drawable != null)
//            Picasso.get()
//                .load(drawable)
////                .resize(50, 50)
////                .centerCrop()
//                .into(imageView)
//
//
//    }

//    fun loadImageBitmap(drawable: String?, imageView: ImageView): Bitmap? {
//        var _bitmap: Bitmap? = null
//
//        if (drawable != null)
//            Picasso.get()
//                .load(drawable)
////                .resize(50, 50)
////                .centerCrop()
//                .into(object : Target {
//                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {

//                    }
//
//                    override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {

//                    }
//
//                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
//                        _bitmap = bitmap!!
//                    }
//fhas
//                })
//        _bitmap?.let {
//            imageView.setImageBitmap(it)
//
//        }
//        return _bitmap
//    }
//
//    fun loadImageFromFile(drawable: String?, imageView: ImageView) {
//        if (drawable != null)
//            Picasso.get()
//                .load(File(drawable))
////                .resize(50, 50)
////                .centerCrop()
//
//                .into(imageView)
//
//    }

//
//    fun loadImage(drawable: Int?, imageView: ImageView) {
//        drawable?.let {
//            Picasso.get()
//                .load(drawable)
////                .resize(50, 50)
////                .centerCrop()
//                .into(imageView)
//        }
//
//
//    }

//    fun checkEdtLenght(edts: List<EdtLenght>): Boolean {
//        for (item: EdtLenght in edts) {
//            if (item.edt.length() < item.lenght_) {
//                setupFloatingLabelError(item.inputLay, item.edt, "")
//                return false
//            }
//        }
//
//        return true
//    }

//    fun setupFloatingLabelError(
//        floatingUsernameLabel: TextInputLayout,
//        edt: com.google.android.material.textfield.TextInputEditText, error: String
//    ) {
//        floatingUsernameLabel.error = error
//        floatingUsernameLabel.isErrorEnabled = true
//
//    }

fun setupFloatingLabelError(
    floatingUsernameLabel: TextInputLayout,
    edt: com.google.android.material.textfield.TextInputEditText, error: String, length: Int
) {
    floatingUsernameLabel.error = error
    floatingUsernameLabel.isErrorEnabled = true
//
    floatingUsernameLabel.editText!!.addTextChangedListener(object : TextWatcher {
        override fun onTextChanged(text: CharSequence, start: Int, count: Int, after: Int) {
            if (text.length in 0..length) {
                floatingUsernameLabel.error = error
                floatingUsernameLabel.isErrorEnabled = true

            } else {
                floatingUsernameLabel.isErrorEnabled = false

            }
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

        }

        override fun afterTextChanged(s: Editable) {

        }
    })
}

fun removeLabelError(
    floatingUsernameLabel: TextInputLayout
) {
    floatingUsernameLabel.error = ""
    floatingUsernameLabel.isErrorEnabled = false
//
}

fun saveImageToExternal(bm: Bitmap, context: Context): File {
    //Create Path to save Image
    val path =
        Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES +
                    ""
        ) //Creates app specific folder
    path.mkdirs()
    val imageFile = File(path, "" + ".png") // Imagename.png
    val out = FileOutputStream(imageFile)
    try {
        bm.compress(Bitmap.CompressFormat.PNG, 100, out)// Compress Image
        out.flush()
        out.close()

        // Tell the media scanner about the new file so that it is
        // immediately available to the user.
        MediaScannerConnection.scanFile(
            context,
            arrayOf(imageFile.absolutePath),
            null,
            object : MediaScannerConnection.OnScanCompletedListener {
                override fun onScanCompleted(p0: String?, p1: Uri?) {
                    Log.i("ExternalStorage", "Scanned " + path + ":");
                }

            })
    } catch (e: Exception) {
    }


    return imageFile
}

fun dpToPx(dp: Int): Int {
    return (dp * Resources.getSystem().displayMetrics.density).toInt()
}

fun pxToDp(px: Int): Int {
    return (px / Resources.getSystem().displayMetrics.density).toInt()
}

fun isNotNullOrEmpty(string: String?): Boolean {
    return !(string == null || string.length == 0)
}

@SuppressLint("MissingPermission")
fun isOnline(mContext: Context): Boolean {
    val connectivityManager =
        mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val netInfo = connectivityManager.activeNetworkInfo
    return netInfo != null && netInfo.isConnected
}

//    fun getTextOffSeparator(editText: CharSequence?): String {
//        return NumberTextWatcherForThousand.trimCommaOfString(editText!!.toString())
//    }
//
//    fun setSeparator(editText: EditText) {
//        editText.addTextChangedListener(NumberTextWatcherForThousand(editText))
//    }

fun removeFormatter(str: String): String? {
    return String.format("%,d", str)
}

fun formatter(number: Int?): CharSequence? {
    return number?.let {
        DecimalFormat("##,###,###").format(number)
    }

}


fun formatter(number: String?): CharSequence? {
    val roundToInt = number!!.toDouble().roundToInt()
    return DecimalFormat("##,###,###").format(roundToInt)


}

fun formatter(number: Long?): CharSequence? {
    val roundToInt = number!!.toDouble().roundToInt()
    return DecimalFormat("##,###,###").format(roundToInt)


}

fun formatter(number: Double?): CharSequence? {
    return number?.let {
        DecimalFormat("##,###,###").format(number)
    }

}

fun converStrToDate(time: String): Date? {
    val sdf2 = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    var date: Date? = null
    try {
        date = sdf2.parse(time)

    } catch (e: Exception) {


    }

    return date
}


fun convertStringToDate(time: String): String? {
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    val sdf2 = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    var datestr: String? = null

    if (time.isNotEmpty())
        try {
            val date = sdf.parse(time)
            date?.let {
                datestr = sdf2.format(date)

            }

        } catch (e: Exception) {


        }

    return datestr

}

fun getCalendarFromDate(date: Date): Calendar {
    val cal = Calendar.getInstance()
    cal.time = date
    return cal
}

fun getMinFromDate(date: Date): Int {
    val cal = getCalendarFromDate(date)
    return cal.get(Calendar.MINUTE)

}

fun getHourFromDate(date: Date): Int {
    val cal = getCalendarFromDate(date)
    return cal.get(Calendar.HOUR_OF_DAY)

}

fun getDayFromDate(date: Date): Int {
    val cal = getCalendarFromDate(date)
    return cal.get(Calendar.DAY_OF_MONTH)

}

fun getMonthFromDate(date: Date): Int {
    val cal = getCalendarFromDate(date)
    return cal.get(Calendar.MONTH)

}

fun setDirection(window: Window) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
    }

}

fun checkArabicGravity(str: String, txv: TextView) {
    if (isProbablyArabic(str)) {
        txv.gravity = Gravity.END
    } else {
        txv.gravity = Gravity.START
    }

}

fun checkToResetErr(
    lay: TextInputLayout,
    edt: com.google.android.material.textfield.TextInputEditText
) {
    edt.doAfterTextChanged { it ->
        it?.let {
            if (it.isEmpty()) {
                removeLabelError(lay)
            }
        }
    }
}


fun convertTimeStrToLong(time: String): Long {
    //time example=> 20:20
    val sdf = SimpleDateFormat("HH:mm:ss", Locale.US)
    sdf.timeZone = TimeZone.getTimeZone("GMT")
    val sdf2 = SimpleDateFormat("HH:mm", Locale.US)
    sdf2.timeZone = TimeZone.getTimeZone("GMT")

    return try {
        sdf.parse(time)?.time ?: 0

    } catch (e: Exception) {
        try {
            sdf2.parse(time)?.time ?: 0

        } catch (e: Exception) {
            Log.e(TAG, "convertTimeStrToLong: $e")
            0
        }

    }

}


fun convertMinSecStrToLong(time: String): Long {
    //time example=> 20:20
    val sdf = SimpleDateFormat("mm:ss", Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone("GMT");

    return sdf.parse(time)?.time ?: 0
}


fun convertLongSecondsToTimerFormate(millis: Long): String {
//***both method1 & method2 are correct

    //method1
//        val durationSeconds=millis/1000
//        String.format("%02d:%02d:%02d", durationSeconds / 3600,
//            (durationSeconds % 3600) / 60, (durationSeconds % 60))

//****
    //method2
    return String.format(
        "%02d:%02d:%02d",
        getHourFromLongMillis(millis),
        getMinFromLongMillis(millis),
        getSecFromLongMillis(millis)
    )

}

fun getHourFromLongMillis(millis: Long): Long {
    return TimeUnit.MILLISECONDS.toHours(millis)

}

fun getMinFromLongMillis(millis: Long): Long {
    return TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1)

}

fun getSecFromLongMillis(millis: Long): Long {
    return TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1)

}

fun parseColor(it: String): Int {
    val color = if (it.contains("#")) {
        it
    } else {

        "#" + it
    }
    return try {
        Color.parseColor(color)
        // color is a valid color
    } catch (iae: IllegalArgumentException) {
        // This color string is not valid
        Log.e(TAG, "parseColor: " + iae.message)
        0
    }
}

fun copyToClipBoard(context: Context, address: String, view: View) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("code", address)
    clipboard.setPrimaryClip(clip)
//    showMsgColor(view, co.idearun.common.R.string.copied, Constants.SUCCESS_MSG)


}

