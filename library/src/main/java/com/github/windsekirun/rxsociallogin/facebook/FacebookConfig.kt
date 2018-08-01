package com.github.windsekirun.rxsociallogin.facebook

import android.text.TextUtils
import com.github.windsekirun.rxsociallogin.model.SocialConfig
import java.util.*

class FacebookConfig(var requestOptions: ArrayList<String>?, var isRequireWritePermissions: Boolean,
                     val isBehaviorOnCancel: Boolean, var applicationId: String?) : SocialConfig() {
    val imageEnum = FacebookImageEnum.Large

    class Builder {
        private var isRequireEmail = false
        private var isRequireFriends = false
        private var requireWritePermissions = false
        private var behaviorOnCancel = false
        private var applicationId: String? = null
        private var imageEnum = FacebookImageEnum.Large

        fun setRequireEmail(): Builder {
            isRequireEmail = true
            return this
        }

        fun setRequireWritePermission(): Builder {
            requireWritePermissions = true
            return this
        }

        fun setApplicationId(applicationId: String): Builder {
            this.applicationId = applicationId
            return this
        }

        fun setRequireFriends(): Builder {
            isRequireFriends = true
            return this
        }

        fun setBehaviorOnCancel(): Builder {
            this.behaviorOnCancel = true
            return this
        }

        fun setPictureSize(imageEnum: FacebookImageEnum): Builder {
            this.imageEnum = imageEnum
            return this
        }

        fun build(): FacebookConfig {
            if (TextUtils.isEmpty(applicationId)) {
                throw IllegalArgumentException("applicationId is empty.")
            }

            val requestOptions = ArrayList<String>()
            if (isRequireEmail) {
                requestOptions.add("email")
            }

            if (isRequireFriends) {
                requestOptions.add("user_friends")
            }

            requestOptions.add("public_profile")
            return FacebookConfig(requestOptions, requireWritePermissions, behaviorOnCancel, applicationId)
        }
    }
}
