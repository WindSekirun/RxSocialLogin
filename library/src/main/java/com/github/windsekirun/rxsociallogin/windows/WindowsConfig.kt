package com.github.windsekirun.rxsociallogin.windows

import com.github.windsekirun.rxsociallogin.intenal.model.SocialConfig

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
