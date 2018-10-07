package com.github.windsekirun.rxsociallogin.twitter

import com.github.windsekirun.rxsociallogin.intenal.model.SocialConfig

class TwitterConfig : SocialConfig() {
    var consumerKey: String = ""
    var consumerSecret: String = ""

    companion object {
        internal fun apply(consumerKey: String, consumerSecret: String): TwitterConfig {
            return TwitterConfig().apply {
                this.consumerKey = consumerKey
                this.consumerSecret = consumerSecret
            }
        }
    }
}
