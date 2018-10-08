package com.github.windsekirun.rxsociallogin.twitch

import com.github.windsekirun.rxsociallogin.intenal.impl.ConfigFunction
import com.github.windsekirun.rxsociallogin.intenal.oauth.OAuthConfig

class TwitchConfig : OAuthConfig() {
    var requireEmail: Boolean = false

    companion object {
        internal fun apply(clientId: String, clientSecret: String, redirectUri: String,
                           setup: ConfigFunction<TwitchConfig>? = null): TwitchConfig {
            val config = TwitchConfig().apply {
                this.clientId = clientId
                this.clientSecret = clientSecret
                this.redirectUri = redirectUri
            }

            setup?.invoke(config)

            return config
        }
    }
}
