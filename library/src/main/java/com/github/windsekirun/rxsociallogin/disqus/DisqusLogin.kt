package com.github.windsekirun.rxsociallogin.disqus

import android.app.Activity
import android.content.Intent
import android.support.v4.app.FragmentActivity
import com.github.kittinunf.fuel.httpGet
import com.github.windsekirun.rxsociallogin.BaseSocialLogin
import com.github.windsekirun.rxsociallogin.OAuthConstants
import com.github.windsekirun.rxsociallogin.RxSocialLogin
import com.github.windsekirun.rxsociallogin.RxSocialLogin.getPlatformConfig
import com.github.windsekirun.rxsociallogin.intenal.exception.LoginFailedException
import com.github.windsekirun.rxsociallogin.intenal.model.LoginResultItem
import com.github.windsekirun.rxsociallogin.intenal.model.PlatformType
import com.github.windsekirun.rxsociallogin.intenal.oauth.LoginOAuthActivity
import com.github.windsekirun.rxsociallogin.intenal.utils.clearCookies
import com.github.windsekirun.rxsociallogin.intenal.utils.toResultObservable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import pyxis.uzuki.live.richutilskt.utils.createJSONObject
import pyxis.uzuki.live.richutilskt.utils.getJSONString
import pyxis.uzuki.live.richutilskt.utils.isEmpty

class DisqusLogin constructor(activity: FragmentActivity) : BaseSocialLogin(activity) {
    private val config: DisqusConfig by lazy { getPlatformConfig(PlatformType.DISQUS) as DisqusConfig }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == OAuthConstants.DISQUS_REQUEST_CODE) {
            val jsonStr = data!!.getStringExtra(LoginOAuthActivity.RESPONSE_JSON) ?: "{}"
            analyzeResult(jsonStr)
        } else if (requestCode == OAuthConstants.DISQUS_REQUEST_CODE && resultCode != Activity.RESULT_OK) {
            callbackAsFail(LoginFailedException(RxSocialLogin.EXCEPTION_USER_CANCELLED))
        }
    }

    override fun login() {
        val authUrl = "${OAuthConstants.DISQUS_URL}?client_id=${config.clientId}&" +
                "scope=read&response_type=code&redirect_uri=${config.redirectUri}"
        val oauthUrl = OAuthConstants.DISQUS_OAUTH
        val parameters = listOf(
                "redirect_uri" to config.redirectUri,
                "client_id" to config.clientId,
                "client_secret" to config.clientSecret,
                "grant_type" to "authorization_code")
        val title = config.activityTitle

        val map = hashMapOf(*parameters.toTypedArray())

        LoginOAuthActivity.startOAuthActivity(activity, OAuthConstants.DISQUS_REQUEST_CODE,
                PlatformType.DISQUS, authUrl, title, oauthUrl, map)
    }

    override fun logout(clearToken: Boolean) {
        super.logout(clearToken)
        clearCookies()
    }

    private fun analyzeResult(jsonStr: String) {
        val accessTokenResult = jsonStr.createJSONObject()

        if (accessTokenResult == null) {
            callbackAsFail(LoginFailedException(RxSocialLogin.EXCEPTION_FAILED_RESULT))
            return
        }

        val accessToken = accessTokenResult.getJSONString("access_token", "")
        if (accessToken.isEmpty()) {
            callbackAsFail(LoginFailedException(RxSocialLogin.EXCEPTION_FAILED_RESULT))
            return
        }

        val requestUrl = "https://disqus.com/api/3.0/users/details.json" +
                "?access_token=$accessToken&api_key=${config.clientId}&api_secret=${config.clientSecret}"

        val disposable = requestUrl.httpGet()
                .header("Content-Type" to "application/json")
                .toResultObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { result, error ->
                    if (error == null && result.component1() != null) {
                        parseUserJson(result.component1(), accessToken)
                    } else {
                        callbackAsFail(LoginFailedException(RxSocialLogin.EXCEPTION_FAILED_RESULT, error))
                    }
                }

        compositeDisposable.add(disposable)
    }

    private fun parseUserJson(jsonStr: String?, accessToken: String) {
        val jsonObject = jsonStr?.createJSONObject()
        val responseObject = jsonObject?.getJSONObject("response")
        if (responseObject == null) {
            callbackAsFail(LoginFailedException(RxSocialLogin.EXCEPTION_FAILED_RESULT))
            return
        }

        val avatarObject = responseObject.getJSONObject("avatar")
        val profilePicture = avatarObject?.getJSONString("permalink") ?: ""

        val item = LoginResultItem().apply {
            this.id = responseObject.getJSONString("id")
            this.name = responseObject.getJSONString("name")
            this.email = responseObject.getJSONString("email")
            this.nickname = responseObject.getJSONString("username")
            this.profilePicture = profilePicture
            this.accessToken = accessToken
            this.platform = PlatformType.DISQUS
            this.result = true
        }

        callbackAsSuccess(item)
    }
}
