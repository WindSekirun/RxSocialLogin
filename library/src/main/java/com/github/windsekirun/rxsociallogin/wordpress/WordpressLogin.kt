package com.github.windsekirun.rxsociallogin.wordpress

import androidx.fragment.app.FragmentActivity
import com.github.kittinunf.fuel.httpGet
import com.github.windsekirun.rxsociallogin.OAuthConstants
import com.github.windsekirun.rxsociallogin.RxSocialLogin
import com.github.windsekirun.rxsociallogin.base.BaseOAuthSocialLogin
import com.github.windsekirun.rxsociallogin.intenal.exception.LoginFailedException
import com.github.windsekirun.rxsociallogin.intenal.model.LoginResultItem
import com.github.windsekirun.rxsociallogin.intenal.model.PlatformType
import com.github.windsekirun.rxsociallogin.intenal.oauth.AccessTokenProvider
import com.github.windsekirun.rxsociallogin.intenal.oauth.LoginOAuthActivity
import com.github.windsekirun.rxsociallogin.intenal.utils.clearCookies
import com.github.windsekirun.rxsociallogin.intenal.utils.toResultObservable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import pyxis.uzuki.live.richutilskt.utils.createJSONObject
import pyxis.uzuki.live.richutilskt.utils.getJSONBoolean
import pyxis.uzuki.live.richutilskt.utils.getJSONString

class WordpressLogin constructor(activity: FragmentActivity) : BaseOAuthSocialLogin<WordpressConfig>(activity) {
    override fun getAuthUrl(): String = "${OAuthConstants.WORDPRESS_URL}?client_id=${config.clientId}&" +
            "redirect_uri=${config.redirectUri}&response_type=code"

    override fun getOAuthUrl(): String = OAuthConstants.WORDPRESS_OAUTH
    override fun getRequestCode(): Int = OAuthConstants.WORDPRESS_REQUEST_CODE
    override fun getPlatformType(): PlatformType = PlatformType.WORDPRESS

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

    override fun analyzeResult(jsonStr: String) {
        val jsonObject = jsonStr.createJSONObject()
        val accessToken = jsonObject?.getJSONString("access_token") ?: ""
        if (accessToken.isEmpty()) {
            callbackAsFail(LoginFailedException(RxSocialLogin.EXCEPTION_FAILED_RESULT))
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

    private fun getUserInfo(accessToken: String) {
        val url = "https://public-api.wordpress.com/rest/v1.1/me"
        val disposable = url.httpGet()
                .header("Authorization" to "Bearer $accessToken")
                .toResultObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { result, error ->
                    if (error == null && result.component1() != null) {
                        parseUserInfo(result.component1(), accessToken)
                    } else {
                        callbackAsFail(LoginFailedException(RxSocialLogin.EXCEPTION_FAILED_RESULT, error))
                    }
                }

        compositeDisposable.add(disposable)
    }

    private fun parseUserInfo(jsonStr: String?, accessToken: String) {
        val response = jsonStr?.createJSONObject()
        if (response == null) {
            callbackAsFail(LoginFailedException(RxSocialLogin.EXCEPTION_FAILED_RESULT))
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
            this.accessToken = accessToken
            this.result = true
            this.platform = PlatformType.WORDPRESS
        }

        callbackAsSuccess(item)
    }
}
