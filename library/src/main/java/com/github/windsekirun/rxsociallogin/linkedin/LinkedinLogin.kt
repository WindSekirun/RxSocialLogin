package com.github.windsekirun.rxsociallogin.linkedin

import androidx.fragment.app.FragmentActivity
import com.github.kittinunf.fuel.httpGet
import com.github.windsekirun.rxsociallogin.OAuthConstants
import com.github.windsekirun.rxsociallogin.RxSocialLogin
import com.github.windsekirun.rxsociallogin.RxSocialLogin.EXCEPTION_FAILED_RESULT
import com.github.windsekirun.rxsociallogin.base.BaseOAuthSocialLogin
import com.github.windsekirun.rxsociallogin.intenal.exception.LoginFailedException
import com.github.windsekirun.rxsociallogin.intenal.model.LoginResultItem
import com.github.windsekirun.rxsociallogin.intenal.model.PlatformType
import com.github.windsekirun.rxsociallogin.intenal.utils.clearCookies
import com.github.windsekirun.rxsociallogin.intenal.utils.toResultObservable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import pyxis.uzuki.live.richutilskt.utils.createJSONObject
import pyxis.uzuki.live.richutilskt.utils.getJSONString

class LinkedinLogin constructor(activity: FragmentActivity) : BaseOAuthSocialLogin<LinkedinConfig>(activity) {
    override fun getOAuthUrl(): String = OAuthConstants.LINKEDIN_OAUTH
    override fun getPlatformType(): PlatformType = PlatformType.LINKEDIN
    override fun getRequestCode(): Int = OAuthConstants.LINKEDIN_REQUEST_CODE

    override fun logout(clearToken: Boolean) {
        super.logout(clearToken)
        clearCookies()
    }

    override fun getAuthUrl(): String {
        var authUrl = "${OAuthConstants.LINKEDIN_URL}?response_type=code&" +
                "client_id=${config.clientId}&redirect_uri=${config.redirectUri}&" +
                "state=${getState()}&scope=r_basicprofile"

        if (config.requireEmail) authUrl += "%20r_emailaddress"

        return authUrl
    }

    override fun analyzeResult(jsonStr: String) {
        val jsonObject = jsonStr.createJSONObject()
        val accessToken = jsonObject?.getJSONString("access_token") ?: ""
        if (accessToken.isEmpty()) {
            callbackAsFail(LoginFailedException(EXCEPTION_FAILED_RESULT))
            return
        }

        val parameters = mutableListOf("id", "picture-url", "first-name", "formatted-name")

        if (config.requireEmail) {
            parameters.add("email-address")
        }

        val url = "https://api.linkedin.com/v1/people/~:(${parameters.joinToString(",")})?format=json"
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
            callbackAsFail(LoginFailedException(EXCEPTION_FAILED_RESULT))
            return
        }

        val firstName = response.getJSONString("firstName")
        val id = response.getJSONString("id")
        val formattedName = response.getJSONString("formattedName")
        val emailAddress = response.getJSONString("emailAddress")

        var pictureUrl: String? = ""
        if (response.has("pictureUrl")) {
            pictureUrl = response.getJSONString("pictureUrl")
        }

        val item = LoginResultItem().apply {
            this.id = id
            this.firstName = firstName
            this.name = formattedName
            this.email = emailAddress
            this.profilePicture = pictureUrl ?: ""
            this.accessToken = accessToken
            this.result = true
            this.platform = PlatformType.LINKEDIN
        }

        callbackAsSuccess(item)
    }
}
