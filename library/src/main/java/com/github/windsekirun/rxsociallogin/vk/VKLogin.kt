package com.github.windsekirun.rxsociallogin.vk

import android.app.Activity
import android.content.Intent
import com.github.windsekirun.rxsociallogin.SocialLogin
import com.github.windsekirun.rxsociallogin.model.PlatformType
import com.vk.sdk.VKAccessToken
import com.vk.sdk.VKCallback
import com.vk.sdk.VKSdk
import com.vk.sdk.api.*
import io.reactivex.disposables.Disposable

class VKLogin(activity: Activity) : SocialLogin(activity) {
    private val config: VKConfig by lazy { getConfig(PlatformType.VK) as VKConfig }
    private lateinit var disposable: Disposable

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        !VKSdk.onActivityResult(requestCode, resultCode, data, object : VKCallback<VKAccessToken> {
            override fun onError(error: VKError?) {
                responseFail(PlatformType.VK)
            }

            override fun onResult(res: VKAccessToken?) {
                getUserInfo()
            }
        })
    }

    override fun onLogin() {
        VKSdk.login(activity as Activity, "status", "email", "photos")
    }

    override fun onDestroy() {
        if (::disposable.isInitialized && !disposable.isDisposed) {
            disposable.dispose()
        }
    }

    private fun getUserInfo() {
        val request = VKApi.users().get(VKParameters.from(VKApiConst.FIELDS, "nickname,screen_name,bdate,city,photo_max"))
        request.executeWithListener(object : VKRequest.VKRequestListener() {
            override fun attemptFailed(request: VKRequest?, attemptNumber: Int, totalAttempts: Int) {
                super.attemptFailed(request, attemptNumber, totalAttempts)
                responseFail(PlatformType.VK)
            }

            override fun onComplete(response: VKResponse?) {
                super.onComplete(response)

            }

            override fun onProgress(progressType: VKRequest.VKProgressType?, bytesLoaded: Long, bytesTotal: Long) {
                super.onProgress(progressType, bytesLoaded, bytesTotal)
            }

            override fun onError(error: VKError?) {
                super.onError(error)
                responseFail(PlatformType.VK)
            }
        })
    }
}
