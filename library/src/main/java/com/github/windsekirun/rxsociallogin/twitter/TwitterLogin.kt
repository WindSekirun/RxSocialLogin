package com.github.windsekirun.rxsociallogin.twitter

import android.content.Intent
import android.support.v4.app.FragmentActivity
import com.github.windsekirun.rxsociallogin.BaseSocialLogin
import com.github.windsekirun.rxsociallogin.RxSocialLogin
import com.github.windsekirun.rxsociallogin.intenal.exception.LoginFailedException
import com.github.windsekirun.rxsociallogin.intenal.model.LoginResultItem
import com.github.windsekirun.rxsociallogin.intenal.model.PlatformType
import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.core.identity.TwitterAuthClient
import com.twitter.sdk.android.core.models.User

class TwitterLogin constructor(activity: FragmentActivity) : BaseSocialLogin(activity) {
    private val twitterAuthClient = TwitterAuthClient()
    private val twitterApiClient: TwitterApiClient by lazy { TwitterCore.getInstance().apiClient }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        twitterAuthClient.onActivityResult(requestCode, resultCode, data)
    }

    override fun login() {
        twitterAuthClient.authorize(activity, object : Callback<TwitterSession>() {
            override fun success(result: Result<TwitterSession>) {
                getUserInfo(result)
            }

            override fun failure(exception: TwitterException) {
                callbackAsFail(LoginFailedException(RxSocialLogin.EXCEPTION_FAILED_RESULT, exception))
            }
        })
    }

    private fun getUserInfo(result: Result<TwitterSession>) {
        twitterApiClient.accountService.verifyCredentials(false, false, true)
                .enqueue(object : Callback<User>() {
                    override fun success(result: Result<User>?) {
                        if (result == null) {
                            callbackAsFail(LoginFailedException(RxSocialLogin.EXCEPTION_FAILED_RESULT))
                            return
                        }

                        val user = result.data
                        if (user == null) {
                            callbackAsFail(LoginFailedException(RxSocialLogin.EXCEPTION_FAILED_RESULT))
                            return
                        }

                        val item = LoginResultItem().apply {
                            this.id = user.idStr
                            this.name = user.name
                            this.nickname = user.screenName
                            this.email = user.email
                            this.platform = PlatformType.TWITTER
                            this.result = true
                            this.profilePicture = user.profileImageUrl
                        }

                        callbackAsSuccess(item)
                    }

                    override fun failure(exception: TwitterException?) {
                        if (exception != null) {
                            callbackAsFail(LoginFailedException(RxSocialLogin.EXCEPTION_FAILED_RESULT, exception))
                        } else {
                            callbackAsFail(LoginFailedException(RxSocialLogin.EXCEPTION_FAILED_RESULT))
                        }
                    }

                })

        val item = LoginResultItem().apply {
            this.id = result.data.userId.toString()
            this.name = result.data.userName

            this.platform = PlatformType.TWITTER
            this.result = true
        }

        callbackAsSuccess(item)
    }
}
