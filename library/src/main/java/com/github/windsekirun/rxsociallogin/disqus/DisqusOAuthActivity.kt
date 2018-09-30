package com.github.windsekirun.rxsociallogin.disqus

import android.annotation.SuppressLint
import android.net.Uri
import android.webkit.WebView
import com.github.kittinunf.fuel.httpPost
import com.github.windsekirun.rxsociallogin.OAuthConstants
import com.github.windsekirun.rxsociallogin.RxSocialLogin
import com.github.windsekirun.rxsociallogin.intenal.fuel.toResultObservable
import com.github.windsekirun.rxsociallogin.intenal.model.PlatformType
import com.github.windsekirun.rxsociallogin.intenal.oauth.BaseOAuthActivity
import com.github.windsekirun.rxsociallogin.intenal.webview.EnhanceWebView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_oauth.*

class DisqusOAuthActivity : BaseOAuthActivity() {
    private val config: DisqusConfig by lazy { RxSocialLogin.getPlatformConfig(PlatformType.DISQUS) as DisqusConfig }

    @SuppressLint("SetJavaScriptEnabled")
    override fun init() {
        val url = "${OAuthConstants.DISQUS_URL}?client_id=${config.clientId}&" +
                "scope=read&response_type=code&redirect_uri=${config.redirectUri}"

        webView.setWebViewHandler(object : EnhanceWebView.EnhanceWebViewHandler {
            override fun onPageFinished(view: WebView?, url: String?) {}

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?, uri: Uri?, scheme: String?,
                                                  host: String?, parameters: MutableMap<String, String>?): Boolean {
                try {
                    if (url?.contains("?code=") == false) return false
                    requestOAuthToken(url!!)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                return false
            }
        })

        webView.setEnableGoBack(true)
        webView.enableFormUpload(this)
        webView.setUrl(url)

        setToolbar(config.activityTitle)
    }

    override fun onBackPressed() {
        if (webView.onBackPressed()) {
            super.onBackPressed()
        }
    }

    private fun requestOAuthToken(code: String) {
        val parameters = listOf(
                "redirect_uri" to config.redirectUri,
                "client_id" to config.clientId,
                "client_secret" to config.clientSecret,
                "grant_type" to "authorization_code",
                "code" to code)

        val header = "Content-Type" to "application/json"

        disposable = OAuthConstants.DISQUS_OAUTH.httpPost(parameters)
                .header(header)
                .toResultObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { result, error ->
                    if (error == null && result.component1() != null) {
                        finishActivity(result.component1() as String)
                    } else {
                        finishActivity()
                    }
                }
    }
}