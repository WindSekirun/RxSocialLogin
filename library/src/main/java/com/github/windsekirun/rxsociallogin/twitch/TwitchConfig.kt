package com.github.windsekirun.rxsociallogin.twitch

import com.github.windsekirun.rxsociallogin.model.SocialConfig

/**
 * RxSocialLogin
 * Class: GithubConfig
 * Created by pyxis on 18. 7. 27.
 *
 *
 * Description:
 */
class TwitchConfig(val clientId: String, val clientSecret: String, val clearCookies: Boolean,
                   val activityTitle: String, val redirectUri: String, val requireEmail: Boolean) : SocialConfig() {

    class Builder {
        private var clientId: String = ""
        private var clientSecret: String = ""
        private var clearCookies: Boolean = false
        private var activityTitle: String = "Login to Twitch"
        private var redirectUri: String = "http://example.com/auth/disqus"
        private var isRequireEmail: Boolean = false

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

        fun setRequireEmail(): Builder {
            this.isRequireEmail = true
            return this
        }

        fun build(): TwitchConfig {
            return TwitchConfig(clientId, clientSecret, clearCookies, activityTitle, redirectUri, isRequireEmail)
        }
    }
}
