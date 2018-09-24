package com.github.windsekirun.rxsociallogin.linkedin

import android.annotation.SuppressLint
import com.github.kittinunf.fuel.httpPost
import com.github.windsekirun.rxsociallogin.OAuthConstants
import com.github.windsekirun.rxsociallogin.RxSocialLogin
import com.github.windsekirun.rxsociallogin.intenal.fuel.toResultObservable
import com.github.windsekirun.rxsociallogin.intenal.oauth.BaseOAuthActivity
import com.github.windsekirun.rxsociallogin.intenal.oauth.OAuthWebViewClient
import com.github.windsekirun.rxsociallogin.intenal.model.PlatformType
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

        webView.settings.javaScriptEnabled = true
        webView.webViewClient = OAuthWebViewClient {
            requestOAuthToken(it)
        }

        webView.loadUrl(url)

        setToolbar(linkedinConfig.activityTitle)
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