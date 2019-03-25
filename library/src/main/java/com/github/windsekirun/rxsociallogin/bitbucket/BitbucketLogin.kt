package com.github.windsekirun.rxsociallogin.bitbucket

import androidx.fragment.app.FragmentActivity
import com.github.kittinunf.fuel.httpGet
import com.github.windsekirun.rxsociallogin.OAuthConstants
import com.github.windsekirun.rxsociallogin.RxSocialLogin
import com.github.windsekirun.rxsociallogin.base.BaseOAuthSocialLogin
import com.github.windsekirun.rxsociallogin.intenal.exception.LoginFailedException
import com.github.windsekirun.rxsociallogin.intenal.model.LoginResultItem
import com.github.windsekirun.rxsociallogin.intenal.model.PlatformType
import com.github.windsekirun.rxsociallogin.intenal.utils.toResultObservable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import pyxis.uzuki.live.richutilskt.utils.createJSONObject
import pyxis.uzuki.live.richutilskt.utils.getJSONString
import pyxis.uzuki.live.richutilskt.utils.isEmpty

class BitbucketLogin constructor(activity: FragmentActivity) : BaseOAuthSocialLogin<BitbucketConfig>(activity) {
    override fun getAuthUrl(): String = "${OAuthConstants.BITBUCKET_URL}?client_id=${config.clientId}&response_type=code"

    override fun getPlatformType(): PlatformType = PlatformType.BITBUCKET
    override fun getRequestCode(): Int = OAuthConstants.BITBUCKET_REQUEST_CODE
    override fun getOAuthUrl(): String = OAuthConstants.BITBUCEKT_OAUTH

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

        val requestUrl = "https://api.bitbucket.org/2.0/user?fields=-links"
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

        val item = LoginResultItem().apply {
            this.id = jsonObject.getJSONString("uuid")
            this.name = jsonObject.getJSONString("username")
            this.nickname = jsonObject.getJSONString("nickname")
            this.accessToken = accessToken
            this.platform = PlatformType.BITBUCKET
            this.result = true
        }

        callbackAsSuccess(item)
    }
}
