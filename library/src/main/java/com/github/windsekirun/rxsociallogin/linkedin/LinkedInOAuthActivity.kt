package com.github.windsekirun.rxsociallogin.linkedin

import android.annotation.SuppressLint
import android.net.Uri
import android.webkit.WebView
import com.github.kittinunf.fuel.httpPost
import com.github.windsekirun.rxsociallogin.OAuthConstants
import com.github.windsekirun.rxsociallogin.RxSocialLogin
import com.github.windsekirun.rxsociallogin.intenal.fuel.toResultObservable
import com.github.windsekirun.rxsociallogin.intenal.oauth.BaseOAuthActivity
import com.github.windsekirun.rxsociallogin.intenal.oauth.OAuthWebViewClient
import com.github.windsekirun.rxsociallogin.intenal.model.PlatformType
import com.github.windsekirun.rxsociallogin.intenal.webview.EnhanceWebView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_oauth.*

class LinkedInOAuthActivity : BaseOAuthActivity() {
    private val linkedinConfig: LinkedinConfig by lazy { RxSocialLogin.getPlatformConfig(PlatformType.LINKEDIN) as LinkedinConfig }

    @SuppressLint("SetJavaScriptEnabled")
    override fun init() {
        val state = randomString(22)

        var url = "${OAuthConstants.LINKEDIN_URL}?response_type=code&" +
                "client_id=${linkedinConfig.clientId}&redirect_uri=${linkedinConfig.redirectUri}&" +
                "state=$state&scope=r_basicprofile"

        if (linkedinConfig.requireEmail) url += "%20r_emailaddress"

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

        setToolbar(linkedinConfig.activityTitle)
    }

    override fun onBackPressed() {
        if (webView.onBackPressed()) {
            super.onBackPressed()
        }
    }

    private fun requestOAuthToken(code: String) {
        val parameters = listOf(
                "grant_type" to "authorization_code",
                "redirect_uri" to linkedinConfig.redirectUri,
                "client_id" to linkedinConfig.clientId,
                "client_secret" to linkedinConfig.clientSecret,
                "code" to code)

        val header = "Content-Type" to "application/json"

        disposable = OAuthConstants.LINKEDIN_OAUTH.httpPost(parameters)
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