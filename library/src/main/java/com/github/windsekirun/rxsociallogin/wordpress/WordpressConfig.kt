package com.github.windsekirun.rxsociallogin.wordpress

import com.github.windsekirun.rxsociallogin.intenal.impl.ConfigFunction
import com.github.windsekirun.rxsociallogin.intenal.oauth.OAuthConfig

class WordpressConfig : OAuthConfig() {
    companion object {
        internal fun apply(clientId: String, clientSecret: String, redirectUri: String,
                           setup: ConfigFunction<WordpressConfig>? = null): WordpressConfig {
            val config = WordpressConfig().apply {
                this.clientId = clientId
                this.clientSecret = clientSecret
                this.redirectUri = redirectUri
            }

            setup?.invoke(config)

            return config
        }
    }
}