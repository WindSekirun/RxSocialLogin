package com.github.windsekirun.rxsociallogin.disqus

import com.github.windsekirun.rxsociallogin.model.SocialConfig

/**
 * RxSocialLogin
 * Class: GithubConfig
 * Created by pyxis on 18. 7. 27.
 *
 *
 * Description:
 */
class DisqusConfig(val clientId: String, val clientSecret: String, val activityTitle: String,
                   val redirectUri: String) : SocialConfig() {

    class Builder {
        private var clientId: String = ""
        private var clientSecret: String = ""
        private var activityTitle: String = "Login to Disqus"
        private var redirectUri: String = "http://example.com/auth/disqus"

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

        fun build(): DisqusConfig {
            return DisqusConfig(clientId, clientSecret, activityTitle, redirectUri)
        }
    }
}
