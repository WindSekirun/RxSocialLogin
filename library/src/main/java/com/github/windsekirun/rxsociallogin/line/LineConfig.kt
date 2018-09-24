package com.github.windsekirun.rxsociallogin.line

import com.github.windsekirun.rxsociallogin.intenal.model.SocialConfig

class LineConfig private constructor(val channelId: String?) : SocialConfig() {

    class Builder {
        private var channelId: String? = null

        fun setChannelId(channelId: String): Builder {
            this.channelId = channelId
            return this
        }

        fun build(): LineConfig {
            return LineConfig(channelId)
        }
    }
}
