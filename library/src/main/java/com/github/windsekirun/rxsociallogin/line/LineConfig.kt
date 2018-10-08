package com.github.windsekirun.rxsociallogin.line

import com.github.windsekirun.rxsociallogin.intenal.impl.ConfigFunction
import com.github.windsekirun.rxsociallogin.intenal.model.SocialConfig

class LineConfig : SocialConfig() {
    var channelId: String = ""

    companion object {
        internal fun apply(channelId: String, setup: ConfigFunction<LineConfig>? = null): LineConfig {
            val config = LineConfig().apply {
                this.channelId = channelId
            }

            setup?.invoke(config)
            return config
        }

    }
}
