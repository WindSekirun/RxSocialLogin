package com.github.windsekirun.rxsociallogin.base

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import com.github.windsekirun.rxsociallogin.RxSocialLogin
import com.github.windsekirun.rxsociallogin.intenal.exception.LoginFailedException
import com.github.windsekirun.rxsociallogin.intenal.model.PlatformType
import com.github.windsekirun.rxsociallogin.intenal.oauth.LoginOAuthActivity
import com.github.windsekirun.rxsociallogin.intenal.oauth.OAuthConfig
import com.github.windsekirun.rxsociallogin.intenal.utils.clearCookies
import com.github.windsekirun.rxsociallogin.intenal.utils.randomString

/**
 * RxSocialLogin
 * Class: BaseOAuthSocialLogin
 * Created by Pyxis on 2019-02-07.
 *
 *
 * Description:
 */
abstract class BaseOAuthSocialLogin<T : OAuthConfig>(activity: FragmentActivity) : BaseSocialLogin<T>(activity) {
    abstract fun analyzeResult(jsonStr: String)
    abstract fun getRequestCode(): Int
    abstract fun getAuthUrl(): String
    abstract fun getOAuthUrl(): String

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == getRequestCode()) {
            if (data == null) {
                callbackAsFail(LoginFailedException(RxSocialLogin.EXCEPTION_FAILED_RESULT))
                return
            }

            val jsonStr = data.getStringExtra(LoginOAuthActivity.RESPONSE_JSON) ?: "{}"
            analyzeResult(jsonStr)
        } else if (requestCode == getRequestCode() && resultCode != Activity.RESULT_OK) {
            callbackAsFail(LoginFailedException(RxSocialLogin.EXCEPTION_USER_CANCELLED))
        }
    }

    override fun logout(clearToken: Boolean) {
        super.logout(clearToken)
        clearCookies()
    }

    override fun login() {
       tryLogin()
    }

    protected open fun getBasicToken(): String {
        return ""
    }

    protected fun tryLogin() {
        val parameters = hashMapOf(
                "redirect_uri" to config.redirectUri,
                "client_id" to config.clientId,
                "client_secret" to config.clientSecret,
                "grant_type" to "authorization_code")

        if (getPlatformType() == PlatformType.DISCORD) {
            parameters["scope"] = "identify email"
        }

        if (getPlatformType() == PlatformType.GITHUB) {
            parameters.remove("redirect_uri")
            parameters.remove("grant_type")
        }

        LoginOAuthActivity.startOAuthActivity(activity, getRequestCode(),
                getPlatformType(), getAuthUrl(), config.activityTitle, getOAuthUrl(), parameters, getBasicToken())
    }

    protected fun getState() = randomString(22)
}
