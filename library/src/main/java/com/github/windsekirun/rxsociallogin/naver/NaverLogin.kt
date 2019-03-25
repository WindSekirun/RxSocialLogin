package com.github.windsekirun.rxsociallogin.naver

import android.annotation.SuppressLint
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import com.github.kittinunf.fuel.httpGet
import com.github.windsekirun.rxsociallogin.RxSocialLogin
import com.github.windsekirun.rxsociallogin.base.BaseSocialLogin
import com.github.windsekirun.rxsociallogin.intenal.exception.LoginFailedException
import com.github.windsekirun.rxsociallogin.intenal.model.LoginResultItem
import com.github.windsekirun.rxsociallogin.intenal.model.PlatformType
import com.github.windsekirun.rxsociallogin.intenal.utils.toResultObservable
import com.nhn.android.naverlogin.OAuthLogin
import com.nhn.android.naverlogin.OAuthLoginDefine
import com.nhn.android.naverlogin.OAuthLoginHandler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import pyxis.uzuki.live.richutilskt.utils.createJSONObject
import pyxis.uzuki.live.richutilskt.utils.getJSONObject
import pyxis.uzuki.live.richutilskt.utils.getJSONString

class NaverLogin constructor(activity: FragmentActivity) : BaseSocialLogin<NaverConfig>(activity) {
    override fun getPlatformType(): PlatformType = PlatformType.NAVER
    private val requestUrl = "https://openapi.naver.com/v1/nid/me"
    private val authLogin = OAuthLogin.getInstance()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

    }

    override fun login() {
        OAuthLoginDefine.MARKET_LINK_WORKING = false
        authLogin.init(activity, config.authClientId, config.authClientSecret, config.clientName)
        authLogin.startOauthLoginActivity(activity, NaverLoginHandler())
    }

    override fun logout(clearToken: Boolean) {
        if (clearToken) {
            OAuthLogin.getInstance().logoutAndDeleteToken(activity)
        } else {
            OAuthLogin.getInstance().logout(activity)
        }
    }

    private fun requestProfile(authHeader: String, accessToken: String) {
        val disposable = requestUrl.httpGet()
                .header("Authorization" to authHeader)
                .toResultObservable()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { result, error ->
                    if (error == null && result.component1() != null) {
                        parseUserInfo(result.component1(), accessToken)
                    } else {
                        callbackAsFail(LoginFailedException(RxSocialLogin.EXCEPTION_FAILED_RESULT, error))
                    }
                }

        compositeDisposable.add(disposable)
    }

    private fun parseUserInfo(jsonStr: String?, accessToken: String) {
        val jsonObject = jsonStr?.createJSONObject()
        val responseObject = getJSONObject(jsonObject, "response")
        if (responseObject == null) {
            callbackAsFail(LoginFailedException(RxSocialLogin.EXCEPTION_FAILED_RESULT))
            return
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
            this.accessToken = accessToken
            this.platform = PlatformType.NAVER
            this.result = true
        }

        callbackAsSuccess(item)
    }

    @SuppressLint("HandlerLeak")
    private inner class NaverLoginHandler : OAuthLoginHandler() {
        override fun run(success: Boolean) {
            if (success) {
                val accessToken = authLogin.getAccessToken(activity)
                val authHeader = "Bearer $accessToken"
                requestProfile(authHeader, accessToken)
            } else {
                callbackAsFail(LoginFailedException(RxSocialLogin.EXCEPTION_FAILED_RESULT))
            }
        }
    }

}
