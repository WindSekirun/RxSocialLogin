package com.github.windsekirun.rxsociallogin.wordpress

import com.github.windsekirun.rxsociallogin.model.SocialConfig

class WordpressConfig(val clientId: String, val clientSecret: String,
                      val activityTitle: String, val redirectUri: String) : SocialConfig() {

    class Builder {
        private var clientId: String = ""
        private var clientSecret: String = ""
        private var activityTitle: String = "Login to Wordpress"
        private var redirectUri: String = "http://www.example.com/auth/wordpress"

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

        fun build(): WordpressConfig {
            return WordpressConfig(clientId, clientSecret, activityTitle, redirectUri)
        }
    }
}
