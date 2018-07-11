package com.github.windsekirun.rxsociallogin.naver

import com.github.windsekirun.rxsociallogin.model.SocialConfig

/**
 * SocialLogin
 * Class: TwitterConfig
 * Created by Pyxis on 2017-10-27.
 *
 *
 * Description:
 */

class NaverConfig private constructor(val authClientId: String?, val authClientSecret: String?,
                                      val clientName: String?) : SocialConfig() {

    class Builder {
        private var oAuthClientId: String? = null
        private var oAuthClientSecret: String? = null
        private var clientName: String? = null

        fun setAuthClientId(oAuthClientId: String): Builder {
            this.oAuthClientId = oAuthClientId
            return this
        }

        fun setAuthClientSecret(oAuthClientSecret: String): Builder {
            this.oAuthClientSecret = oAuthClientSecret
            return this
        }

        fun setClientName(clientName: String): Builder {
            this.clientName = clientName
            return this
        }

        fun build(): NaverConfig {
            return NaverConfig(oAuthClientId, oAuthClientSecret, clientName)
        }
    }
}
