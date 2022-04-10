package co.formaloo.common

import android.content.pm.ActivityInfo
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.microsoft.appcenter.analytics.Analytics
import kotlinx.android.synthetic.main.activity_base_super.*
import org.json.JSONArray
import org.json.JSONObject
import org.koin.android.ext.koin.ERROR_MSG
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

open class BaseAppCompatActivity : AppCompatActivity(), KoinComponent {

    val baseMethod: BaseMethod by inject()
    val analytics: Analytics by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base_super)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        supportActionBar?.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    this,
                    R.color.design_default_color_background
                )
            )
        )

        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM;
//        supportActionBar?.setCustomView(R.layout.layout_abs)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.elevation=0f
        baseMethod.showBackBtn(supportActionBar)

    }

    override fun onResume() {
        
        super.onResume()
        

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
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

    fun checkInternet(): Boolean {
        if (!baseMethod.isOnline(this)) {

        }

        return baseMethod.isOnline(this)
    }

    fun checkInternet(view: View): Boolean {
        if (!baseMethod.isOnline(this)) {
             baseMethod.showMsg(view, getString(R.string.no_internet))

        }

        return baseMethod.isOnline(this)
    }




    fun setUpErrors(
        input_layout: TextInputLayout,
        edt: TextInputEditText,
        err: String
    ) {
        baseMethod.setupFloatingLabelError(input_layout, edt, err, 1)
    }



    fun showMsg(id: View, msg: String) {
        baseMethod.showMsg(id, msg)
    }

    fun showMsgColor(id: View, msg: String,tag:String) {
         baseMethod.showMsg(id, msg)
    }
    fun showMsgColor(id: View, msg: Int,tag:String) {
         baseMethod.showMsg(id, msg)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }

        try {
            return super.dispatchTouchEvent(ev)

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }




    fun retrieveGeneralErr(gErrors: JSONArray?) {
        gErrors?.let {
            if (it.length() > 0) {
                showMsgColor(activity_base_super, "${it[0]}", ERROR_MSG)
                Log.e("", "generalErrors ${it[0]}")

            }
        }
    }

    fun retrieveErr(
        it: JSONObject,
        key: String,
        inputLayout: TextInputLayout,
        editText: TextInputEditText
    ) {
        if (it.has(key)) {
            val err = it.getJSONArray(key)
            err?.let {
                setUpErrors(inputLayout, editText, err[0] as String)
            }

        }

    }

    fun retrieveNormalFormErr(
        it: JSONObject,
        key: String
    ) {
        if (it.has(key)) {
            val err = it.getJSONArray(key)
            err?.let {
                if (it.length() > 0) {
                    showMsgColor(activity_base_super, "${it[0]}", ERROR_MSG)

                }

            }
        }

    }

//    override fun attachBaseContext(base: Context) {
//        super.attachBaseContext(RuntimeLocaleChanger.wrapContext(base))
//    }
}
