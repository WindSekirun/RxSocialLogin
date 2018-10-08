package com.github.windsekirun.rxsociallogin.foursquare

import com.github.windsekirun.rxsociallogin.intenal.impl.ConfigFunction
import com.github.windsekirun.rxsociallogin.intenal.model.SocialConfig

class FoursquareConfig : SocialConfig() {
    var clientId: String = ""
    var clientSecret: String = ""

    companion object {
        internal fun apply(clientId: String, clientSecret: String,
                           setup: ConfigFunction<FoursquareConfig>? = null): FoursquareConfig {
            val config = FoursquareConfig().apply {
                this.clientId = clientId
                this.clientSecret = clientSecret
            }

            setup?.invoke(config)
            return config
        }

    }
}
