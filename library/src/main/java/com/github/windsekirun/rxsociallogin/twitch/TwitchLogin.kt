package com.github.windsekirun.rxsociallogin.twitch

import android.app.Activity
import android.content.Intent
import android.support.v4.app.FragmentActivity
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.windsekirun.rxsociallogin.OAuthConstants
import com.github.windsekirun.rxsociallogin.RxSocialLogin
import com.github.windsekirun.rxsociallogin.intenal.fuel.toResultObservable
import com.github.windsekirun.rxsociallogin.intenal.oauth.AccessTokenProvider
import com.github.windsekirun.rxsociallogin.intenal.oauth.BaseOAuthActivity
import com.github.windsekirun.rxsociallogin.intenal.model.LoginResultItem
import com.github.windsekirun.rxsociallogin.intenal.model.PlatformType
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import pyxis.uzuki.live.richutilskt.utils.createJSONObject
import pyxis.uzuki.live.richutilskt.utils.getJSONString
import pyxis.uzuki.live.richutilskt.utils.isEmpty

class TwitchLogin @JvmOverloads constructor (activity: FragmentActivity? = null) : RxSocialLogin(activity) {
    private val config: TwitchConfig by lazy { getPlatformConfig(PlatformType.TWITCH) as TwitchConfig }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == OAuthConstants.TWITCH_REQUEST_CODE) {
            val jsonStr = data!!.getStringExtra(BaseOAuthActivity.RESPONSE_JSON) ?: "{}"
            analyzeResult(jsonStr)
        } else if (requestCode == OAuthConstants.TWITCH_REQUEST_CODE && resultCode != Activity.RESULT_OK) {
            callbackFail(PlatformType.TWITCH)
        }
    }

    override fun login() {
        addWeakMap(PlatformType.TWITCH, this)
        val intent = Intent(activity, TwitchOAuthActivity::class.java)
        activity?.startActivityForResult(intent, OAuthConstants.TWITCH_REQUEST_CODE)
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

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated("use RxSocialLogin.result instead")
    fun toObservable() = RxSocialLogin.twitch(this)

    private fun analyzeResult(jsonStr: String) {
        val accessTokenObject = jsonStr.createJSONObject()
        if (accessTokenObject == null) {
            callbackFail(PlatformType.TWITCH)
            return
        }

        val accessToken = accessTokenObject.getJSONString("access_token", "")
        if (accessToken.isEmpty()) {
            callbackFail(PlatformType.TWITCH)
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
                        parseUserInfo(result.component1())
                    } else {
                        callbackFail(PlatformType.TWITCH)
                    }
                }

        compositeDisposable.add(disposable)
    }

    private fun parseUserInfo(jsonStr: String?) {
        val jsonObject = jsonStr?.createJSONObject()
        val responseArray = jsonObject?.getJSONArray("data")

        if (responseArray == null) {
            callbackFail(PlatformType.TWITCH)
            return
        }

        val responseObject = responseArray.getJSONObject(0)

        if (responseObject == null) {
            callbackFail(PlatformType.TWITCH)
            return
        }

        val item = LoginResultItem().apply {
            this.result = true
            this.platform = PlatformType.TWITCH
            this.id = responseObject.getJSONString("id")
            this.name = responseObject.getJSONString("display_name")
            this.email = responseObject.getJSONString("email", "")
            this.profilePicture = responseObject.getJSONString("profile_image_url", "")
        }

        callbackItem(item)
    }
}