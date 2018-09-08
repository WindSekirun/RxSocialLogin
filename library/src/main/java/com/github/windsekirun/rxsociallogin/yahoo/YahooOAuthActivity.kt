package com.github.windsekirun.rxsociallogin.yahoo

import android.annotation.SuppressLint
import android.util.Base64
import com.github.windsekirun.rxsociallogin.SocialLogin
import com.github.windsekirun.rxsociallogin.intenal.net.OkHttpHelper
import com.github.windsekirun.rxsociallogin.intenal.oauth.BaseOAuthActivity
import com.github.windsekirun.rxsociallogin.intenal.oauth.OAuthWebViewClient
import com.github.windsekirun.rxsociallogin.intenal.oauth.clearCookies
import com.github.windsekirun.rxsociallogin.model.SocialType
import kotlinx.android.synthetic.main.activity_oauth.*

class YahooOAuthActivity : BaseOAuthActivity() {
    private val config: YahooConfig by lazy { SocialLogin.getConfig(SocialType.YAHOO) as YahooConfig }

    @SuppressLint("SetJavaScriptEnabled")
    override fun init() {
        val nonce = randomString(21)

        val url = "${YahooOAuthConstants.YAHOO_URL}?client_id=${config.clientId}&" +
                "redirect_uri=${config.redirectUri}&response_type=code&scope=openid%20sdps-r&nonce=$nonce"

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

        val basicToken = String(Base64.encode("${config.clientId}:${config.clientSecret}".toByteArray(), Base64.URL_SAFE))
                .replace("\n", "")

        val header = "Authorization" to "Basic $basicToken"

        disposable = OkHttpHelper.post(YahooOAuthConstants.YAHOO_OAUTH, header, formArray).requestAccessToken()
    }
}