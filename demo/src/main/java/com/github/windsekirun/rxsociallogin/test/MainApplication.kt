package com.github.windsekirun.rxsociallogin.test

import android.app.Application
import com.github.windsekirun.rxsociallogin.SocialLogin
import com.github.windsekirun.rxsociallogin.facebook.FacebookConfig
import com.github.windsekirun.rxsociallogin.google.GoogleConfig
import com.github.windsekirun.rxsociallogin.kakao.KakaoConfig
import com.github.windsekirun.rxsociallogin.line.LineConfig
import com.github.windsekirun.rxsociallogin.model.SocialType
import com.github.windsekirun.rxsociallogin.naver.NaverConfig
import com.github.windsekirun.rxsociallogin.twitter.TwitterConfig


/**
 * SocialLogin
 * Class: MainApplication
 * Created by Pyxis on 7/2/18.
 *
 *
 * Description:
 */

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        SocialLogin.init(this)

        val twitterConfig = TwitterConfig.Builder()
                .setConsumerKey(getString(R.string.twitter_api_id))
                .setConsumerSecret(getString(R.string.twitter_api_secret))
                .build()

        SocialLogin.addType(SocialType.TWITTER, twitterConfig)

        val facebookConfig = FacebookConfig.Builder()
                .setApplicationId(getString(R.string.facebook_api_key))
                .setRequireEmail()
                .setBehaviorOnCancel()
                .build()

        SocialLogin.addType(SocialType.FACEBOOK, facebookConfig)

        val kakaoConfig = KakaoConfig.Builder()
                .setRequireEmail()
                .setRequireAgeRange()
                .setRequireBirthday()
                .setRequireEmail()
                .setRequireGender()
                .build()

        SocialLogin.addType(SocialType.KAKAO, kakaoConfig)

        val naverConfig = NaverConfig.Builder()
                .setAuthClientId(getString(R.string.naver_api_id))
                .setAuthClientSecret(getString(R.string.naver_api_secret))
                .setClientName(getString(R.string.app_name))
                .build()

        SocialLogin.addType(SocialType.NAVER, naverConfig)

        val lineConfig = LineConfig.Builder()
                .setChannelId(getString(R.string.line_api_channel))
                .build()

        SocialLogin.addType(SocialType.LINE, lineConfig)

        val googleConfig = GoogleConfig.Builder()
                .setRequireEmail()
                .setClientTokenId(getString(R.string.server_client_id))
                .build()

        SocialLogin.addType(SocialType.GOOGLE, googleConfig)
    }
}
