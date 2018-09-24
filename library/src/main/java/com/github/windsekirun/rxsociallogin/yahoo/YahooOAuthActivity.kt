package com.github.windsekirun.rxsociallogin.yahoo

import android.annotation.SuppressLint
import android.util.Base64
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

class YahooOAuthActivity : BaseOAuthActivity() {
    private val config: YahooConfig by lazy { RxSocialLogin.getPlatformConfig(PlatformType.YAHOO) as YahooConfig }

    @SuppressLint("SetJavaScriptEnabled")
    override fun init() {
        val nonce = randomString(21)

        val url = "${OAuthConstants.YAHOO_URL}?client_id=${config.clientId}&" +
                "redirect_uri=${config.redirectUri}&response_type=code&scope=openid%20sdps-r&nonce=$nonce"

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
                "grant_type" to "authorization_code",
                "redirect_uri" to config.redirectUri,
                "client_id" to config.clientId,
                "client_secret" to config.clientSecret,
                "code" to code)

        val basicToken = String(Base64.encode("${config.clientId}:${config.clientSecret}".toByteArray(), Base64.URL_SAFE))
                .replace("\n", "")

        val header = "Authorization" to "Basic $basicToken"

        disposable = OAuthConstants.YAHOO_OAUTH.httpPost(parameters)
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