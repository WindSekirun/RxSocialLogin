package com.github.windsekirun.rxsociallogin.discord

import androidx.fragment.app.FragmentActivity
import com.github.kittinunf.fuel.httpGet
import com.github.windsekirun.rxsociallogin.OAuthConstants
import com.github.windsekirun.rxsociallogin.RxSocialLogin
import com.github.windsekirun.rxsociallogin.RxSocialLogin.getPlatformConfig
import com.github.windsekirun.rxsociallogin.base.BaseOAuthSocialLogin
import com.github.windsekirun.rxsociallogin.intenal.exception.LoginFailedException
import com.github.windsekirun.rxsociallogin.intenal.model.LoginResultItem
import com.github.windsekirun.rxsociallogin.intenal.model.PlatformType
import com.github.windsekirun.rxsociallogin.intenal.oauth.LoginOAuthActivity
import com.github.windsekirun.rxsociallogin.intenal.utils.clearCookies
import com.github.windsekirun.rxsociallogin.intenal.utils.randomString
import com.github.windsekirun.rxsociallogin.intenal.utils.toResultObservable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import pyxis.uzuki.live.richutilskt.utils.createJSONObject
import pyxis.uzuki.live.richutilskt.utils.getJSONString
import pyxis.uzuki.live.richutilskt.utils.isEmpty

class DiscordLogin constructor(activity: FragmentActivity) : BaseOAuthSocialLogin<DiscordConfig>(activity) {
    override fun getAuthUrl(): String = "${OAuthConstants.DISCORD_URL}?response_type=code&client_id=${config.clientId}" +
            "&scope=identify%20email&state=${getState()}&redirect_uri=${config.redirectUri}"
    override fun getPlatformType(): PlatformType = PlatformType.DISCORD
    override fun getRequestCode(): Int = OAuthConstants.DISCORD_REQUEST_CODE
    override fun getOAuthUrl(): String = OAuthConstants.DISCORD_OAUTH

    override fun analyzeResult(jsonStr: String) {
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

        val requestUrl = "https://discordapp.com/api/v6/users/@me"
        val disposable = requestUrl.httpGet()
                .header("Content-Type" to "application/json")
                .header("Authorization" to "Bearer $accessToken")
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
        if (jsonObject == null) {
            callbackAsFail(LoginFailedException(RxSocialLogin.EXCEPTION_FAILED_RESULT))
            return
        }

        val avatar = jsonObject.getJSONString("avatar")
        val id = jsonObject.getJSONString("id")
        val profilePicture = "https://cdn.discordapp.com/avatars/$id/$avatar.png?size=512"

        val item = LoginResultItem().apply {
            this.id = id
            this.name = jsonObject.getJSONString("name")
            this.email = jsonObject.getJSONString("email")
            this.nickname = jsonObject.getJSONString("username")
            this.profilePicture = profilePicture
            this.accessToken = accessToken
            this.platform = PlatformType.DISCORD
            this.result = true
        }

        callbackAsSuccess(item)
    }
}
