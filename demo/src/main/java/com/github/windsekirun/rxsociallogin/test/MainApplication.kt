package com.github.windsekirun.rxsociallogin.test

import android.app.Application
import com.github.windsekirun.rxsociallogin.RxSocialLogin
import com.github.windsekirun.rxsociallogin.disqus.DisqusConfig
import com.github.windsekirun.rxsociallogin.facebook.FacebookConfig
import com.github.windsekirun.rxsociallogin.foursquare.FoursquareConfig
import com.github.windsekirun.rxsociallogin.github.GithubConfig
import com.github.windsekirun.rxsociallogin.google.GoogleConfig
import com.github.windsekirun.rxsociallogin.kakao.KakaoConfig
import com.github.windsekirun.rxsociallogin.line.LineConfig
import com.github.windsekirun.rxsociallogin.linkedin.LinkedinConfig
import com.github.windsekirun.rxsociallogin.intenal.model.PlatformType
import com.github.windsekirun.rxsociallogin.naver.NaverConfig
import com.github.windsekirun.rxsociallogin.twitch.TwitchConfig
import com.github.windsekirun.rxsociallogin.twitter.TwitterConfig
import com.github.windsekirun.rxsociallogin.vk.VKConfig
import com.github.windsekirun.rxsociallogin.windows.WindowsConfig
import com.github.windsekirun.rxsociallogin.wordpress.WordpressConfig
import com.github.windsekirun.rxsociallogin.yahoo.YahooConfig


/**
 * RxSocialLogin
 * Class: MainApplication
 * Created by Pyxis on 7/2/18.
 *
 *
 * Description:
 */

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        RxSocialLogin.init(this)

        val twitterConfig = TwitterConfig.Builder()
                .setConsumerKey(getString(R.string.twitter_api_id))
                .setConsumerSecret(getString(R.string.twitter_api_secret))
                .build()

        RxSocialLogin.addType(PlatformType.TWITTER, twitterConfig)

        val facebookConfig = FacebookConfig.Builder()
                .setApplicationId(getString(R.string.facebook_api_key))
                .setRequireEmail()
                .setBehaviorOnCancel()
                .build()

        RxSocialLogin.addType(PlatformType.FACEBOOK, facebookConfig)

        val kakaoConfig = KakaoConfig.Builder()
                .setRequireEmail()
                .setRequireAgeRange()
                .setRequireBirthday()
                .setRequireEmail()
                .setRequireGender()
                .build()

        RxSocialLogin.addType(PlatformType.KAKAO, kakaoConfig)

        val naverConfig = NaverConfig.Builder()
                .setAuthClientId(getString(R.string.naver_api_id))
                .setAuthClientSecret(getString(R.string.naver_api_secret))
                .setClientName(getString(R.string.app_name))
                .build()

        RxSocialLogin.addType(PlatformType.NAVER, naverConfig)

        val lineConfig = LineConfig.Builder()
                .setChannelId(getString(R.string.line_api_channel))
                .build()

        RxSocialLogin.addType(PlatformType.LINE, lineConfig)

        val googleConfig = GoogleConfig.Builder()
                .setRequireEmail()
                .setClientTokenId(getString(R.string.server_client_id))
                .build()

        RxSocialLogin.addType(PlatformType.GOOGLE, googleConfig)

        val githubConfig = GithubConfig.Builder()
                .setClientId(getString(R.string.github_api_key))
                .setClientSecret(getString(R.string.github_api_secret))
                .build()

        RxSocialLogin.addType(PlatformType.GITHUB, githubConfig)

        val linkedinConfig = LinkedinConfig.Builder()
                .setRedirectUri("http://example.com/oauth/callback")
                .setRequireEmail()
                .setClientId(getString(R.string.linkedin_api_key))
                .setClientSecret(getString(R.string.linkedin_api_secret))
                .build()

        RxSocialLogin.addType(PlatformType.LINKEDIN, linkedinConfig)

        val wordpressConfig = WordpressConfig.Builder()
                .setClientId(getString(R.string.wordpress_api_key))
                .setClientSecret(getString(R.string.wordpress_api_secret))
                .setRedirectUri("http://example.com/oauth/callback")
                .build()

        RxSocialLogin.addType(PlatformType.WORDPRESS, wordpressConfig)

        val yahooConfig = YahooConfig.Builder()
                .setClientId(getString(R.string.yahoo_api_key))
                .setClientSecret(getString(R.string.yahoo_api_secret))
                .setRedirectUri("http://example.com")
                .build()

        RxSocialLogin.addType(PlatformType.YAHOO, yahooConfig)

        val vkConfig = VKConfig.Builder()
                .setRequireEmail()
                .build()

        RxSocialLogin.addType(PlatformType.VK, vkConfig)

        val windowsConfig = WindowsConfig.Builder()
                .setClientId(getString(R.string.windows_api_key))
                .build()

        RxSocialLogin.addType(PlatformType.WINDOWS, windowsConfig)

        val disqusConfig = DisqusConfig.Builder()
                .setClientId(getString(R.string.disqus_api_key))
                .setClientSecret(getString(R.string.disqus_api_secret))
                .setRedirectUri("http://www.example.com/oauth_redirect")
                .build()

        RxSocialLogin.addType(PlatformType.DISQUS, disqusConfig)

        val foursquareConfig = FoursquareConfig.Builder()
                .setClientId(getString(R.string.foursquare_api_key))
                .setClientSecret(getString(R.string.foursquare_api_secret))
                .build()

        RxSocialLogin.addType(PlatformType.FOURSQUARE, foursquareConfig)

        val twitchConfig = TwitchConfig.Builder()
                .setClientId(getString(R.string.twitch_api_key))
                .setClientSecret(getString(R.string.twitch_api_secret))
                .setRedirectUri("http://example.com/oauth/callback")
                .setRequireEmail()
                .build()

        RxSocialLogin.addType(PlatformType.TWITCH, twitchConfig)
    }
}
