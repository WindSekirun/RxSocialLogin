package com.github.windsekirun.rxsociallogin.instagram

import androidx.fragment.app.FragmentActivity
import com.github.windsekirun.rxsociallogin.OAuthConstants
import com.github.windsekirun.rxsociallogin.RxSocialLogin
import com.github.windsekirun.rxsociallogin.base.BaseOAuthSocialLogin
import com.github.windsekirun.rxsociallogin.intenal.exception.LoginFailedException
import com.github.windsekirun.rxsociallogin.intenal.model.LoginResultItem
import com.github.windsekirun.rxsociallogin.intenal.model.PlatformType
import pyxis.uzuki.live.richutilskt.utils.createJSONObject
import pyxis.uzuki.live.richutilskt.utils.getJSONString
import pyxis.uzuki.live.richutilskt.utils.isEmpty

class InstagramLogin constructor(activity: FragmentActivity) : BaseOAuthSocialLogin<InstagramConfig>(activity) {
    override fun getAuthUrl(): String = "${OAuthConstants.INSTAGRAM_URL}?client_id=${config.clientId}" +
            "&redirect_uri=${config.redirectUri}&response_type=code"

    override fun getPlatformType(): PlatformType = PlatformType.INSTAGRAM
    override fun getRequestCode(): Int = OAuthConstants.INSTAGRAM_REQUEST_CODE
    override fun getOAuthUrl(): String = OAuthConstants.INSTAGRAM_OAUTH

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

        val userObject = accessTokenResult.getJSONObject("user")
        if (userObject == null) {
            callbackAsFail(LoginFailedException(RxSocialLogin.EXCEPTION_FAILED_RESULT))
            return
        }

        val item = LoginResultItem().apply {
            this.id = userObject.getJSONString("id")
            this.name = userObject.getJSONString("username")
            this.profilePicture = userObject.getJSONString("profile_picture")
            this.accessToken = accessToken
            this.platform = PlatformType.INSTAGRAM
            this.result = true
        }

        callbackAsSuccess(item)
    }
}