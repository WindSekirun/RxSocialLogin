package com.github.windsekirun.rxsociallogin.windows

import android.content.Intent
import androidx.fragment.app.FragmentActivity
import com.github.kittinunf.fuel.httpGet
import com.github.windsekirun.rxsociallogin.base.BaseSocialLogin
import com.github.windsekirun.rxsociallogin.RxSocialLogin
import com.github.windsekirun.rxsociallogin.RxSocialLogin.getPlatformConfig
import com.github.windsekirun.rxsociallogin.intenal.exception.LoginFailedException
import com.github.windsekirun.rxsociallogin.intenal.model.LoginResultItem
import com.github.windsekirun.rxsociallogin.intenal.model.PlatformType
import com.github.windsekirun.rxsociallogin.intenal.utils.toResultObservable
import com.microsoft.identity.client.AuthenticationCallback
import com.microsoft.identity.client.AuthenticationResult
import com.microsoft.identity.client.MsalException
import com.microsoft.identity.client.PublicClientApplication
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import pyxis.uzuki.live.richutilskt.utils.createJSONObject
import pyxis.uzuki.live.richutilskt.utils.getJSONString


class WindowsLogin constructor(activity: FragmentActivity) : BaseSocialLogin<WindowsConfig>(activity) {
    override fun getPlatformType(): PlatformType = PlatformType.WINDOWS

    private val clientApplication: PublicClientApplication by lazy { PublicClientApplication(activity.applicationContext, config.clientId) }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1001) { // found on InteractiveRequest.java
            clientApplication.handleInteractiveRequestRedirect(requestCode, resultCode, data)
        }
    }

    override fun login() {
        clientApplication.acquireToken(activity!!, arrayOf("User.Read"), object : AuthenticationCallback {
            override fun onSuccess(authenticationResult: AuthenticationResult?) {
                if (authenticationResult == null) {
                    callbackAsFail(LoginFailedException(RxSocialLogin.EXCEPTION_FAILED_RESULT))
                    return
                }

                getUserInfo(authenticationResult)
            }

            override fun onCancel() {
                callbackAsFail(LoginFailedException(RxSocialLogin.EXCEPTION_USER_CANCELLED))
            }

            override fun onError(exception: MsalException?) {
                if (exception != null) {
                    callbackAsFail(LoginFailedException(RxSocialLogin.EXCEPTION_FAILED_RESULT, exception))
                } else {
                    callbackAsFail(LoginFailedException(RxSocialLogin.EXCEPTION_FAILED_RESULT))
                }
            }
        })
    }

    override fun logout(clearToken: Boolean) {
        super.logout(clearToken)

        try {
            clientApplication.users.forEach {
                clientApplication.remove(it)
            }
        } catch (ignore: Exception) {
        }
    }

    private fun getUserInfo(authenticationResult: AuthenticationResult) {
        if (authenticationResult.accessToken == null) {
            callbackAsFail(LoginFailedException(RxSocialLogin.EXCEPTION_FAILED_RESULT))
            return
        }

        val requestUrl = "https://graph.microsoft.com/v1.0/me"

        val disposable = requestUrl.httpGet()
                .header("Authorization" to "Bearer ${authenticationResult.accessToken}")
                .toResultObservable()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { result, error ->
                    if (error == null && result.component1() != null) {
                        parseUserInfo(result.component1(), authenticationResult.accessToken)
                    } else {
                        callbackAsFail(LoginFailedException(RxSocialLogin.EXCEPTION_FAILED_RESULT, error))
                    }
                }

        compositeDisposable.add(disposable)
    }

    private fun parseUserInfo(jsonStr: String?, accessToken: String) {
        val jsonObject = jsonStr?.createJSONObject()
        if (jsonObject == null) {
            callbackAsFail(LoginFailedException(RxSocialLogin.EXCEPTION_FAILED_RESULT))
            return
        }

        val item = LoginResultItem().apply {
            this.id = jsonObject.getJSONString("id")
            this.name = jsonObject.getJSONString("displayName")
            this.email = jsonObject.getJSONString("userPrincipalName")
            this.accessToken = accessToken
            this.platform = PlatformType.WINDOWS
            this.result = true
        }

        callbackAsSuccess(item)
    }
}
