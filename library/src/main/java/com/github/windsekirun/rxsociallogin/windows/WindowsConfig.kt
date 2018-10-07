package com.github.windsekirun.rxsociallogin.windows

import com.github.windsekirun.rxsociallogin.intenal.impl.ConfigFunction
import com.github.windsekirun.rxsociallogin.intenal.model.SocialConfig

class WindowsConfig : SocialConfig() {
    var clientId: String = ""

    companion object {
        internal fun apply(clientId: String, setup: ConfigFunction<WindowsConfig>? = null): WindowsConfig {
            val config = WindowsConfig().apply {
                this.clientId = clientId
            }

            setup?.invoke(config)
            return config
        }
    }
}
