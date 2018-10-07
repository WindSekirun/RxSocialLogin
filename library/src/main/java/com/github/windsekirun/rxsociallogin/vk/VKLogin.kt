package com.github.windsekirun.rxsociallogin.vk

import android.app.Activity
import android.content.Intent
import android.support.v4.app.FragmentActivity
import com.github.windsekirun.rxsociallogin.BaseSocialLogin
import com.github.windsekirun.rxsociallogin.RxSocialLogin
import com.github.windsekirun.rxsociallogin.RxSocialLogin.getPlatformConfig
import com.github.windsekirun.rxsociallogin.intenal.exception.LoginFailedException
import com.github.windsekirun.rxsociallogin.intenal.model.LoginResultItem
import com.github.windsekirun.rxsociallogin.intenal.model.PlatformType
import com.vk.sdk.VKAccessToken
import com.vk.sdk.VKCallback
import com.vk.sdk.VKSdk
import com.vk.sdk.api.*
import pyxis.uzuki.live.richutilskt.utils.getJSONString

class VKLogin constructor(activity: FragmentActivity) : BaseSocialLogin(activity) {
    private val config: VKConfig by lazy { getPlatformConfig(PlatformType.VK) as VKConfig }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        !VKSdk.onActivityResult(requestCode, resultCode, data, object : VKCallback<VKAccessToken> {
            override fun onError(error: VKError?) {
                callbackAsFail(LoginFailedException(RxSocialLogin.EXCEPTION_FAILED_RESULT,
                        Exception(error?.errorMessage ?: "")))
            }

            override fun onResult(res: VKAccessToken?) {
                getUserInfo(res)
            }
        })
    }

    override fun login() {
        val scopeList = mutableListOf("status", "photos")
        if (config.requireEmail) {
            scopeList.add("email")
        }

        VKSdk.login(activity as Activity, *scopeList.toTypedArray())
    }

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated("use RxSocialLogin.result instead")
    fun toObservable() = RxSocialLogin.vk(this)

    private fun getUserInfo(token: VKAccessToken?) {
        val request = VKApi.users().get(VKParameters.from(VKApiConst.FIELDS, "nickname,screen_name,bdate,city,photo_max"))
        request.executeWithListener(object : VKRequest.VKRequestListener() {
            override fun attemptFailed(request: VKRequest?, attemptNumber: Int, totalAttempts: Int) {
                super.attemptFailed(request, attemptNumber, totalAttempts)
                callbackAsFail(LoginFailedException(RxSocialLogin.EXCEPTION_FAILED_RESULT))
            }

            override fun onComplete(response: VKResponse?) {
                super.onComplete(response)

                if (response == null) {
                    callbackAsFail(LoginFailedException(RxSocialLogin.EXCEPTION_FAILED_RESULT))
                } else {
                    parseResponse(response, token)
                }
            }

            override fun onError(error: VKError?) {
                super.onError(error)
                callbackAsFail(LoginFailedException(RxSocialLogin.EXCEPTION_FAILED_RESULT,
                        Exception(error?.errorMessage ?: "")))
            }
        })
    }

    private fun parseResponse(response: VKResponse, token: VKAccessToken?) {
        val jsonObject = response.json
        if (jsonObject == null) {
            callbackAsFail(LoginFailedException(RxSocialLogin.EXCEPTION_FAILED_RESULT))
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

        callbackAsSuccess(result)
    }
}
