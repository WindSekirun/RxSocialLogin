package com.github.windsekirun.rxsociallogin.github

import com.github.windsekirun.rxsociallogin.model.SocialConfig

/**
 * RxSocialLogin
 * Class: GithubConfig
 * Created by pyxis on 18. 7. 27.
 *
 *
 * Description:
 */
class GithubConfig(val clientId: String, val clientSecret: String, val scopeList: ArrayList<String>,
                   val clearCookies: Boolean, val activityTitle: String) : SocialConfig() {

    class Builder {
        private var clientId: String = ""
        private var clientSecret: String = ""
        private var scopeList: ArrayList<String> = arrayListOf()
        private var clearCookies: Boolean = false
        private var activityTitle: String = "Login to Github"

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

        fun setClearCookies(clearCookies: Boolean): Builder {
            this.clearCookies = clearCookies
            return this
        }

        fun setActivityTitle(activityTitle: String): Builder {
            this.activityTitle = activityTitle
            return this
        }

        fun build(): GithubConfig {
            return GithubConfig(clientId, clientSecret, scopeList, clearCookies, activityTitle)
        }
    }
}
