package com.github.windsekirun.rxsociallogin.linkedin

import com.github.windsekirun.rxsociallogin.intenal.impl.ConfigFunction
import com.github.windsekirun.rxsociallogin.intenal.oauth.OAuthConfig

class LinkedinConfig : OAuthConfig() {
    var requireEmail: Boolean = false

    companion object {
        internal fun apply(clientId: String, clientSecret: String, redirectUri: String,
                           setup: ConfigFunction<LinkedinConfig>? = null): LinkedinConfig {
            val config = LinkedinConfig().apply {
                this.clientId = clientId
                this.clientSecret = clientSecret
                this.redirectUri = redirectUri
            }

            setup?.invoke(config)

            return config
        }
    }
}
