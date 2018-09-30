package com.github.windsekirun.rxsociallogin.disqus

import android.app.Activity
import android.content.Intent
import android.support.v4.app.FragmentActivity
import com.github.kittinunf.fuel.httpGet
import com.github.windsekirun.rxsociallogin.OAuthConstants
import com.github.windsekirun.rxsociallogin.RxSocialLogin
import com.github.windsekirun.rxsociallogin.intenal.fuel.toResultObservable
import com.github.windsekirun.rxsociallogin.intenal.oauth.BaseOAuthActivity
import com.github.windsekirun.rxsociallogin.intenal.oauth.clearCookies
import com.github.windsekirun.rxsociallogin.intenal.model.LoginResultItem
import com.github.windsekirun.rxsociallogin.intenal.model.PlatformType
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import pyxis.uzuki.live.richutilskt.utils.createJSONObject
import pyxis.uzuki.live.richutilskt.utils.getJSONString
import pyxis.uzuki.live.richutilskt.utils.isEmpty

class DisqusLogin @JvmOverloads constructor(activity: FragmentActivity? = null) : RxSocialLogin(activity) {
    private val config: DisqusConfig by lazy { getPlatformConfig(PlatformType.DISQUS) as DisqusConfig }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == OAuthConstants.DISQUS_REQUEST_CODE) {
            val jsonStr = data!!.getStringExtra(BaseOAuthActivity.RESPONSE_JSON) ?: "{}"
            analyzeResult(jsonStr)
        } else if (requestCode == OAuthConstants.DISQUS_REQUEST_CODE && resultCode != Activity.RESULT_OK) {
            callbackFail(PlatformType.DISQUS)
        }
    }

    override fun login() {
        addWeakMap(PlatformType.DISQUS, this)

        val intent = Intent(activity, DisqusOAuthActivity::class.java)
        activity?.startActivityForResult(intent, OAuthConstants.DISQUS_REQUEST_CODE)
    }

    override fun logout(clearToken: Boolean) {
        super.logout(clearToken)
        clearCookies()
    }

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated("use RxSocialLogin.result instead")
    fun toObservable() = RxSocialLogin.disqus(this)

    private fun analyzeResult(jsonStr: String) {
        val accessTokenResult = jsonStr.createJSONObject()
        if (accessTokenResult == null) {
            callbackFail(PlatformType.DISQUS)
            return
        }

        val accessToken = accessTokenResult.getJSONString("access_token", "")
        if (accessToken.isEmpty()) {
            callbackFail(PlatformType.DISQUS)
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
                        parseUserJson(result.component1())
                    } else {
                        callbackFail(PlatformType.DISQUS)
                    }
                }

        compositeDisposable.add(disposable)
    }

    private fun parseUserJson(jsonStr: String?) {
        val jsonObject = jsonStr?.createJSONObject()
        val responseObject = jsonObject?.getJSONObject("response")

        if (responseObject == null) {
            callbackFail(PlatformType.DISQUS)
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
            this.platform = PlatformType.DISQUS
            this.result = true
        }

        callbackItem(item)
    }
}
