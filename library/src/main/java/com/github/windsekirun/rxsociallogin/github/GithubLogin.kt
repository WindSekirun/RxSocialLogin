package com.github.windsekirun.rxsociallogin.github

import android.app.Activity
import android.content.Intent
import com.github.kittinunf.fuel.httpDelete
import com.github.kittinunf.fuel.httpGet
import com.github.windsekirun.rxsociallogin.OAuthConstants
import com.github.windsekirun.rxsociallogin.RxSocialLogin
import com.github.windsekirun.rxsociallogin.intenal.firebase.signInWithCredential
import com.github.windsekirun.rxsociallogin.intenal.fuel.toResultObservable
import com.github.windsekirun.rxsociallogin.intenal.oauth.AccessTokenProvider
import com.github.windsekirun.rxsociallogin.intenal.oauth.BaseOAuthActivity
import com.github.windsekirun.rxsociallogin.intenal.oauth.clearCookies
import com.github.windsekirun.rxsociallogin.model.PlatformType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GithubAuthProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import pyxis.uzuki.live.richutilskt.utils.createJSONObject
import pyxis.uzuki.live.richutilskt.utils.getJSONString

class GithubLogin(activity: Activity) : RxSocialLogin(activity) {
    private val auth = FirebaseAuth.getInstance()
    private val config: GithubConfig by lazy { getPlatformConfig(PlatformType.GITHUB) as GithubConfig }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == OAuthConstants.GITHUB_REQUEST_CODE) {
            val jsonStr = data!!.getStringExtra(BaseOAuthActivity.RESPONSE_JSON) ?: "{}"
            analyzeResult(jsonStr)
        } else if (requestCode == OAuthConstants.GITHUB_REQUEST_CODE && resultCode != Activity.RESULT_OK) {
            callbackFail(PlatformType.GITHUB)
        }
    }

    override fun login() {
        val accessToken = AccessTokenProvider.githubAccessToken
        if (accessToken.isNotEmpty()) {
            checkAccessTokenAvailable(accessToken)
        } else {
            val intent = Intent(activity, GithubOAuthActivity::class.java)
            activity?.startActivityForResult(intent, OAuthConstants.GITHUB_REQUEST_CODE)
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

    fun toObservable() = RxSocialLogin.github(this)

    private fun analyzeResult(jsonStr: String) {
        val jsonObject = jsonStr.createJSONObject()
        val accessToken = jsonObject?.getJSONString("access_token") ?: ""
        if (accessToken.isEmpty()) {
            callbackFail(PlatformType.GITHUB)
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
                        val intent = Intent(activity, GithubOAuthActivity::class.java)
                        activity?.startActivityForResult(intent, OAuthConstants.GITHUB_REQUEST_CODE)
                    }
                }

        compositeDisposable.add(disposable)
    }

    private fun getUserInfo(accessToken: String) {
        val credential = GithubAuthProvider.getCredential(accessToken)
        val disposable = auth.signInWithCredential(credential, activity, PlatformType.GITHUB)
                .subscribe({ callbackItem(it) }, {})
        compositeDisposable.add(disposable)
    }
}