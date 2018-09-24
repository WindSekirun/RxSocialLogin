package com.github.windsekirun.rxsociallogin.twitch

import com.github.windsekirun.rxsociallogin.model.SocialConfig

class TwitchConfig(val clientId: String, val clientSecret: String, val activityTitle: String,
                   val redirectUri: String, val requireEmail: Boolean) : SocialConfig() {

    class Builder {
        private var clientId: String = ""
        private var clientSecret: String = ""
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
            return TwitchConfig(clientId, clientSecret, activityTitle, redirectUri, isRequireEmail)
        }
    }
}
