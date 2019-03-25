package com.github.windsekirun.rxsociallogin.discord

import com.github.windsekirun.rxsociallogin.intenal.impl.ConfigFunction
import com.github.windsekirun.rxsociallogin.intenal.oauth.OAuthConfig

class DiscordConfig : OAuthConfig() {
    companion object {
        internal fun apply(clientId: String, clientSecret: String, redirectUri: String,
                           setup: ConfigFunction<DiscordConfig>? = null): DiscordConfig {
            val config = DiscordConfig().apply {
                this.clientId = clientId
                this.clientSecret = clientSecret
                this.redirectUri = redirectUri
            }

            setup?.invoke(config)

            return config
        }
    }
}