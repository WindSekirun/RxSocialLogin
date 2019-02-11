package com.github.windsekirun.rxsociallogin.gitlab

import androidx.fragment.app.FragmentActivity
import com.github.kittinunf.fuel.httpGet
import com.github.windsekirun.rxsociallogin.OAuthConstants
import com.github.windsekirun.rxsociallogin.RxSocialLogin
import com.github.windsekirun.rxsociallogin.RxSocialLogin.getPlatformConfig
import com.github.windsekirun.rxsociallogin.base.BaseOAuthSocialLogin
import com.github.windsekirun.rxsociallogin.intenal.exception.LoginFailedException
import com.github.windsekirun.rxsociallogin.intenal.model.LoginResultItem
import com.github.windsekirun.rxsociallogin.intenal.model.PlatformType
import com.github.windsekirun.rxsociallogin.intenal.oauth.LoginOAuthActivity
import com.github.windsekirun.rxsociallogin.intenal.utils.clearCookies
import com.github.windsekirun.rxsociallogin.intenal.utils.randomString
import com.github.windsekirun.rxsociallogin.intenal.utils.toResultObservable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import pyxis.uzuki.live.richutilskt.utils.createJSONObject
import pyxis.uzuki.live.richutilskt.utils.getJSONString
import pyxis.uzuki.live.richutilskt.utils.isEmpty

class GitlabLogin constructor(activity: FragmentActivity) : BaseOAuthSocialLogin<GitlabConfig>(activity) {
    override fun getAuthUrl(): String = "${OAuthConstants.GITLAB_URL}?client_id=${config.clientId}" +
            "&redirect_uri=${config.redirectUri}&response_type=code&state=${getState()}"
    override fun getPlatformType(): PlatformType = PlatformType.GITLAB
    override fun getRequestCode(): Int = OAuthConstants.GITLAB_REQUEST_CODE
    override fun getOAuthUrl(): String = OAuthConstants.GITLAB_OAUTH

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

        val requestUrl = "https://gitlab.com/api/v4/user"
        val disposable = requestUrl.httpGet()
                .header("Content-Type" to "application/json")
                .header("Authorization" to "Bearer $accessToken")
                .toResultObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { result, error ->
                    if (error == null && result.component1() != null) {
                        parseUserJson(result.component1(), accessToken)
                    } else {
                        callbackAsFail(LoginFailedException(RxSocialLogin.EXCEPTION_FAILED_RESULT, error))
                    }
                }

        compositeDisposable.add(disposable)
    }

    private fun parseUserJson(jsonStr: String?, accessToken: String) {
        val jsonObject = jsonStr?.createJSONObject()
        if (jsonObject == null) {
            callbackAsFail(LoginFailedException(RxSocialLogin.EXCEPTION_FAILED_RESULT))
            return
        }

        val id = jsonObject.getJSONString("id")
        val profilePicture = jsonObject.getJSONString("avatar_url")

        val item = LoginResultItem().apply {
            this.id = id
            this.name = jsonObject.getJSONString("name")
            this.email = jsonObject.getJSONString("email")
            this.nickname = jsonObject.getJSONString("username")
            this.profilePicture = profilePicture
            this.accessToken = accessToken
            this.platform = PlatformType.GITLAB
            this.result = true
        }

        callbackAsSuccess(item)
    }
}
