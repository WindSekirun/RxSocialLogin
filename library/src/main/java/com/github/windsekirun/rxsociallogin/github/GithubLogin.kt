package com.github.windsekirun.rxsociallogin.github

import androidx.fragment.app.FragmentActivity
import com.github.kittinunf.fuel.httpDelete
import com.github.kittinunf.fuel.httpGet
import com.github.windsekirun.rxsociallogin.OAuthConstants
import com.github.windsekirun.rxsociallogin.RxSocialLogin.EXCEPTION_FAILED_RESULT
import com.github.windsekirun.rxsociallogin.base.BaseOAuthSocialLogin
import com.github.windsekirun.rxsociallogin.intenal.exception.LoginFailedException
import com.github.windsekirun.rxsociallogin.intenal.model.PlatformType
import com.github.windsekirun.rxsociallogin.intenal.oauth.AccessTokenProvider
import com.github.windsekirun.rxsociallogin.intenal.utils.signInWithCredential
import com.github.windsekirun.rxsociallogin.intenal.utils.toResultObservable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GithubAuthProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import pyxis.uzuki.live.richutilskt.utils.createJSONObject
import pyxis.uzuki.live.richutilskt.utils.getJSONString

class GithubLogin constructor(activity: FragmentActivity) : BaseOAuthSocialLogin<GithubConfig>(activity) {
    override fun getOAuthUrl(): String = OAuthConstants.GITHUB_OAUTH
    override fun getPlatformType(): PlatformType = PlatformType.GITHUB
    override fun getRequestCode(): Int = OAuthConstants.GITHUB_REQUEST_CODE

    private val auth = FirebaseAuth.getInstance()

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

    override fun analyzeResult(jsonStr: String) {
        val jsonObject = jsonStr.createJSONObject()
        val accessToken = jsonObject?.getJSONString("access_token") ?: ""
        if (accessToken.isEmpty()) {
            callbackAsFail(LoginFailedException(EXCEPTION_FAILED_RESULT))
            return
        }

        AccessTokenProvider.githubAccessToken = accessToken
        getUserInfo(accessToken)
    }

    override fun getAuthUrl(): String {
        var authUrl = "${OAuthConstants.GITHUB_URL}?client_id=${config.clientId}"

        if (config.scopeConfig.isNotEmpty()) {
            val scope = config.scopeConfig.joinToString(",")
            authUrl += scope
        }

        return authUrl
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