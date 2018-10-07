package com.github.windsekirun.rxsociallogin.vk

import com.github.windsekirun.rxsociallogin.intenal.impl.ConfigFunction
import com.github.windsekirun.rxsociallogin.intenal.model.SocialConfig

class VKConfig() : SocialConfig() {
    var requireEmail: Boolean = false

    companion object {
        internal fun apply(setup: ConfigFunction<VKConfig>? = null): VKConfig {
            val config = VKConfig()
            setup?.invoke(config)
            return config
        }
    }
}
