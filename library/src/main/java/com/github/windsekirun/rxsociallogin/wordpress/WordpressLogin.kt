package com.github.windsekirun.rxsociallogin.wordpress

import android.app.Activity
import android.content.Intent
import android.support.v4.app.FragmentActivity
import com.github.kittinunf.fuel.httpGet
import com.github.windsekirun.rxsociallogin.BaseSocialLogin
import com.github.windsekirun.rxsociallogin.intenal.oauth.LoginOAuthActivity
import com.github.windsekirun.rxsociallogin.OAuthConstants
import com.github.windsekirun.rxsociallogin.RxSocialLogin
import com.github.windsekirun.rxsociallogin.RxSocialLogin.getPlatformConfig
import com.github.windsekirun.rxsociallogin.intenal.fuel.toResultObservable
import com.github.windsekirun.rxsociallogin.intenal.model.LoginResultItem
import com.github.windsekirun.rxsociallogin.intenal.model.PlatformType
import com.github.windsekirun.rxsociallogin.intenal.oauth.AccessTokenProvider
import com.github.windsekirun.rxsociallogin.intenal.oauth.clearCookies
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import pyxis.uzuki.live.richutilskt.utils.createJSONObject
import pyxis.uzuki.live.richutilskt.utils.getJSONBoolean
import pyxis.uzuki.live.richutilskt.utils.getJSONString

class WordpressLogin constructor(activity: FragmentActivity) : BaseSocialLogin(activity) {
    private val config: WordpressConfig by lazy { getPlatformConfig(PlatformType.WORDPRESS) as WordpressConfig }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == OAuthConstants.WORDPRESS_REQUEST_CODE) {
            val jsonStr = data!!.getStringExtra(LoginOAuthActivity.RESPONSE_JSON) ?: "{}"
            analyzeResult(jsonStr)
        } else if (requestCode == OAuthConstants.WORDPRESS_REQUEST_CODE && resultCode != Activity.RESULT_OK) {
            callbackFail(PlatformType.WORDPRESS)
        }
    }

    override fun login() {
        val accessToken = AccessTokenProvider.wordpressAccessToken
        if (accessToken.isNotEmpty()) {
            checkAccessTokenAvailable(accessToken)
        } else {
            tryLogin()
        }
    }

    override fun logout(clearToken: Boolean) {
        super.logout(clearToken)
        clearCookies()
        AccessTokenProvider.wordpressAccessToken = ""
    }

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated("use RxSocialLogin.result instead")
    fun toObservable() = RxSocialLogin.wordpress(this)

    private fun analyzeResult(jsonStr: String) {
        val jsonObject = jsonStr.createJSONObject()
        val accessToken = jsonObject?.getJSONString("access_token") ?: ""
        if (accessToken.isEmpty()) {
            callbackFail(PlatformType.WORDPRESS)
            return
        }

        AccessTokenProvider.wordpressAccessToken = accessToken
        getUserInfo(accessToken)
    }

    private fun checkAccessTokenAvailable(accessToken: String) {
        val requestUrl = "https://public-api.wordpress.com/oauth2/token-info" +
                "?client_id=${config.clientId}&token=$accessToken"

        val disposable = requestUrl.httpGet()
                .toResultObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { result, error ->
                    if (error == null && result.component1() != null) {
                        getUserInfo(accessToken)
                    } else {
                        tryLogin()
                    }
                }

        compositeDisposable.add(disposable)
    }

    private fun tryLogin() {
        val authUrl = "${OAuthConstants.WORDPRESS_URL}?client_id=${config.clientId}&" +
                "redirect_uri=${config.redirectUri}&response_type=code"

        val title = config.activityTitle
        val oauthUrl = OAuthConstants.WORDPRESS_OAUTH
        val parameters = listOf(
                "grant_type" to "authorization_code",
                "redirect_uri" to config.redirectUri,
                "client_id" to config.clientId,
                "client_secret" to config.clientSecret)
        val map = hashMapOf(*parameters.toTypedArray())

        LoginOAuthActivity.startOAuthActivity(activity, OAuthConstants.WORDPRESS_REQUEST_CODE,
                PlatformType.WORDPRESS, authUrl, title, oauthUrl, map)
    }

    private fun getUserInfo(accessToken: String) {
        val url = "https://public-api.wordpress.com/rest/v1.1/me"
        val disposable = url.httpGet()
                .header("Authorization" to "Bearer $accessToken")
                .toResultObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { result, error ->
                    if (error == null && result.component1() != null) {
                        parseUserInfo(result.component1())
                    } else {
                        callbackFail(PlatformType.WORDPRESS)
                    }
                }

        compositeDisposable.add(disposable)
    }

    private fun parseUserInfo(jsonStr: String?) {
        val response = jsonStr?.createJSONObject()

        if (response == null) {
            callbackFail(PlatformType.WORDPRESS)
            return
        }

        val id = response.getJSONString("ID")
        val username = response.getJSONString("username")
        val email = response.getJSONString("email")
        val profilePicture = response.getJSONString("profile_URL")
        val emailVerified = response.getJSONBoolean("email_verified")

        val item = LoginResultItem().apply {
            this.id = id
            this.name = username
            this.email = email
            this.profilePicture = profilePicture
            this.emailVerified = emailVerified

            this.result = true
            this.platform = PlatformType.WORDPRESS
        }

        callbackItem(item)
    }
}
