package com.github.windsekirun.rxsociallogin.github

import com.github.windsekirun.rxsociallogin.intenal.impl.ConfigFunction
import com.github.windsekirun.rxsociallogin.intenal.oauth.OAuthConfig

class GithubConfig : OAuthConfig() {
    var scopeConfig: ArrayList<String> = arrayListOf()

    companion object {
        internal fun apply(clientId: String, clientSecret: String,
                           setup: ConfigFunction<GithubConfig>? = null): GithubConfig {
            val config = GithubConfig().apply {
                this.clientId = clientId
                this.clientSecret = clientSecret
            }

            setup?.invoke(config)

            return config
        }
    }
}