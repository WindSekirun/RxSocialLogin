package com.github.windsekirun.rxsociallogin.vk

import com.github.windsekirun.rxsociallogin.model.SocialConfig

/**
 * RxSocialLogin
 * Class: GithubConfig
 * Created by pyxis on 18. 7. 27.
 *
 *
 * Description:
 */
class VKConfig(val requireEmail: Boolean) : SocialConfig() {

    class Builder {
        private var isRequireEmail = false

        fun setRequireEmail(): Builder {
            isRequireEmail = true
            return this
        }


        fun build(): VKConfig {
            return VKConfig(isRequireEmail)
        }
    }
}
