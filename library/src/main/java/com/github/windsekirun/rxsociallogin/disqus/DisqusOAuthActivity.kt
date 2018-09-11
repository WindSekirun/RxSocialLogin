package com.github.windsekirun.rxsociallogin.disqus

import android.annotation.SuppressLint
import android.util.Base64
import com.github.windsekirun.rxsociallogin.OAuthConstants
import com.github.windsekirun.rxsociallogin.SocialLogin
import com.github.windsekirun.rxsociallogin.intenal.net.OkHttpHelper
import com.github.windsekirun.rxsociallogin.intenal.oauth.BaseOAuthActivity
import com.github.windsekirun.rxsociallogin.intenal.oauth.OAuthWebViewClient
import com.github.windsekirun.rxsociallogin.intenal.oauth.clearCookies
import com.github.windsekirun.rxsociallogin.model.PlatformType
import kotlinx.android.synthetic.main.activity_oauth.*

class DisqusOAuthActivity : BaseOAuthActivity() {
    private val config: DisqusConfig by lazy { SocialLogin.getConfig(PlatformType.DISQUS) as DisqusConfig }

    @SuppressLint("SetJavaScriptEnabled")
    override fun init() {
        val url = "${OAuthConstants.DISQUS_URL}?client_id=${config.clientId}&" +
                "scope=read&response_type=code&redirect_uri=${config.redirectUri}"

        if (config.clearCookies) {
            clearCookies()
        }

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true

        webView.webViewClient = OAuthWebViewClient {
            requestOAuthToken(it)
        }

        webView.loadUrl(url)

        setToolbar(config.activityTitle)
    }

    private fun requestOAuthToken(code: String) {
        val formArray = arrayOf(
                "redirect_uri" to config.redirectUri,
                "client_id" to config.clientId,
                "client_secret" to config.clientSecret,
                "grant_type" to "authorization_code",
                "code" to code)

        val header = "Content-Type" to "application/json"

        disposable = OkHttpHelper.post(OAuthConstants.DISQUS_OAUTH, header, formArray).requestAccessToken()
    }
}