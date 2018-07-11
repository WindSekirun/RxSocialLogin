package com.github.windsekirun.rxsociallogin.google

import com.github.windsekirun.rxsociallogin.model.SocialConfig


class GoogleConfig private constructor(val requireEmail: Boolean) : SocialConfig() {

    class Builder {
        private var requireEmail = false

        fun setRequireEmail(): Builder {
            requireEmail = true
            return this
        }

        fun build(): GoogleConfig {
            return GoogleConfig(requireEmail)
        }
    }
}
