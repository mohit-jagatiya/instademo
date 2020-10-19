package com.spaceo.myapplication.insdataselection

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.webkit.WebView
import android.webkit.WebViewClient
import com.geekmeets.utils.gone
import com.spaceo.myapplication.R
import kotlinx.android.synthetic.main.custom_dialog.*


class CustomDialogClass(context: Context, callBackUrlResponseListener: CallBackUrlResponseListener) : Dialog(context) {

    init {
        setCancelable(true)
    }

    private val appID = context.getString(R.string.instagram_app_id)
    private val redirectUrl = context.getString(R.string.instagram_redirect_url_with_encode)

    /**Replace your instagram app id and redirect url*/
    val url = "https://api.instagram.com/oauth/authorize?app_id=$appID&redirect_uri=$redirectUrl&scope=user_profile,user_media&response_type=code"

    val callBackUrlResponse = callBackUrlResponseListener

    //  private val redirectedUrl = "https://www.techumiya.com/?code="
    // Replace with your redirected url
    private val redirectedUrl = "https://www.google.com/?code="
    //Replace with your redirected url

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.custom_dialog)
        attachListener()
        webView.settings.javaScriptEnabled = true
        webView.loadUrl(url)
    }

    private fun attachListener() {
        /*listener for webView*/
        webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                Log.d("TAG", "shouldUrl$url")
                if (url.endsWith("#_")) {
                    println("Load URL: $url")
                    view.stopLoading()
                    webView.gone()
                    callBackUrlResponse.gettingResponseCallBack(getAuthCodeFromRedirectedUrl(url))
                }
                return false
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
            }
        }
    }

    fun getAuthCodeFromRedirectedUrl(url: String): String {
        return url.replace(redirectedUrl, "").replace("#_", "")
    }

    interface CallBackUrlResponseListener {
        fun gettingResponseCallBack(response: String)
    }
}