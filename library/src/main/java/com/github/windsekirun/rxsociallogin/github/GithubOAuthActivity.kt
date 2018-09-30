package com.github.windsekirun.rxsociallogin.github

import android.annotation.SuppressLint
import android.net.Uri
import android.webkit.WebView
import com.github.kittinunf.fuel.httpPost
import com.github.windsekirun.rxsociallogin.OAuthConstants
import com.github.windsekirun.rxsociallogin.RxSocialLogin
import com.github.windsekirun.rxsociallogin.intenal.fuel.toResultObservable
import com.github.windsekirun.rxsociallogin.intenal.oauth.BaseOAuthActivity
import com.github.windsekirun.rxsociallogin.intenal.oauth.OAuthWebViewClient
import com.github.windsekirun.rxsociallogin.intenal.model.PlatformType
import com.github.windsekirun.rxsociallogin.intenal.webview.EnhanceWebView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_oauth.*

class GithubOAuthActivity : BaseOAuthActivity() {
    private val githubConfig: GithubConfig by lazy { RxSocialLogin.getPlatformConfig(PlatformType.GITHUB) as GithubConfig }

    @SuppressLint("SetJavaScriptEnabled")
    override fun init() {
        var url = "${OAuthConstants.GITHUB_URL}?client_id=${githubConfig.clientId}"

        if (githubConfig.scopeList.isNotEmpty()) {
            val scope = githubConfig.scopeList.joinToString(",")
            url += scope
        }

        webView.setWebViewHandler(object : EnhanceWebView.EnhanceWebViewHandler {
            override fun onPageFinished(view: WebView?, url: String?) {}

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?, uri: Uri?, scheme: String?,
                                                  host: String?, parameters: MutableMap<String, String>?): Boolean {
                try {
                    if (url?.contains("?code=") == false) return false
                    requestOAuthToken(url!!)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                return false
            }
        })

        webView.setEnableGoBack(true)
        webView.enableFormUpload(this)
        webView.setUrl(url)

        setToolbar(githubConfig.activityTitle)
    }

    override fun onBackPressed() {
        if (webView.onBackPressed()) {
            super.onBackPressed()
        }
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