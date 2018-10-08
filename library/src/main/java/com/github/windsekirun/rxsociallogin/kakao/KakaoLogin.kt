package com.github.windsekirun.rxsociallogin.kakao

import android.content.Intent
import android.support.v4.app.FragmentActivity
import com.github.windsekirun.rxsociallogin.BaseSocialLogin
import com.github.windsekirun.rxsociallogin.RxSocialLogin
import com.github.windsekirun.rxsociallogin.RxSocialLogin.getPlatformConfig
import com.github.windsekirun.rxsociallogin.intenal.exception.LoginFailedException
import com.github.windsekirun.rxsociallogin.intenal.model.LoginResultItem
import com.github.windsekirun.rxsociallogin.intenal.model.PlatformType
import com.kakao.auth.AuthType
import com.kakao.auth.ISessionCallback
import com.kakao.auth.KakaoSDK
import com.kakao.auth.Session
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import com.kakao.util.OptionalBoolean
import com.kakao.util.exception.KakaoException
import java.util.*

class KakaoLogin constructor(activity: FragmentActivity) : BaseSocialLogin(activity) {
    private var sessionCallback: SessionCallback? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        checkSession()
        Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)
    }

    override fun login() {
        checkSession()
        sessionCallback = SessionCallback()

        val session = Session.getCurrentSession()

        session.addCallback(sessionCallback)
        if (!session.checkAndImplicitOpen()) {
            session.open(AuthType.KAKAO_LOGIN_ALL, activity)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        checkSession()

        if (sessionCallback != null) {
            Session.getCurrentSession().removeCallback(sessionCallback)
        }
    }

    override fun logout(clearToken: Boolean) {
        checkSession()

        if (Session.getCurrentSession().checkAndImplicitOpen()) {
            Session.getCurrentSession().close()
        }
    }

    private fun checkSession() {
        try {
            Session.getCurrentSession().checkAndImplicitOpen()
        } catch (e: Exception) {
            KakaoSDK.init(kakaoSDKAdapter)
        }
    }

    private inner class SessionCallback : ISessionCallback {

        override fun onSessionOpened() {
            requestMe()
        }

        override fun onSessionOpenFailed(exception: KakaoException?) {
            if (exception != null) {
                callbackAsFail(LoginFailedException(RxSocialLogin.EXCEPTION_FAILED_RESULT, exception))
            } else {
                callbackAsFail(LoginFailedException(RxSocialLogin.EXCEPTION_FAILED_RESULT))
            }
        }
    }

    private fun requestMe() {
        val config = getPlatformConfig(PlatformType.KAKAO) as KakaoConfig

        val requestOptions = ArrayList<String>()
        requestOptions.add("properties.nickname")
        requestOptions.add("properties.profile_image")
        requestOptions.add("properties.thumbnail_image")

        if (config.requireEmail) requestOptions.add("kakao_account.email")
        if (config.requireAgeRange) requestOptions.add("kakao_account.age_range")
        if (config.requireBirthday) requestOptions.add("kakao_account.birthday")
        if (config.requireGender) requestOptions.add("kakao_account.gender")

        UserManagement.getInstance().me(requestOptions, object : MeV2ResponseCallback() {
            override fun onSessionClosed(errorResult: ErrorResult) {
                callbackAsFail(LoginFailedException(RxSocialLogin.EXCEPTION_FAILED_RESULT))
            }

            override fun onSuccess(result: MeV2Response) {
                // default value
                val id = result.id.toString()
                val nickname = result.nickname
                val profilePicture = result.profileImagePath
                val thumbnailPicture = result.thumbnailImagePath

                // optional value
                var email = ""
                var gender = ""
                var ageRange = ""
                var birthday = ""
                var isEmailVerified = false
                val userAccount = result.kakaoAccount

                if (userAccount != null && userAccount.hasEmail() == OptionalBoolean.TRUE) {
                    email = userAccount.email
                    isEmailVerified = userAccount.isEmailVerified == OptionalBoolean.TRUE
                }

                if (userAccount != null && userAccount.hasAgeRange() == OptionalBoolean.TRUE) {
                    ageRange = userAccount.ageRange!!.value
                }

                if (userAccount != null && userAccount.hasGender() == OptionalBoolean.TRUE) {
                    gender = userAccount.gender!!.value
                }

                if (userAccount != null && userAccount.hasBirthday() == OptionalBoolean.TRUE) {
                    birthday = userAccount.birthday
                }

                val item = LoginResultItem().apply {
                    this.id = id
                    this.nickname = nickname
                    this.profilePicture = profilePicture
                    this.thumbnailPicture = thumbnailPicture
                    this.email = email
                    this.gender = gender
                    this.ageRange = ageRange
                    this.birthday = birthday
                    this.emailVerified = isEmailVerified
                    this.result = true
                    this.platform = PlatformType.KAKAO
                }

                callbackAsSuccess(item)
            }
        })
    }
}
