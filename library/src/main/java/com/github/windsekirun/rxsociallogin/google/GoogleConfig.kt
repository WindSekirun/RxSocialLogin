package com.github.windsekirun.rxsociallogin.google

import com.github.windsekirun.rxsociallogin.intenal.model.SocialConfig


class GoogleConfig private constructor(val requireEmail: Boolean, val clientTokenId: String) : SocialConfig() {

    class Builder {
        private var requireEmail = false
        private var clientTokenId = ""

        fun setRequireEmail(): Builder {
            requireEmail = true
            return this
        }

        fun setClientTokenId(clientTokenId: String): Builder {
            this.clientTokenId = clientTokenId
            return this
        }

        fun build(): GoogleConfig {
            return GoogleConfig(requireEmail, clientTokenId)
        }
    }
}
