package co.formaloo.formfields.adapter.holder

import android.view.View
import android.webkit.WebSettings
import android.webkit.WebViewClient
import co.formaloo.common.*
import co.formaloo.formfields.databinding.LayoutUiVideoItemBinding
import kotlinx.android.synthetic.main.layout_ui_field_footer.view.*
import kotlinx.android.synthetic.main.layout_ui_field_header.view.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber
import java.util.regex.Pattern
import co.formaloo.common.extension.invisible
import co.formaloo.common.extension.visible
class VideoViewHolder(itemView: View) : BaseVH(itemView), KoinComponent {

    val binding = LayoutUiVideoItemBinding.bind(itemView)
    val baseMethod: BaseMethod by inject()

    override fun initView(){
        val fieldUiHeader = binding.videoFieldLay
        fillHeaderData(fieldUiHeader.key_txv,fieldUiHeader.desc_txv)

        errStatus.observe(lifeCycleOwner){
            fieldBackgroundUI(binding.videoview,it)
            if (it == true){

                displayErrLay(binding.errLay, binding.errLay.error_txv)

            }
        }

       val webview= binding.videoview
        val url = field.video_link

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

            val youtuRegex ="v=([^\\\\s&#]*)"
//            val youtuRegex =
//                "/(?:https?://)?(?:www\\.)?(?:(?:youtu\\.be/)|(?:youtube\\.com)/(?:v/|u/\\w/|embed/|watch))(?:(?:\\?v=)?([^#&?=]*))?((?:[?&]\\w*=\\w*)*)/"
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
Timber.e("group $group")
                val link = when {
                    isAparat -> {
                        "https://www.aparat.com/video/video/embed/videohash/${group}/vt/frame"
                    }
                    else -> {
                        "https://www.youtube.com/embed/${group}"
                    }
                }
                webview.loadUrl(link)
                webview.webViewClient = WebViewClient()
            }


        } else {
            webview.invisible()
        }


        if (fromEdit) {
        } else {

        }

    }



}
