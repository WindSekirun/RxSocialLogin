package com.github.windsekirun.rxsociallogin.windows

import com.github.windsekirun.rxsociallogin.model.SocialConfig

/**
 * RxSocialLogin
 * Class: GithubConfig
 * Created by pyxis on 18. 7. 27.
 *
 *
 * Description:
 */
class WindowsConfig(val clientId: String) : SocialConfig() {

    class Builder {
        private var clientId: String = ""

        fun setClientId(clientId: String): Builder {
            this.clientId = clientId
            return this
        }

        fun build(): WindowsConfig {
            return WindowsConfig(clientId)
        }
    }
}
