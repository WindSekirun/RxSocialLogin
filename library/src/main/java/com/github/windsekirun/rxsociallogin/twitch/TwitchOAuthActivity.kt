package com.github.windsekirun.rxsociallogin.twitch

import android.annotation.SuppressLint
import com.github.kittinunf.fuel.httpPost
import com.github.windsekirun.rxsociallogin.OAuthConstants
import com.github.windsekirun.rxsociallogin.RxSocialLogin
import com.github.windsekirun.rxsociallogin.intenal.fuel.toResultObservable
import com.github.windsekirun.rxsociallogin.intenal.oauth.BaseOAuthActivity
import com.github.windsekirun.rxsociallogin.intenal.oauth.OAuthWebViewClient
import com.github.windsekirun.rxsociallogin.model.PlatformType
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_oauth.*

class TwitchOAuthActivity : BaseOAuthActivity() {
    private val config: TwitchConfig by lazy { RxSocialLogin.getPlatformConfig(PlatformType.TWITCH) as TwitchConfig }

    @SuppressLint("SetJavaScriptEnabled")
    override fun init() {
        val state = randomString(22)

        var url = "${OAuthConstants.TWITCH_URL}?client_id=${config.clientId}&" +
                "response_type=code&redirect_uri=${config.redirectUri}&state=$state&scope=user:edit"

        if (config.requireEmail) {
            url += "+user:read:email"
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
        val parameters = listOf(
                "redirect_uri" to config.redirectUri,
                "client_id" to config.clientId,
                "client_secret" to config.clientSecret,
                "grant_type" to "authorization_code",
                "code" to code)

        val header = "Content-Type" to "application/json"

        disposable = OAuthConstants.TWITCH_OAUTH.httpPost(parameters)
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