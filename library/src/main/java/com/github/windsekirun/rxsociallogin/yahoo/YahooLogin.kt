package com.github.windsekirun.rxsociallogin.yahoo

import android.app.Activity
import android.content.Intent
import android.support.v4.app.FragmentActivity
import android.util.Base64
import com.github.windsekirun.rxsociallogin.BaseSocialLogin
import com.github.windsekirun.rxsociallogin.intenal.oauth.LoginOAuthActivity
import com.github.windsekirun.rxsociallogin.OAuthConstants
import com.github.windsekirun.rxsociallogin.RxSocialLogin
import com.github.windsekirun.rxsociallogin.intenal.model.LoginResultItem
import com.github.windsekirun.rxsociallogin.intenal.model.PlatformType
import com.github.windsekirun.rxsociallogin.intenal.utils.randomString
import pyxis.uzuki.live.richutilskt.utils.createJSONObject
import pyxis.uzuki.live.richutilskt.utils.getJSONString

class YahooLogin @JvmOverloads constructor(activity: FragmentActivity? = null) : BaseSocialLogin(activity) {
    private val config: YahooConfig by lazy { RxSocialLogin.getPlatformConfig(PlatformType.YAHOO) as YahooConfig }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == OAuthConstants.YAHOO_REQUEST_CODE) {
            val jsonStr = data!!.getStringExtra(LoginOAuthActivity.RESPONSE_JSON) ?: "{}"
            analyzeResult(jsonStr)
        } else if (requestCode == OAuthConstants.YAHOO_REQUEST_CODE && resultCode != Activity.RESULT_OK) {
            callbackFail(PlatformType.YAHOO)
        }
    }

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

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated("use RxSocialLogin.result instead")
    fun toObservable() = RxSocialLogin.yahoo(this)

    private fun analyzeResult(jsonStr: String) {
        val jsonObject = jsonStr.createJSONObject()
        val idToken = jsonObject?.getJSONString("id_token") ?: ""
        val guid = jsonObject?.getJSONString("xoauth_yahoo_guid") ?: ""
        if (guid.isEmpty() || idToken.isEmpty()) {
            callbackFail(PlatformType.YAHOO)
            return
        }

        // decode idToken by https://developer.yahoo.com/oauth2/guide/openid_connect/decode_id_token.html
        val array = idToken.split(".")
        val decodedStr = String(Base64.decode(array[1], Base64.DEFAULT))
        val response = decodedStr.createJSONObject()

        if (response == null) {
            callbackFail(PlatformType.YAHOO)
            return
        }

        val name = response.getJSONString("name")

        val item = LoginResultItem().apply {
            this.id = guid
            this.name = name
            this.result = true
            this.platform = PlatformType.YAHOO
        }

        callbackItem(item)
    }
}
