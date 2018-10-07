package com.github.windsekirun.rxsociallogin.google

import com.github.windsekirun.rxsociallogin.intenal.impl.ConfigFunction
import com.github.windsekirun.rxsociallogin.intenal.model.SocialConfig

class GoogleConfig : SocialConfig() {
    var requireEmail: Boolean = false
    var clientTokenId: String = ""

    companion object {
        internal fun apply(clientTokenId: String, setup: ConfigFunction<GoogleConfig>? = null): GoogleConfig {
            val config = GoogleConfig().apply {
                this.clientTokenId = clientTokenId
            }

            setup?.invoke(config)
            return config
        }
    }
}
