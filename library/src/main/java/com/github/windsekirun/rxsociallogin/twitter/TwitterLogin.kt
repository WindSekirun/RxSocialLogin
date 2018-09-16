package com.github.windsekirun.rxsociallogin.twitter

import android.app.Activity
import android.content.Intent
import com.github.windsekirun.rxsociallogin.RxSocialLogin
import com.github.windsekirun.rxsociallogin.SocialLogin
import com.github.windsekirun.rxsociallogin.model.LoginResultItem
import com.github.windsekirun.rxsociallogin.model.PlatformType
import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.core.identity.TwitterAuthClient
import com.twitter.sdk.android.core.models.User

class TwitterLogin(activity: Activity) : SocialLogin(activity) {
    private val twitterAuthClient = TwitterAuthClient()
    private val twitterApiClient: TwitterApiClient by lazy { TwitterCore.getInstance().apiClient }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        twitterAuthClient.onActivityResult(requestCode, resultCode, data)
    }

    fun toObservable() = RxSocialLogin.twitter(this)

    override fun login() {
        twitterAuthClient.authorize(activity, object : Callback<TwitterSession>() {
            override fun success(result: Result<TwitterSession>) {
                getUserInfo(result)
            }

            override fun failure(exception: TwitterException) {
                responseFail(PlatformType.TWITTER)
            }
        })
    }

    private fun getUserInfo(result: Result<TwitterSession>) {
        twitterApiClient.accountService.verifyCredentials(false, false, true)
                .enqueue(object : Callback<User>() {
                    override fun success(result: Result<User>?) {
                        if (result == null) {
                            responseFail(PlatformType.TWITTER)
                            return
                        }

                        val user = result.data
                        if (user == null) {
                            responseFail(PlatformType.TWITTER)
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

                        responseSuccess(item)
                    }

                    override fun failure(exception: TwitterException?) {
                        responseFail(PlatformType.TWITTER)
                    }

                })

        val item = LoginResultItem().apply {
            this.id = result.data.userId.toString()
            this.name = result.data.userName

            this.platform = PlatformType.TWITTER
            this.result = true
        }

        responseSuccess(item)
    }
}
