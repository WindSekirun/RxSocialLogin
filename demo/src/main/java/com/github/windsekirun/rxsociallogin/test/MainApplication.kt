package com.github.windsekirun.rxsociallogin.test

import android.app.Application
import com.github.windsekirun.rxsociallogin.facebook.FacebookConfig
import com.github.windsekirun.rxsociallogin.initSocialLogin


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

        initSocialLogin {
            disqus(getString(R.string.disqus_api_key), getString(R.string.disqus_api_secret), getString(R.string.disqus_redirect_uri)) {
                activityTitle = "Login to Disqus"
            }

            facebook(getString(R.string.facebook_api_key)) {
                behaviorOnCancel = true
                requireWritePermissions = false
                imageEnum = FacebookConfig.FacebookImageEnum.Large
            }

            foursquare(getString(R.string.foursquare_api_key), getString(R.string.foursquare_api_secret))

            github(getString(R.string.github_api_key), getString(R.string.github_api_secret)) {
                scopeConfig = arrayListOf()
                activityTitle = "Login to Github"
            }

            google(getString(R.string.google_api_token)) {
                requireEmail = true
            }

            kakao {
                requireBirthday = true
            }

            line(getString(R.string.line_api_channel))

            linkedin(getString(R.string.linkedin_api_key), getString(R.string.linkedin_api_secret), getString(R.string.linkedin_redirect_uri)) {
                requireEmail = true
                activityTitle = "Login to LinkedIn"
            }

            naver(getString(R.string.naver_api_id), getString(R.string.naver_api_secret), getString(R.string.app_name))

            twitch(getString(R.string.twitch_api_key), getString(R.string.twitch_api_secret), getString(R.string.twitch_redirect_uri)) {
                requireEmail = true
                activityTitle = "Login to Twitch"
            }

            twitter(getString(R.string.twitter_api_id), getString(R.string.twitter_api_secret))

            vk {
                requireEmail = true
            }

            windows(getString(R.string.windows_api_key))

            wordpress(getString(R.string.wordpress_api_key), getString(R.string.wordpress_api_secret), getString(R.string.wordpress_redirect_uri)) {
                activityTitle = "Login to Wordpress"
            }

            yahoo(getString(R.string.yahoo_api_key), getString(R.string.yahoo_api_secret), getString(R.string.yahoo_redirect_uri)) {
                activityTitle = "Login to Yahoo"
            }

            discord(getString(R.string.discord_api_key), getString(R.string.discord_api_secret), getString(R.string.discord_redirect_uri)) {
                activityTitle = "Login to Discord"
            }

            bitbucket(getString(R.string.bitbucket_api_key), getString(R.string.bitbucket_api_secret), getString(R.string.bitbucket_redirect_uri)) {
                activityTitle = "Login to Bitbucket"
            }

            gitlab(getString(R.string.gitlab_api_key), getString(R.string.gitlab_api_secret), getString(R.string.gitlab_redirect_uri)) {
                activityTitle = "Login to Gitlab"
            }
        }
    }
}
