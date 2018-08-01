package com.github.windsekirun.rxsociallogin.linkedin

import com.github.windsekirun.rxsociallogin.kakao.KakaoConfig
import com.github.windsekirun.rxsociallogin.model.SocialConfig

/**
 * RxSocialLogin
 * Class: GithubConfig
 * Created by pyxis on 18. 7. 27.
 *
 *
 * Description:
 */
class LinkedinConfig(val clientId: String, val clientSecret: String, val clearCookies: Boolean,
                     val activityTitle: String, val redirectUri: String, val requireEmail: Boolean) : SocialConfig() {

    class Builder {
        private var clientId: String = ""
        private var clientSecret: String = ""
        private var clearCookies: Boolean = false
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

        fun build(): LinkedinConfig {
            return LinkedinConfig(clientId, clientSecret, clearCookies, activityTitle, redirectUri, isRequireEmail)
        }
    }
}
