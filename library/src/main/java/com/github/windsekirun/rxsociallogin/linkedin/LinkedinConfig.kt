package com.github.windsekirun.rxsociallogin.linkedin

import com.github.windsekirun.rxsociallogin.intenal.model.SocialConfig

class LinkedinConfig(val clientId: String, val clientSecret: String, val activityTitle: String,
                     val redirectUri: String, val requireEmail: Boolean) : SocialConfig() {

    class Builder {
        private var clientId: String = ""
        private var clientSecret: String = ""
        private var activityTitle: String = "Login to Linkedin"
        private var redirectUri: String = "http://www.example.com/auth/linkedin"
        private var isRequireEmail = false

        fun setRequireEmail(): Builder {
            isRequireEmail = true
            return this
        }

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

        fun build(): LinkedinConfig {
            return LinkedinConfig(clientId, clientSecret, activityTitle, redirectUri, isRequireEmail)
        }
    }
}
