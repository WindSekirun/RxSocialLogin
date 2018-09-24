package com.github.windsekirun.rxsociallogin.windows

import com.github.windsekirun.rxsociallogin.model.SocialConfig

class WindowsConfig(val clientId: String) : SocialConfig() {

    class Builder {
        private var clientId: String = ""

        fun setClientId(clientId: String): Builder {
            this.clientId = clientId
            return this
        }

        fun build(): WindowsConfig {
            return WindowsConfig(clientId)
        }
    }
}
