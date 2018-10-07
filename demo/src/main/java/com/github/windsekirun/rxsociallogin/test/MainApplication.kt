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
            disqus(getString(R.string.disqus_api_key),
                    getString(R.string.disqus_api_secret),
                    getString(R.string.disqus_redirect_uri)) {
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

            google(getString(R.string.google_api_key)) {
                requireEmail = true
            }

            kakao {
                requireAgeRange = true
                requireBirthday = true
                requireEmail = true
                requireGender = true
            }

            line(getString(R.string.line_api_channel))

            linkedin(getString(R.string.linkedin_api_key),
                    getString(R.string.linkedin_api_secret),
                    getString(R.string.linkedin_redirect_uri)) {
                requireEmail = true
                activityTitle = "Login to LinkedIn"
            }

            naver(getString(R.string.naver_api_id),
                    getString(R.string.naver_api_secret),
                    getString(R.string.app_name))
        }
    }
}
