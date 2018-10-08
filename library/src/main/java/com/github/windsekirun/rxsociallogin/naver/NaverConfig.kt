package com.github.windsekirun.rxsociallogin.naver

import com.github.windsekirun.rxsociallogin.intenal.model.SocialConfig

class NaverConfig : SocialConfig() {
    var authClientId: String = ""
    var authClientSecret: String = ""
    var clientName: String = ""

    companion object {
        internal fun apply(authClientId: String, authClientSecret: String, clientName: String): NaverConfig {
            return NaverConfig().apply {
                this.authClientId = authClientId
                this.authClientSecret = authClientSecret
                this.clientName = clientName
            }
        }
    }
}
