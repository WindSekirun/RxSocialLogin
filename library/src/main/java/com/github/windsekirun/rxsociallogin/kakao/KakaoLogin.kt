package com.github.windsekirun.rxsociallogin.kakao

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.github.windsekirun.rxsociallogin.RxSocialLogin
import com.github.windsekirun.rxsociallogin.SocialLogin
import com.github.windsekirun.rxsociallogin.model.LoginResultItem
import com.github.windsekirun.rxsociallogin.model.PlatformType
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

class KakaoLogin(activity: Activity) : SocialLogin(activity) {
    private var mSessionCallback: SessionCallback? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        checkSession()
        Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)
    }

    override fun onLogin() {
        checkSession()
        mSessionCallback = SessionCallback()

        val session = Session.getCurrentSession()

        session.addCallback(mSessionCallback)
        if (!session.checkAndImplicitOpen()) {
            session.open(AuthType.KAKAO_LOGIN_ALL, activity)
        }
    }

    override fun onDestroy() {
        checkSession()

        if (mSessionCallback != null) {
            Session.getCurrentSession().removeCallback(mSessionCallback)
        }
    }

    override fun logout(clearToken: Boolean) {
        checkSession()

        if (Session.getCurrentSession().checkAndImplicitOpen()) {
            Session.getCurrentSession().close()
        }
    }

    fun toObservable() = RxSocialLogin.kakao(this)

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
            val message = exception?.message ?: ""
            Log.d("SessionCallback", "OpenFailed:: $message")

            responseFail(PlatformType.KAKAO)
        }
    }

    private fun requestMe() {
        val config = getConfig(PlatformType.KAKAO) as KakaoConfig

        UserManagement.getInstance().me(config.requestOptions, object : MeV2ResponseCallback() {
            override fun onSessionClosed(errorResult: ErrorResult) {
                responseFail(PlatformType.KAKAO)
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

                responseSuccess(item)
            }
        })
    }
}
