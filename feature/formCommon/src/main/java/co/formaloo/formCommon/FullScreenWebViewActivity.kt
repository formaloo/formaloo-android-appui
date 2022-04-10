package co.formaloo.formCommon

import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import co.formaloo.common.BaseAppCompatActivity
import co.formaloo.common.extension.invisible
import co.formaloo.common.extension.visible
import co.formaloo.common.showBackBtn
import kotlinx.android.synthetic.main.activity_full_screen_webview.*
import timber.log.Timber

class FullScreenWebViewActivity:BaseAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen_webview)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        val title=    intent?.extras?.getString("title")?:""
        showBackBtn(supportActionBar)
        co.formaloo.common.setTitle(supportActionBar,title)

    val link=    intent?.extras?.getString("link")?:""

        val webview = videoviewfull

        webview.settings.userAgentString = "Android"
        webview.settings.loadWithOverviewMode = true
        webview.settings.javaScriptEnabled = true
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
        webview.setInitialScale(10)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            webview.webChromeClient = WebChromeClient()
        }

        webview.webViewClient = object : WebViewClient() {
            override fun onLoadResource(view: WebView?, url: String?) { /* compiled code */
//                        userStoreUrl = url

            }

            override fun onPageStarted(
                view: WebView?,
                url: String?,
                favicon: android.graphics.Bitmap?
            ) { /* compiled code */
                Timber.e("onPageStarted $url")
//                        userStoreUrl = url
                videoprogress.visible()
            }

            override fun onPageFinished(
                view: WebView?,
                url: String?
            ) { /* compiled code */
                Timber.e("onPageFinished $url")
                videoprogress.invisible()
            }

        }

        webview.loadUrl(link)

close_full_btn.setOnClickListener { onBackPressed() }
    }
}
