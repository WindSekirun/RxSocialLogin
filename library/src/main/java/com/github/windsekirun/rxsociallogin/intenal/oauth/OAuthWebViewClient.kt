package com.github.windsekirun.rxsociallogin.intenal.oauth

import android.webkit.WebView
import android.webkit.WebViewClient

class OAuthWebViewClient(val callback: (String) -> Unit) : WebViewClient() {
    override fun shouldOverrideUrlLoading(view: WebView?, uri: String?): Boolean {
        super.shouldOverrideUrlLoading(view, uri)

        try {
            if (uri?.contains("?code=") == false) return false
            val url = uri!!
            callback.invoke(url.getCode())
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }
}
