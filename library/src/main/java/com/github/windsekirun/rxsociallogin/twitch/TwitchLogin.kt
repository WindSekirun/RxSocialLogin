package com.github.windsekirun.rxsociallogin.twitch

import androidx.fragment.app.FragmentActivity
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.windsekirun.rxsociallogin.OAuthConstants
import com.github.windsekirun.rxsociallogin.RxSocialLogin.EXCEPTION_FAILED_RESULT
import com.github.windsekirun.rxsociallogin.base.BaseOAuthSocialLogin
import com.github.windsekirun.rxsociallogin.intenal.exception.LoginFailedException
import com.github.windsekirun.rxsociallogin.intenal.model.LoginResultItem
import com.github.windsekirun.rxsociallogin.intenal.model.PlatformType
import com.github.windsekirun.rxsociallogin.intenal.oauth.AccessTokenProvider
import com.github.windsekirun.rxsociallogin.intenal.utils.toResultObservable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import pyxis.uzuki.live.richutilskt.utils.createJSONObject
import pyxis.uzuki.live.richutilskt.utils.getJSONString
import pyxis.uzuki.live.richutilskt.utils.isEmpty

class TwitchLogin constructor(activity: FragmentActivity) : BaseOAuthSocialLogin<TwitchConfig>(activity) {
    override fun getOAuthUrl(): String = OAuthConstants.TWITCH_OAUTH
    override fun getPlatformType(): PlatformType = PlatformType.TWITCH
    override fun getRequestCode(): Int = OAuthConstants.TWITCH_REQUEST_CODE

    override fun getAuthUrl(): String {
        var authUrl = "${OAuthConstants.TWITCH_URL}?client_id=${config.clientId}&" +
                "response_type=code&redirect_uri=${config.redirectUri}&state=${getState()}&scope=user:edit"

        if (config.requireEmail) {
            authUrl += "+user:read:email"
        }

        return authUrl
    }

    override fun logout(clearToken: Boolean) {
        super.logout(clearToken)

        val accessToken = AccessTokenProvider.twitchAccessToken
        if (accessToken.isNotEmpty()) {
            val requestUrl = "https://id.twitch.tv/oauth2/revoke?client_id=${config.clientId}" +
                    "&token=$accessToken"
            val disposable = requestUrl.httpPost()
                    .toResultObservable()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { _, _ ->
                        AccessTokenProvider.twitchAccessToken = ""
                    }

            compositeDisposable.add(disposable)
        }
    }

    override fun analyzeResult(jsonStr: String) {
        val accessTokenObject = jsonStr.createJSONObject()
        if (accessTokenObject == null) {
            callbackAsFail(LoginFailedException(EXCEPTION_FAILED_RESULT))
            return
        }

        val accessToken = accessTokenObject.getJSONString("access_token", "")
        if (accessToken.isEmpty()) {
            callbackAsFail(LoginFailedException(EXCEPTION_FAILED_RESULT))
            return
        }

        AccessTokenProvider.twitchAccessToken = accessToken

        val requestUrl = "https://api.twitch.tv/helix/users"
        val authorization = "Bearer $accessToken"

        val disposable = requestUrl.httpGet()
                .header("Authorization" to authorization)
                .toResultObservable()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { result, error ->
                    if (error == null && result.component1() != null) {
                        parseUserInfo(result.component1(), accessToken)
                    } else {
                        callbackAsFail(LoginFailedException(EXCEPTION_FAILED_RESULT, error))
                    }
                }

        compositeDisposable.add(disposable)
    }

    private fun parseUserInfo(jsonStr: String?, accessToken: String) {
        val jsonObject = jsonStr?.createJSONObject()
        val responseArray = jsonObject?.getJSONArray("data")
        if (responseArray == null) {
            callbackAsFail(LoginFailedException(EXCEPTION_FAILED_RESULT))
            return
        }
        val responseObject = responseArray.getJSONObject(0)
        if (responseObject == null) {
            callbackAsFail(LoginFailedException(EXCEPTION_FAILED_RESULT))
            return
        }

        val item = LoginResultItem().apply {
            this.result = true
            this.platform = PlatformType.TWITCH
            this.id = responseObject.getJSONString("id")
            this.name = responseObject.getJSONString("display_name")
            this.email = responseObject.getJSONString("email", "")
            this.profilePicture = responseObject.getJSONString("profile_image_url", "")
            this.accessToken = accessToken
        }

        callbackAsSuccess(item)
    }
}