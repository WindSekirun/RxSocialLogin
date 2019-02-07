package com.github.windsekirun.rxsociallogin.base

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import com.github.windsekirun.rxsociallogin.RxSocialLogin
import com.github.windsekirun.rxsociallogin.intenal.exception.LoginFailedException
import com.github.windsekirun.rxsociallogin.intenal.model.SocialConfig
import com.github.windsekirun.rxsociallogin.intenal.oauth.LoginOAuthActivity
import com.github.windsekirun.rxsociallogin.intenal.utils.clearCookies

/**
 * RxSocialLogin
 * Class: BaseOAuthSocialLogin
 * Created by Pyxis on 2019-02-07.
 *
 *
 * Description:
 */
abstract class BaseOAuthSocialLogin<T : SocialConfig>(activity: FragmentActivity) : BaseSocialLogin<T>(activity) {
    abstract fun getRequestCode(): Int
    abstract fun analyzeResult(jsonStr: String)

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

    }
}
