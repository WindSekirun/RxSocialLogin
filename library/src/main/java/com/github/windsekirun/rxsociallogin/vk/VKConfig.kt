package com.github.windsekirun.rxsociallogin.vk

import com.github.windsekirun.rxsociallogin.intenal.model.SocialConfig

class VKConfig(val requireEmail: Boolean) : SocialConfig() {

    class Builder {
        private var isRequireEmail = false

        fun setRequireEmail(): Builder {
            isRequireEmail = true
            return this
        }


        fun build(): VKConfig {
            return VKConfig(isRequireEmail)
        }
    }
}
