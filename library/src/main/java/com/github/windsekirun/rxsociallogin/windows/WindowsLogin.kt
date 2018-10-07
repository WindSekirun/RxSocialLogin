package com.github.windsekirun.rxsociallogin.windows

import android.content.Intent
import android.support.v4.app.FragmentActivity
import com.github.kittinunf.fuel.httpGet
import com.github.windsekirun.rxsociallogin.BaseSocialLogin
import com.github.windsekirun.rxsociallogin.RxSocialLogin
import com.github.windsekirun.rxsociallogin.RxSocialLogin.getPlatformConfig
import com.github.windsekirun.rxsociallogin.intenal.fuel.toResultObservable
import com.github.windsekirun.rxsociallogin.intenal.model.LoginResultItem
import com.github.windsekirun.rxsociallogin.intenal.model.PlatformType
import com.microsoft.identity.client.AuthenticationCallback
import com.microsoft.identity.client.AuthenticationResult
import com.microsoft.identity.client.MsalException
import com.microsoft.identity.client.PublicClientApplication
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import pyxis.uzuki.live.richutilskt.utils.createJSONObject
import pyxis.uzuki.live.richutilskt.utils.getJSONString


class WindowsLogin constructor(activity: FragmentActivity) : BaseSocialLogin(activity) {
    private val config: WindowsConfig by lazy { getPlatformConfig(PlatformType.WINDOWS) as WindowsConfig }
    private val clientApplication: PublicClientApplication by lazy { PublicClientApplication(activity!!.applicationContext, config.clientId) }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1001) { // found on InteractiveRequest.java
            clientApplication.handleInteractiveRequestRedirect(requestCode, resultCode, data)
        }
    }

    override fun login() {
        clientApplication.acquireToken(activity!!, arrayOf("User.Read"), object : AuthenticationCallback {
            override fun onSuccess(authenticationResult: AuthenticationResult?) {
                if (authenticationResult == null) {
                    callbackFail(PlatformType.WINDOWS)
                    return
                }
                getUserInfo(authenticationResult)
            }

            override fun onCancel() {
                callbackFail(PlatformType.WINDOWS)
            }

            override fun onError(exception: MsalException?) {
                callbackFail(PlatformType.WINDOWS)
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

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated("use RxSocialLogin.result instead")
    fun toObservable() = RxSocialLogin.windows(this)

    private fun getUserInfo(authenticationResult: AuthenticationResult) {
        if (authenticationResult.accessToken == null) {
            callbackFail(PlatformType.WINDOWS)
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
                        parseUserInfo(result.component1())
                    } else {
                        callbackFail(PlatformType.WINDOWS)
                    }
                }

        compositeDisposable.add(disposable)
    }

    private fun parseUserInfo(jsonStr: String?) {
        val jsonObject = jsonStr?.createJSONObject()

        if (jsonObject == null) {
            callbackFail(PlatformType.WINDOWS)
            return
        }

        val item = LoginResultItem().apply {
            this.id = jsonObject.getJSONString("id")
            this.name = jsonObject.getJSONString("displayName")
            this.email = jsonObject.getJSONString("userPrincipalName")
            this.platform = PlatformType.WINDOWS
            this.result = true
        }

        callbackItem(item)
    }
}
