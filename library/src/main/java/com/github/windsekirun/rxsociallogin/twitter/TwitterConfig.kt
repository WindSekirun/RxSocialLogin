package com.github.windsekirun.rxsociallogin.twitter

import com.github.windsekirun.rxsociallogin.intenal.model.SocialConfig

class TwitterConfig private constructor(var consumerKey: String?, var consumerSecret: String?) : SocialConfig() {

    class Builder {
        private var consumerKey: String? = null
        private var consumerSecret: String? = null

        fun setConsumerKey(consumerKey: String): Builder {
            this.consumerKey = consumerKey
            return this
        }

        fun setConsumerSecret(consumerSecret: String): Builder {
            this.consumerSecret = consumerSecret
            return this
        }

        fun build(): TwitterConfig {
            return TwitterConfig(consumerKey, consumerSecret)
        }
    }
}
