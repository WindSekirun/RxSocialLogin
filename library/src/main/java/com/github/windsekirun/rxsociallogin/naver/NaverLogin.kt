package com.github.windsekirun.rxsociallogin.naver

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import com.github.windsekirun.rxsociallogin.SocialLogin
import com.github.windsekirun.rxsociallogin.intenal.OkHttpHelper
import com.github.windsekirun.rxsociallogin.model.LoginResultItem
import com.github.windsekirun.rxsociallogin.model.SocialType
import com.nhn.android.naverlogin.OAuthLogin
import com.nhn.android.naverlogin.OAuthLoginDefine
import com.nhn.android.naverlogin.OAuthLoginHandler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import pyxis.uzuki.live.richutilskt.utils.createJSONObject
import pyxis.uzuki.live.richutilskt.utils.getJSONObject
import pyxis.uzuki.live.richutilskt.utils.getJSONString

class NaverLogin(activity: Activity) : SocialLogin(activity) {
    private val compositeDisposable = CompositeDisposable()
    private val requestUrl = "https://openapi.naver.com/v1/nid/me"

    private val authLogin = OAuthLogin.getInstance()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

    }

    override fun onLogin() {
        OAuthLoginDefine.MARKET_LINK_WORKING = false

        val config = getConfig(SocialType.NAVER) as NaverConfig
        authLogin.init(activity, config.authClientId, config.authClientSecret, config.clientName)
        authLogin.startOauthLoginActivity(activity, NaverLoginHandler())
    }

    override fun onDestroy() {
        compositeDisposable.clear()
    }

    override fun logout(clearToken: Boolean) {
        if (clearToken) {
            OAuthLogin.getInstance().logoutAndDeleteToken(activity)
        } else {
            OAuthLogin.getInstance().logout(activity)
        }
    }

    @SuppressLint("HandlerLeak")
    private inner class NaverLoginHandler : OAuthLoginHandler() {

        override fun run(success: Boolean) {
            if (success) {
                val accessToken = authLogin.getAccessToken(activity)
                val authHeader = "Bearer $accessToken"
                requestProfile(authHeader)
            }
        }
    }

    private fun requestProfile(authHeader: String) {
        val disposable = requestUrl.toRequest(authHeader)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val jsonObject = it.createJSONObject()
                    val responseObject = getJSONObject(jsonObject, "response")

                    if (responseObject == null) {
                        responseFail(SocialType.NAVER)
                        return@subscribe
                    }

                    val item = LoginResultItem().apply {
                        this.id = responseObject.getJSONString("id")
                        this.name = responseObject.getJSONString("name")
                        this.email = responseObject.getJSONString("email")
                        this.nickname = responseObject.getJSONString("nickname")
                        this.gender = responseObject.getJSONString("gender")
                        this.age = responseObject.getJSONString("age")
                        this.birthday = responseObject.getJSONString("birthday")
                        this.profilePicture = responseObject.getJSONString("profile_image")
                        this.type = SocialType.NAVER
                        this.result = true
                    }

                    responseSuccess(item)
                }) {
                    responseFail(SocialType.NAVER)
                }

        compositeDisposable.add(disposable)
    }

    private fun String.toRequest(authorization: String): Single<String> {
        return OkHttpHelper.get(this, "Authorization", authorization)
    }
}
