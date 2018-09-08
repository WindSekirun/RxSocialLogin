package com.github.windsekirun.rxsociallogin.vk

import com.github.windsekirun.rxsociallogin.model.SocialConfig

/**
 * RxSocialLogin
 * Class: GithubConfig
 * Created by pyxis on 18. 7. 27.
 *
 *
 * Description:
 */
class VKConfig(val clientId: String, val clientSecret: String, val clearCookies: Boolean,
               val activityTitle: String, val redirectUri: String, val requireEmail: Boolean,
               val version: String) : SocialConfig() {

    class Builder {
        private var clientId: String = ""
        private var clientSecret: String = ""
        private var clearCookies: Boolean = false
        private var activityTitle: String = "Login to VK"
        private var redirectUri: String = "http://www.example.com/auth/vk"
        private var isRequireEmail = false
        private var version = "5.84"

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

        fun setVersion(version: String): Builder {
            this.version = version
            return this
        }

        fun build(): VKConfig {
            return VKConfig(clientId, clientSecret, clearCookies, activityTitle, redirectUri, isRequireEmail, version)
        }
    }
}
