package com.github.windsekirun.rxsociallogin.bitbucket

import com.github.windsekirun.rxsociallogin.intenal.impl.ConfigFunction
import com.github.windsekirun.rxsociallogin.intenal.oauth.OAuthConfig

class BitbucketConfig : OAuthConfig() {
    companion object {
        internal fun apply(clientId: String, clientSecret: String, redirectUri: String,
                           setup: ConfigFunction<BitbucketConfig>? = null): BitbucketConfig {
            val config = BitbucketConfig().apply {
                this.clientId = clientId
                this.clientSecret = clientSecret
                this.redirectUri = redirectUri
            }

            setup?.invoke(config)

            return config
        }
    }
}