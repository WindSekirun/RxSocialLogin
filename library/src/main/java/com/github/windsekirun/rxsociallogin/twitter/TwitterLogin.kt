package com.github.windsekirun.rxsociallogin.twitter

import android.app.Activity
import android.content.Intent
import com.github.windsekirun.rxsociallogin.SocialLogin
import com.github.windsekirun.rxsociallogin.model.LoginResultItem
import com.github.windsekirun.rxsociallogin.model.SocialType
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.TwitterSession
import com.twitter.sdk.android.core.identity.TwitterAuthClient

class TwitterLogin(activity: Activity) : SocialLogin(activity) {
    private val twitterAuthClient = TwitterAuthClient()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        twitterAuthClient.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {

    }

    override fun onLogin() {
        twitterAuthClient.authorize(activity, object : Callback<TwitterSession>() {
            override fun success(result: Result<TwitterSession>) {
                val item = LoginResultItem().apply {
                    this.id = result.data.userId.toString()
                    this.name = result.data.userName
                    this.type = SocialType.TWITTER
                    this.result = true
                }

                responseSuccess(item)
            }

            override fun failure(exception: TwitterException) {
                responseFail(SocialType.TWITTER)
            }
        })
    }
}
