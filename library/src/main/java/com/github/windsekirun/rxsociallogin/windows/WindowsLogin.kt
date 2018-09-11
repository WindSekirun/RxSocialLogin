package com.github.windsekirun.rxsociallogin.windows

import android.app.Activity
import android.content.Intent
import com.github.windsekirun.rxsociallogin.SocialLogin
import com.github.windsekirun.rxsociallogin.intenal.net.OkHttpHelper
import com.github.windsekirun.rxsociallogin.model.LoginResultItem
import com.github.windsekirun.rxsociallogin.model.PlatformType
import com.microsoft.identity.client.AuthenticationCallback
import com.microsoft.identity.client.AuthenticationResult
import com.microsoft.identity.client.MsalException
import com.microsoft.identity.client.PublicClientApplication
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import pyxis.uzuki.live.richutilskt.utils.createJSONObject
import pyxis.uzuki.live.richutilskt.utils.getJSONString


class WindowsLogin(activity: Activity) : SocialLogin(activity) {
    private val mConfig: WindowsConfig by lazy { getConfig(PlatformType.WINDOWS) as WindowsConfig }
    private lateinit var disposable: Disposable
    private lateinit var clientApplication: PublicClientApplication

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (::clientApplication.isInitialized) {
            clientApplication.handleInteractiveRequestRedirect(requestCode, resultCode, data)
        }
    }

    override fun onLogin() {
        clientApplication = PublicClientApplication(activity!!.applicationContext, mConfig.clientId)
        clientApplication.acquireToken(activity!!, arrayOf("User.Read"), object : AuthenticationCallback {
            override fun onSuccess(authenticationResult: AuthenticationResult?) {
                if (authenticationResult == null) {
                    responseFail(PlatformType.WINDOWS)
                    return
                }
                getUserInfo(authenticationResult)
            }

            override fun onCancel() {
                responseFail(PlatformType.WINDOWS)
            }

            override fun onError(exception: MsalException?) {
                responseFail(PlatformType.WINDOWS)
            }

        })
    }

    override fun onDestroy() {
        if (::disposable.isInitialized && !disposable.isDisposed) {
            disposable.dispose()
        }
    }

    override fun logout(clearToken: Boolean) {
        super.logout(clearToken)

        if (::clientApplication.isInitialized) {
            try {
                clientApplication.users.forEach {
                    clientApplication.remove(it)
                }
            } catch (ignore: Exception) {
            }
        }
    }

    private fun getUserInfo(authenticationResult: AuthenticationResult) {
        if (authenticationResult.accessToken == null) {
            responseFail(PlatformType.WINDOWS)
            return
        }

        val requestUrl = "https://graph.microsoft.com/v1.0/me"

        disposable = requestUrl.toRequest("Bearer ${authenticationResult.accessToken}")
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val jsonObject = it.createJSONObject()

                    if (jsonObject == null) {
                        responseFail(PlatformType.WINDOWS)
                        return@subscribe
                    }

                    val item = LoginResultItem().apply {
                        this.id = jsonObject.getJSONString("id")
                        this.name = jsonObject.getJSONString("displayName")
                        this.email = jsonObject.getJSONString("userPrincipalName")
                        this.platform = PlatformType.WINDOWS
                        this.result = true
                    }

                    responseSuccess(item)
                }) {
                    responseFail(PlatformType.WINDOWS)
                }
    }

    private fun String.toRequest(authorization: String): Single<String> {
        return OkHttpHelper.get(this, "Authorization" to authorization)
    }
}
