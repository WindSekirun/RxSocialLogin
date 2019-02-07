package com.github.windsekirun.rxsociallogin.line

import android.content.Context
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import com.github.windsekirun.rxsociallogin.base.BaseSocialLogin
import com.github.windsekirun.rxsociallogin.RxSocialLogin
import com.github.windsekirun.rxsociallogin.RxSocialLogin.getPlatformConfig
import com.github.windsekirun.rxsociallogin.intenal.exception.LoginFailedException
import com.github.windsekirun.rxsociallogin.intenal.model.LoginResultItem
import com.github.windsekirun.rxsociallogin.intenal.model.PlatformType
import com.linecorp.linesdk.LineApiResponseCode
import com.linecorp.linesdk.auth.LineLoginApi

class LineLogin constructor(activity: FragmentActivity) : BaseSocialLogin<LineConfig>(activity) {
    override fun getPlatformType(): PlatformType = PlatformType.LINE

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE) onResultLineLogin(data)
    }

    override fun login() {
        val loginIntent = LineLoginApi.getLoginIntent(activity as Context, config.channelId)
        activity!!.startActivityForResult(loginIntent, REQUEST_CODE)
    }

    override fun onDestroy() {

    }

    private fun onResultLineLogin(data: Intent?) {
        val result = LineLoginApi.getLoginResultFromIntent(data)
        when (result.responseCode) {
            LineApiResponseCode.SUCCESS -> {
                val accessToken = result.lineCredential?.accessToken?.accessToken
                val lineProfile = result.lineProfile
                if (lineProfile == null) {
                    callbackAsFail(LoginFailedException(RxSocialLogin.EXCEPTION_FAILED_RESULT))
                    return
                }

                val item = LoginResultItem().apply {
                    this.platform = PlatformType.LINE
                    this.result = true
                    this.accessToken = accessToken ?: ""
                    this.id = lineProfile.userId
                    this.name = lineProfile.displayName
                    this.profilePicture = lineProfile.pictureUrl?.path ?: ""
                }

                callbackAsSuccess(item)
            }

            else -> callbackAsFail(LoginFailedException(RxSocialLogin.EXCEPTION_FAILED_RESULT, Exception(result.errorData.message)))
        }
    }

    companion object {
        private const val REQUEST_CODE = 8073
    }
}
