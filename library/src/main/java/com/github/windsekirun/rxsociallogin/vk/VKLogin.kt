package com.github.windsekirun.rxsociallogin.vk

import android.app.Activity
import android.content.Intent
import com.github.windsekirun.rxsociallogin.RxSocialLogin
import com.github.windsekirun.rxsociallogin.SocialLogin
import com.github.windsekirun.rxsociallogin.model.LoginResultItem
import com.github.windsekirun.rxsociallogin.model.PlatformType
import com.vk.sdk.VKAccessToken
import com.vk.sdk.VKCallback
import com.vk.sdk.VKSdk
import com.vk.sdk.api.*
import io.reactivex.disposables.Disposable
import pyxis.uzuki.live.richutilskt.utils.getJSONString

class VKLogin(activity: Activity) : SocialLogin(activity) {
    private val mConfig: VKConfig by lazy { getConfig(PlatformType.VK) as VKConfig }
    private lateinit var disposable: Disposable

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        !VKSdk.onActivityResult(requestCode, resultCode, data, object : VKCallback<VKAccessToken> {
            override fun onError(error: VKError?) {
                responseFail(PlatformType.VK)
            }

            override fun onResult(res: VKAccessToken?) {
                getUserInfo(res)
            }
        })
    }

    override fun onLogin() {
        val scopeList = mutableListOf("status", "photos")
        if (mConfig.requireEmail) {
            scopeList.add("email")
        }

        VKSdk.login(activity as Activity, *scopeList.toTypedArray())
    }

    override fun onDestroy() {
        if (::disposable.isInitialized && !disposable.isDisposed) {
            disposable.dispose()
        }
    }

    fun toObservable() = RxSocialLogin.vk(this)

    private fun getUserInfo(token: VKAccessToken?) {
        val request = VKApi.users().get(VKParameters.from(VKApiConst.FIELDS, "nickname,screen_name,bdate,city,photo_max"))
        request.executeWithListener(object : VKRequest.VKRequestListener() {
            override fun attemptFailed(request: VKRequest?, attemptNumber: Int, totalAttempts: Int) {
                super.attemptFailed(request, attemptNumber, totalAttempts)
                responseFail(PlatformType.VK)
            }

            override fun onComplete(response: VKResponse?) {
                super.onComplete(response)

                if (response == null) {
                    responseFail(PlatformType.VK)
                } else {
                    parseResponse(response, token)
                }
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

    private fun parseResponse(response: VKResponse, token: VKAccessToken?) {
        val jsonObject = response.json
        if (jsonObject == null) {
            responseFail(PlatformType.VK)
            return
        }

        val myObject = jsonObject.getJSONArray("response").getJSONObject(0)
        val id = myObject.getJSONString("id")
        val firstName = myObject.getJSONString("first_name")
        val name = "$firstName ${myObject.getJSONString("last_name")}"
        val birthday = myObject.getJSONString("bdate")
        val profilePicture = myObject.getJSONString("photo_max")
        val nickname = myObject.getJSONString("nickname")

        val email = if (token != null) token.email ?: "" else ""

        val result = LoginResultItem().apply {
            this.platform = PlatformType.VK
            this.result = true
            this.id = id
            this.name = name
            this.birthday = birthday
            this.profilePicture = profilePicture
            this.firstName = firstName
            this.nickname = nickname
            this.email = email
        }

        responseSuccess(result)
    }
}
