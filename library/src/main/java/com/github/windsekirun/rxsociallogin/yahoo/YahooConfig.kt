package com.github.windsekirun.rxsociallogin.yahoo

import com.github.windsekirun.rxsociallogin.model.SocialConfig

/**
 * RxSocialLogin
 * Class: GithubConfig
 * Created by pyxis on 18. 7. 27.
 *
 *
 * Description:
 */
class YahooConfig(val clientId: String, val clientSecret: String, val clearCookies: Boolean,
                  val activityTitle: String, val redirectUri: String) : SocialConfig() {

    class Builder {
        private var clientId: String = ""
        private var clientSecret: String = ""
        private var clearCookies: Boolean = false
        private var activityTitle: String = "Login to Yahoo"
        private var redirectUri: String = "http://example.com/auth/yahoo"

        fun setClientId(clientId: String): Builder {
            this.clientId = clientId
            return this
        }

        fun setClientSecret(clientSecret: String): Builder {
            this.clientSecret = clientSecret
            return this
        }

        fun setClearCookies(clearCookies: Boolean): Builder {
            this.clearCookies = clearCookies
            return this
        }

        fun setActivityTitle(activityTitle: String): Builder {
            this.activityTitle = activityTitle
            return this
        }

        fun setRedirectUri(redirectUri: String): Builder {
            this.redirectUri = redirectUri
            return this
        }

        fun build(): YahooConfig {
            return YahooConfig(clientId, clientSecret, clearCookies, activityTitle, redirectUri)
        }
    }
}
