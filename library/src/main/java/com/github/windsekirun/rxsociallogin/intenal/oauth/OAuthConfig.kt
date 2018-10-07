package com.github.windsekirun.rxsociallogin.intenal.oauth

import com.github.windsekirun.rxsociallogin.intenal.impl.ConfigFunction
import com.github.windsekirun.rxsociallogin.intenal.model.SocialConfig

/**
 * RxSocialLogin
 * Class: OAuthConfig
 * Created by Pyxis on 10/7/18.
 *
 *
 * Description:
 */
open class OAuthConfig : SocialConfig() {
    var clientId: String = ""
    var clientSecret: String = ""
    var activityTitle: String = "Login to Platform"
    var redirectUri: String = ""

    companion object {
        internal fun apply(clientId: String, clientSecret: String, redirectUri: String,
                           setup: ConfigFunction<OAuthConfig>? = null): OAuthConfig {
            val config = OAuthConfig().apply {
                this.clientId = clientId
                this.clientSecret = clientSecret
                this.redirectUri = redirectUri
            }

            setup?.invoke(config)

            return config
        }
    }
}
