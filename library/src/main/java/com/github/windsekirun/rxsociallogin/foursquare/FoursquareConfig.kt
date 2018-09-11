package com.github.windsekirun.rxsociallogin.foursquare

import com.github.windsekirun.rxsociallogin.model.SocialConfig

/**
 * RxSocialLogin
 * Class: GithubConfig
 * Created by pyxis on 18. 7. 27.
 *
 *
 * Description:
 */
class FoursquareConfig(val clientId: String, val clientSecret: String) : SocialConfig() {

    class Builder {
        private var clientId: String = ""
        private var clientSecret: String = ""

        fun setClientId(clientId: String): Builder {
            this.clientId = clientId
            return this
        }

        fun setClientSecret(clientSecret: String): Builder {
            this.clientSecret = clientSecret
            return this
        }

        fun build(): FoursquareConfig {
            return FoursquareConfig(clientId, clientSecret)
        }
    }
}
