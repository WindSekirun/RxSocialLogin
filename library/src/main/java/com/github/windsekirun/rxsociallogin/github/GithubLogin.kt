package com.github.windsekirun.rxsociallogin.github

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import com.github.kittinunf.fuel.httpDelete
import com.github.kittinunf.fuel.httpGet
import com.github.windsekirun.rxsociallogin.BaseSocialLogin
import com.github.windsekirun.rxsociallogin.OAuthConstants
import com.github.windsekirun.rxsociallogin.RxSocialLogin.EXCEPTION_FAILED_RESULT
import com.github.windsekirun.rxsociallogin.RxSocialLogin.EXCEPTION_USER_CANCELLED
import com.github.windsekirun.rxsociallogin.RxSocialLogin.getPlatformConfig
import com.github.windsekirun.rxsociallogin.intenal.exception.LoginFailedException
import com.github.windsekirun.rxsociallogin.intenal.model.PlatformType
import com.github.windsekirun.rxsociallogin.intenal.oauth.AccessTokenProvider
import com.github.windsekirun.rxsociallogin.intenal.oauth.LoginOAuthActivity
import com.github.windsekirun.rxsociallogin.intenal.utils.clearCookies
import com.github.windsekirun.rxsociallogin.intenal.utils.signInWithCredential
import com.github.windsekirun.rxsociallogin.intenal.utils.toResultObservable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GithubAuthProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import pyxis.uzuki.live.richutilskt.utils.createJSONObject
import pyxis.uzuki.live.richutilskt.utils.getJSONString

class GithubLogin constructor(activity: androidx.fragment.app.FragmentActivity) : BaseSocialLogin(activity) {
    private val auth = FirebaseAuth.getInstance()
    private val config: GithubConfig by lazy { getPlatformConfig(PlatformType.GITHUB) as GithubConfig }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == OAuthConstants.GITHUB_REQUEST_CODE) {
            val jsonStr = data!!.getStringExtra(LoginOAuthActivity.RESPONSE_JSON) ?: "{}"
            analyzeResult(jsonStr)
        } else if (requestCode == OAuthConstants.GITHUB_REQUEST_CODE && resultCode != Activity.RESULT_OK) {
            callbackAsFail(LoginFailedException(EXCEPTION_USER_CANCELLED))
        }
    }

    override fun login() {
        val accessToken = AccessTokenProvider.githubAccessToken
        if (accessToken.isNotEmpty()) {
            checkAccessTokenAvailable(accessToken)
        } else {
            tryLogin()
        }
    }

    override fun logout(clearToken: Boolean) {
        FirebaseAuth.getInstance().signOut()
        clearCookies()

        val accessToken = AccessTokenProvider.githubAccessToken
        if (accessToken.isNotEmpty()) {
            val requestUrl = "https://api.github.com/applications/${config.clientSecret}/tokens/$accessToken"
            val disposable = requestUrl.httpDelete()
                    .authenticate(config.clientId, config.clientSecret)
                    .toResultObservable()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { _, _ ->
                        AccessTokenProvider.githubAccessToken = ""
                    }

            compositeDisposable.add(disposable)
        }
    }

    private fun analyzeResult(jsonStr: String) {
        val jsonObject = jsonStr.createJSONObject()
        val accessToken = jsonObject?.getJSONString("access_token") ?: ""
        if (accessToken.isEmpty()) {
            callbackAsFail(LoginFailedException(EXCEPTION_FAILED_RESULT))
            return
        }

        AccessTokenProvider.githubAccessToken = accessToken
        getUserInfo(accessToken)
    }

    private fun checkAccessTokenAvailable(accessToken: String) {
        val requestUrl = "https://api.github.com/applications/${config.clientId}/tokens/$accessToken"
        val disposable = requestUrl.httpGet()
                .authenticate(config.clientId, config.clientSecret)
                .toResultObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { result, error ->
                    if (error == null && result.component1() != null) {
                        getUserInfo(accessToken)
                    } else {
                        tryLogin()
                    }
                }

        compositeDisposable.add(disposable)
    }

    private fun tryLogin() {
        var authUrl = "${OAuthConstants.GITHUB_URL}?client_id=${config.clientId}"

        if (config.scopeConfig.isNotEmpty()) {
            val scope = config.scopeConfig.joinToString(",")
            authUrl += scope
        }

        val title = config.activityTitle
        val oauthUrl = OAuthConstants.GITHUB_OAUTH
        val parameters = listOf("client_id" to config.clientId,
                "client_secret" to config.clientSecret)
        val map = hashMapOf(*parameters.toTypedArray())

        LoginOAuthActivity.startOAuthActivity(activity, OAuthConstants.GITHUB_REQUEST_CODE, PlatformType.GITHUB,
                authUrl, title, oauthUrl, map)
    }

    private fun getUserInfo(accessToken: String) {
        val credential = GithubAuthProvider.getCredential(accessToken)
        val disposable = auth.signInWithCredential(credential, activity, PlatformType.GITHUB)
                .subscribe({
                    val data = it.apply { this.accessToken = accessToken }
                    callbackAsSuccess(data)
                }, {
                    callbackAsFail(LoginFailedException(EXCEPTION_FAILED_RESULT, it))
                })
        compositeDisposable.add(disposable)
    }
}