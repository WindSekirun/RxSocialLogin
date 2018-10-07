package com.github.windsekirun.rxsociallogin.facebook

import com.github.windsekirun.rxsociallogin.intenal.impl.ConfigFunction
import com.github.windsekirun.rxsociallogin.intenal.model.SocialConfig
import java.util.*

class FacebookConfig : SocialConfig() {
    var imageEnum = FacebookImageEnum.Large
    var requestOptions: ArrayList<String> = arrayListOf()
    var requireWritePermissions: Boolean = false
    var behaviorOnCancel: Boolean = false
    var applicationId: String? = ""
    var requireEmail: Boolean = false
    var requireFriends: Boolean = false

    enum class FacebookImageEnum(val fieldName: String) {
        Small("picture.type(small)"),
        Normal("picture.type(normal)"),
        Album("picture.type(album)"),
        Large("picture.type(large)"),
        Square("picture.type(square)")
    }

    companion object {
        internal fun apply(applicationId: String, setup: ConfigFunction<FacebookConfig>? = null): FacebookConfig {
            val config = FacebookConfig().apply {
                this.applicationId = applicationId
            }

            setup?.invoke(config)
            return config
        }
    }
}
