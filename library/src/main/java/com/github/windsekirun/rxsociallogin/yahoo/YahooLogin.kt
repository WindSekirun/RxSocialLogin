package com.github.windsekirun.rxsociallogin.yahoo

import android.util.Base64
import androidx.fragment.app.FragmentActivity
import com.github.windsekirun.rxsociallogin.OAuthConstants
import com.github.windsekirun.rxsociallogin.RxSocialLogin
import com.github.windsekirun.rxsociallogin.base.BaseOAuthSocialLogin
import com.github.windsekirun.rxsociallogin.intenal.exception.LoginFailedException
import com.github.windsekirun.rxsociallogin.intenal.model.LoginResultItem
import com.github.windsekirun.rxsociallogin.intenal.model.PlatformType
import pyxis.uzuki.live.richutilskt.utils.createJSONObject
import pyxis.uzuki.live.richutilskt.utils.getJSONString

class YahooLogin constructor(activity: FragmentActivity) : BaseOAuthSocialLogin<YahooConfig>(activity) {
    override fun getAuthUrl(): String = "${OAuthConstants.YAHOO_URL}?client_id=${config.clientId}&" +
            "redirect_uri=${config.redirectUri}&response_type=code&scope=openid%20sdps-r&nonce=${getState()}"

    override fun getOAuthUrl(): String = OAuthConstants.YAHOO_OAUTH
    override fun getRequestCode(): Int = OAuthConstants.YAHOO_REQUEST_CODE
    override fun getPlatformType(): PlatformType = PlatformType.YAHOO

    override fun getBasicToken(): String {
        val token = "${config.clientId}:${config.clientSecret}"
        return String(Base64.encode(token.toByteArray(), Base64.URL_SAFE)).replace("\n", "")
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
