package com.github.windsekirun.rxsociallogin.github

import android.app.Activity
import android.content.Intent
import com.github.windsekirun.rxsociallogin.OAuthConstants
import com.github.windsekirun.rxsociallogin.RxSocialLogin
import com.github.windsekirun.rxsociallogin.SocialLogin
import com.github.windsekirun.rxsociallogin.intenal.firebase.signInWithCredential
import com.github.windsekirun.rxsociallogin.intenal.net.OkHttpHelper
import com.github.windsekirun.rxsociallogin.intenal.oauth.AccessTokenProvider
import com.github.windsekirun.rxsociallogin.intenal.oauth.BaseOAuthActivity
import com.github.windsekirun.rxsociallogin.intenal.oauth.clearCookies
import com.github.windsekirun.rxsociallogin.model.PlatformType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GithubAuthProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import pyxis.uzuki.live.richutilskt.utils.createJSONObject
import pyxis.uzuki.live.richutilskt.utils.getJSONString

class GithubLogin(activity: Activity) : SocialLogin(activity) {
    private val auth = FirebaseAuth.getInstance()
    private val compositeDisposable = CompositeDisposable()
    private val config: GithubConfig by lazy { getConfig(PlatformType.GITHUB) as GithubConfig }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == OAuthConstants.GITHUB_REQUEST_CODE) {
            val jsonStr = data!!.getStringExtra(BaseOAuthActivity.RESPONSE_JSON) ?: "{}"
            analyzeResult(jsonStr)
        } else if (requestCode == OAuthConstants.GITHUB_REQUEST_CODE && resultCode != Activity.RESULT_OK) {
            responseFail(PlatformType.GITHUB)
        }
    }

    override fun onLogin() {
        val accessToken = AccessTokenProvider.githubAccessToken
        if (accessToken.isNotEmpty()) {
//            checkAccessTokenAvailable(accessToken)
        } else {
            val intent = Intent(activity, GithubOAuthActivity::class.java)
            activity?.startActivityForResult(intent, OAuthConstants.GITHUB_REQUEST_CODE)
        }
    }

    override fun onDestroy() {
        compositeDisposable.clear()
    }

    override fun logout(clearToken: Boolean) {
        FirebaseAuth.getInstance().signOut()
        clearCookies()
        AccessTokenProvider.githubAccessToken = ""
    }

    fun toObservable() = RxSocialLogin.github(this)

    private fun analyzeResult(jsonStr: String) {
        val jsonObject = jsonStr.createJSONObject()
        val accessToken = jsonObject?.getJSONString("access_token") ?: ""
        if (accessToken.isEmpty()) {
            responseFail(PlatformType.GITHUB)
            return
        }

        AccessTokenProvider.githubAccessToken = accessToken
        getUserInfo(accessToken)
    }

    private fun checkAccessTokenAvailable(accessToken: String) {
//        val disposable = OkHttpHelper.get("https://api.github.com/${config.clientId}/tokens/$accessToken", )
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe({
//                    getUserInfo(accessToken)
//                }, {
//                    val intent = Intent(activity, GithubOAuthActivity::class.java)
//                    activity?.startActivityForResult(intent, OAuthConstants.GITHUB_REQUEST_CODE)
//                })
//
//        compositeDisposable.add(disposable)
    }

    private fun getUserInfo(accessToken: String) {
        val credential = GithubAuthProvider.getCredential(accessToken)
        val disposable = auth.signInWithCredential(credential, activity, PlatformType.GITHUB)
                .subscribe({ responseSuccess(it) }, {})
        compositeDisposable.add(disposable)
    }
}