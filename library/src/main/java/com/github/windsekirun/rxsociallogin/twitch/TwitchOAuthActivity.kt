package com.github.windsekirun.rxsociallogin.twitch

import android.annotation.SuppressLint
import com.github.windsekirun.rxsociallogin.OAuthConstants
import com.github.windsekirun.rxsociallogin.SocialLogin
import com.github.windsekirun.rxsociallogin.intenal.net.OkHttpHelper
import com.github.windsekirun.rxsociallogin.intenal.oauth.BaseOAuthActivity
import com.github.windsekirun.rxsociallogin.intenal.oauth.OAuthWebViewClient
import com.github.windsekirun.rxsociallogin.intenal.oauth.clearCookies
import com.github.windsekirun.rxsociallogin.model.PlatformType
import kotlinx.android.synthetic.main.activity_oauth.*

class TwitchOAuthActivity : BaseOAuthActivity() {
    private val config: TwitchConfig by lazy { SocialLogin.getConfig(PlatformType.TWITCH) as TwitchConfig }

    @SuppressLint("SetJavaScriptEnabled")
    override fun init() {
        val state = randomString(22)

        var url = "${OAuthConstants.TWITCH_URL}?client_id=${config.clientId}&" +
                "response_type=code&redirect_uri=${config.redirectUri}&state=$state&scope=user:edit"

        if (config.requireEmail) {
            url += "+user:read:email"
        }

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

        disposable = OkHttpHelper.post(OAuthConstants.TWITCH_OAUTH, header, formArray).requestAccessToken()
    }
}