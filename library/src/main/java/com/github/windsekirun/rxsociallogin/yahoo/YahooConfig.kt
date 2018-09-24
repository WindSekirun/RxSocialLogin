package com.github.windsekirun.rxsociallogin.yahoo

import com.github.windsekirun.rxsociallogin.model.SocialConfig

class YahooConfig(val clientId: String, val clientSecret: String, val activityTitle: String,
                  val redirectUri: String) : SocialConfig() {

    class Builder {
        private var clientId: String = ""
        private var clientSecret: String = ""
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

        fun setActivityTitle(activityTitle: String): Builder {
            this.activityTitle = activityTitle
            return this
        }

        fun setRedirectUri(redirectUri: String): Builder {
            this.redirectUri = redirectUri
            return this
        }

        fun build(): YahooConfig {
            return YahooConfig(clientId, clientSecret, activityTitle, redirectUri)
        }
    }
}
