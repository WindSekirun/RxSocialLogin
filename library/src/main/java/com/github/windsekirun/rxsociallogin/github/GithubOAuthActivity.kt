package com.github.windsekirun.rxsociallogin.github

import android.annotation.SuppressLint
import com.github.kittinunf.fuel.httpPost
import com.github.windsekirun.rxsociallogin.OAuthConstants
import com.github.windsekirun.rxsociallogin.SocialLogin
import com.github.windsekirun.rxsociallogin.intenal.fuel.toResultObservable
import com.github.windsekirun.rxsociallogin.intenal.oauth.BaseOAuthActivity
import com.github.windsekirun.rxsociallogin.intenal.oauth.OAuthWebViewClient
import com.github.windsekirun.rxsociallogin.model.PlatformType
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_oauth.*

class GithubOAuthActivity : BaseOAuthActivity() {
    private val githubConfig: GithubConfig by lazy { SocialLogin.getConfig(PlatformType.GITHUB) as GithubConfig }

    @SuppressLint("SetJavaScriptEnabled")
    override fun init() {
        var url = "${OAuthConstants.GITHUB_URL}?client_id=${githubConfig.clientId}"

        if (githubConfig.scopeList.isNotEmpty()) {
            val scope = githubConfig.scopeList.joinToString(",")
            url += scope
        }

        webView.settings.javaScriptEnabled = true
        webView.webViewClient = OAuthWebViewClient {
            requestOAuthToken(it)
        }

        webView.loadUrl(url)

        setToolbar(githubConfig.activityTitle)
    }

    private fun requestOAuthToken(code: String) {
        val parameters = listOf("client_id" to githubConfig.clientId,
                "client_secret" to githubConfig.clientSecret, "code" to code)

        val header = "Accept" to "application/json"

        disposable = OAuthConstants.GITHUB_OAUTH.httpPost(parameters)
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