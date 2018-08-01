package com.github.windsekirun.rxsociallogin.wordpress

import android.annotation.SuppressLint
import com.github.windsekirun.rxsociallogin.SocialLogin
import com.github.windsekirun.rxsociallogin.intenal.net.OkHttpHelper
import com.github.windsekirun.rxsociallogin.intenal.oauth.BaseOAuthActivity
import com.github.windsekirun.rxsociallogin.intenal.oauth.OAuthWebViewClient
import com.github.windsekirun.rxsociallogin.intenal.oauth.clearCookies
import com.github.windsekirun.rxsociallogin.model.SocialType
import kotlinx.android.synthetic.main.activity_oauth.*

class WordpressOAuthActivity : BaseOAuthActivity() {
    private val config: WordpressConfig by lazy { SocialLogin.getConfig(SocialType.WORDPRESS) as WordpressConfig }

    @SuppressLint("SetJavaScriptEnabled")
    override fun init() {
        val url = "${WordpressOAuthConstants.WORDPRESS_URL}?client_id=${config.clientId}&" +
                "redirect_uri=${config.redirectUri}&response_type=code"

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
                "grant_type" to "authorization_code",
                "redirect_uri" to config.redirectUri,
                "client_id" to config.clientId,
                "client_secret" to config.clientSecret,
                "code" to code)

        val header = "Content-Type" to "application/json"

        disposable = OkHttpHelper.post(WordpressOAuthConstants.WORDPRESS_OAUTH, header, formArray).requestAccessToken()
    }
}