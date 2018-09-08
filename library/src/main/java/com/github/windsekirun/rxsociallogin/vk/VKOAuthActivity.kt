package com.github.windsekirun.rxsociallogin.vk

import android.annotation.SuppressLint
import com.github.windsekirun.rxsociallogin.OAuthConstants
import com.github.windsekirun.rxsociallogin.SocialLogin
import com.github.windsekirun.rxsociallogin.intenal.net.OkHttpHelper
import com.github.windsekirun.rxsociallogin.intenal.oauth.BaseOAuthActivity
import com.github.windsekirun.rxsociallogin.intenal.oauth.OAuthWebViewClient
import com.github.windsekirun.rxsociallogin.intenal.oauth.clearCookies
import com.github.windsekirun.rxsociallogin.model.PlatformType
import kotlinx.android.synthetic.main.activity_oauth.*

class VKOAuthActivity : BaseOAuthActivity() {
    private val vkConfig: VKConfig by lazy { SocialLogin.getConfig(PlatformType.VK) as VKConfig }

    @SuppressLint("SetJavaScriptEnabled")
    override fun init() {
        val state = randomString(22)

        var url = "${OAuthConstants.VK_URL}?response_type=code&" +
                "client_id=${vkConfig.clientId}&redirect_uri=${vkConfig.redirectUri}&" +
                "display=page&state=$state&v=${vkConfig.version}&scope=state"

        if (vkConfig.requireEmail) url += "%20email"

        if (vkConfig.clearCookies) {
            clearCookies()
        }

        webView.settings.javaScriptEnabled = true
        webView.webViewClient = OAuthWebViewClient {
            requestOAuthToken(it)
        }

        webView.loadUrl(url)

        setToolbar(vkConfig.activityTitle)
    }

    private fun requestOAuthToken(code: String) {
        val formArray = arrayOf(
                "redirect_uri" to vkConfig.redirectUri,
                "client_id" to vkConfig.clientId,
                "client_secret" to vkConfig.clientSecret,
                "code" to code)

        val header = "Content-Type" to "application/json"

        disposable = OkHttpHelper.post(OAuthConstants.VK_OAUTH, header, formArray).requestAccessToken()
    }
}