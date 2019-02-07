package com.github.windsekirun.rxsociallogin.yahoo

import android.app.Activity
import android.content.Intent
import android.util.Base64
import androidx.fragment.app.FragmentActivity
import com.github.windsekirun.rxsociallogin.OAuthConstants
import com.github.windsekirun.rxsociallogin.RxSocialLogin
import com.github.windsekirun.rxsociallogin.base.BaseOAuthSocialLogin
import com.github.windsekirun.rxsociallogin.intenal.exception.LoginFailedException
import com.github.windsekirun.rxsociallogin.intenal.model.LoginResultItem
import com.github.windsekirun.rxsociallogin.intenal.model.PlatformType
import com.github.windsekirun.rxsociallogin.intenal.oauth.LoginOAuthActivity
import com.github.windsekirun.rxsociallogin.intenal.utils.randomString
import pyxis.uzuki.live.richutilskt.utils.createJSONObject
import pyxis.uzuki.live.richutilskt.utils.getJSONString

class YahooLogin constructor(activity: FragmentActivity) : BaseOAuthSocialLogin<YahooConfig>(activity) {
    override fun getRequestCode(): Int = OAuthConstants.YAHOO_REQUEST_CODE
    override fun getPlatformType(): PlatformType = PlatformType.YAHOO

    override fun login() {
        val nonce = randomString(21)

        val authUrl = "${OAuthConstants.YAHOO_URL}?client_id=${config.clientId}&" +
                "redirect_uri=${config.redirectUri}&response_type=code&scope=openid%20sdps-r&nonce=$nonce"

        val title = config.activityTitle
        val oauthUrl = OAuthConstants.YAHOO_OAUTH
        val parameters = listOf(
                "grant_type" to "authorization_code",
                "redirect_uri" to config.redirectUri,
                "client_id" to config.clientId,
                "client_secret" to config.clientSecret)
        val map = hashMapOf(*parameters.toTypedArray())
        val token = "${config.clientId}:${config.clientSecret}"
        val basicToken = String(Base64.encode(token.toByteArray(), Base64.URL_SAFE))
                .replace("\n", "")

        LoginOAuthActivity.startOAuthActivity(activity, OAuthConstants.YAHOO_REQUEST_CODE,
                PlatformType.YAHOO, authUrl, title, oauthUrl, map, basicToken)
    }

    override fun analyzeResult(jsonStr: String) {
        val jsonObject = jsonStr.createJSONObject()
        val idToken = jsonObject?.getJSONString("id_token") ?: ""
        val guid = jsonObject?.getJSONString("xoauth_yahoo_guid") ?: ""
        if (guid.isEmpty() || idToken.isEmpty()) {
            callbackAsFail(LoginFailedException(RxSocialLogin.EXCEPTION_FAILED_RESULT))
            return
        }

        // decode idToken by https://developer.yahoo.com/oauth2/guide/openid_connect/decode_id_token.html
        val array = idToken.split(".")
        val decodedStr = String(Base64.decode(array[1], Base64.DEFAULT))
        val response = decodedStr.createJSONObject()
        if (response == null) {
            callbackAsFail(LoginFailedException(RxSocialLogin.EXCEPTION_FAILED_RESULT))
            return
        }

        val name = response.getJSONString("name")

        val item = LoginResultItem().apply {
            this.id = guid
            this.name = name
            this.result = true
            this.platform = PlatformType.YAHOO
        }

        callbackAsSuccess(item)
    }
}
