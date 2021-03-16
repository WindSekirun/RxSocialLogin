package com.github.windsekirun.rxsociallogin.apple

import com.github.windsekirun.rxsociallogin.intenal.impl.ConfigFunction
import com.github.windsekirun.rxsociallogin.intenal.model.SocialConfig
import okhttp3.Response
import java.util.ArrayList

class AppleConfig : SocialConfig() {
    var scopes: Array<Scope> = arrayOf()

    enum class Scope{
        NAME, EMAIL
    }

    companion object {
        internal fun apply(
            scopes: Array<Scope>,
            setup: ConfigFunction<AppleConfig>? = null): AppleConfig {
            val config = AppleConfig().apply {
                this.scopes = scopes
            }

            setup?.invoke(config)
            return config
        }
    }
}
