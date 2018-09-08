package com.github.windsekirun.rxsociallogin.yahoo

import android.app.Activity
import android.content.Intent
import android.util.Base64
import com.github.windsekirun.rxsociallogin.SocialLogin
import com.github.windsekirun.rxsociallogin.intenal.oauth.BaseOAuthActivity
import com.github.windsekirun.rxsociallogin.model.LoginResultItem
import com.github.windsekirun.rxsociallogin.model.PlatformType
import pyxis.uzuki.live.richutilskt.utils.createJSONObject
import pyxis.uzuki.live.richutilskt.utils.getJSONString

class YahooLogin(activity: Activity) : SocialLogin(activity) {

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == YahooOAuthConstants.YAHOO_REQUEST_CODE) {
            val jsonStr = data!!.getStringExtra(BaseOAuthActivity.RESPONSE_JSON) ?: "{}"
            analyzeResult(jsonStr)
        } else if (resultCode != Activity.RESULT_OK) {
            responseFail(PlatformType.YAHOO)
        }
    }

    override fun onLogin() {
        val intent = Intent(activity, YahooOAuthActivity::class.java)
        activity?.startActivityForResult(intent, YahooOAuthConstants.YAHOO_REQUEST_CODE)
    }

    override fun onDestroy() {

    }

    private fun analyzeResult(jsonStr: String) {
        val jsonObject = jsonStr.createJSONObject()
        val idToken = jsonObject?.getJSONString("id_token") ?: ""
        val guid = jsonObject?.getJSONString("xoauth_yahoo_guid") ?: ""
        if (guid.isEmpty() || idToken.isEmpty()) {
            responseFail(PlatformType.YAHOO)
            return
        }

        // decode idToken by https://developer.yahoo.com/oauth2/guide/openid_connect/decode_id_token.html
        val array = idToken.split(".")
        val decodedStr = String(Base64.decode(array[1], Base64.DEFAULT))
        val response = decodedStr.createJSONObject()

        if (response == null) {
            responseFail(PlatformType.YAHOO)
            return
        }

        val name = response.getJSONString("name")

        val item = LoginResultItem().apply {
            this.id = guid
            this.name = name
            this.result = true
            this.mPlatform = PlatformType.YAHOO
        }

        responseSuccess(item)
    }
}
