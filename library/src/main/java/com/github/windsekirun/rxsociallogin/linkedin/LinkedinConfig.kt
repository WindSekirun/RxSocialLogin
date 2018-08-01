package com.github.windsekirun.rxsociallogin.linkedin

import com.github.windsekirun.rxsociallogin.model.SocialConfig

/**
 * RxSocialLogin
 * Class: GithubConfig
 * Created by pyxis on 18. 7. 27.
 *
 *
 * Description:
 */
class LinkedinConfig(val clientId: String, val clientSecret: String, val scopeList: ArrayList<String>) : SocialConfig() {

    class Builder {
        private var clientId: String = ""
        private var clientSecret: String = ""
        private var scopeList: ArrayList<String> = arrayListOf()
        fun setClientId(clientId: String): Builder {
            this.clientId = clientId
            return this
        }

        fun setClientSecret(clientSecret: String): Builder {
            this.clientSecret = clientSecret
            return this
        }

        fun addScope(vararg items: String): Builder {
            this.scopeList.addAll(items.toList())
            return this
        }

        fun setScopeList(scopeList: ArrayList<String>): Builder {
            this.scopeList = scopeList
            return this
        }

        fun build(): LinkedinConfig {
            return LinkedinConfig(clientId, clientSecret, scopeList)
        }
    }
}
