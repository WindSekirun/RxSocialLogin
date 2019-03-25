package com.github.windsekirun.rxsociallogin.amazon

import com.github.windsekirun.rxsociallogin.intenal.model.SocialConfig

class AmazonConfig : SocialConfig() {

    companion object {
        internal fun apply(): AmazonConfig {
            val config = AmazonConfig()
//            setup?.invoke(config)
            return config
        }
    }
}
