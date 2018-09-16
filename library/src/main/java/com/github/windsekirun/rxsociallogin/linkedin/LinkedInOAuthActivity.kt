package com.github.windsekirun.rxsociallogin.linkedin

import android.annotation.SuppressLint
import com.github.windsekirun.rxsociallogin.OAuthConstants
import com.github.windsekirun.rxsociallogin.SocialLogin
import com.github.windsekirun.rxsociallogin.intenal.net.OkHttpHelper
import com.github.windsekirun.rxsociallogin.intenal.oauth.BaseOAuthActivity
import com.github.windsekirun.rxsociallogin.intenal.oauth.OAuthWebViewClient
import com.github.windsekirun.rxsociallogin.model.PlatformType
import kotlinx.android.synthetic.main.activity_oauth.*

class LinkedInOAuthActivity : BaseOAuthActivity() {
    private val linkedinConfig: LinkedinConfig by lazy { SocialLogin.getConfig(PlatformType.LINKEDIN) as LinkedinConfig }

    @SuppressLint("SetJavaScriptEnabled")
    override fun init() {
        val state = randomString(22)

        var url = "${OAuthConstants.LINKEDIN_URL}?response_type=code&" +
                "client_id=${linkedinConfig.clientId}&redirect_uri=${linkedinConfig.redirectUri}&" +
                "state=$state&scope=r_basicprofile"

        if (linkedinConfig.requireEmail) url += "%20r_emailaddress"

        webView.settings.javaScriptEnabled = true
        webView.webViewClient = OAuthWebViewClient {
            requestOAuthToken(it)
        }

        webView.loadUrl(url)

        setToolbar(linkedinConfig.activityTitle)
    }

    private fun requestOAuthToken(code: String) {
        val formArray = arrayOf(
                "grant_type" to "authorization_code",
                "redirect_uri" to linkedinConfig.redirectUri,
                "client_id" to linkedinConfig.clientId,
                "client_secret" to linkedinConfig.clientSecret,
                "code" to code)

        val header = "Content-Type" to "application/json"

        disposable = OkHttpHelper.post(OAuthConstants.LINKEDIN_OAUTH, header, formArray).requestAccessToken()
    }
}