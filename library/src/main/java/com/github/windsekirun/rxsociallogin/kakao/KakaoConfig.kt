package com.github.windsekirun.rxsociallogin.kakao

import com.github.windsekirun.rxsociallogin.model.SocialConfig
import java.io.Serializable
import java.util.*

class KakaoConfig private constructor(val requestOptions: ArrayList<String>) : SocialConfig(), Serializable {

    class Builder {
        private var isRequireEmail = false
        private var isRequireAgeRange = false
        private var isRequireBirthday = false
        private var isRequireGender = false

        fun setRequireEmail(): Builder {
            isRequireEmail = true
            return this
        }

        fun setRequireAgeRange(): Builder {
            isRequireAgeRange = true
            return this
        }

        fun setRequireBirthday(): Builder {
            isRequireBirthday = true
            return this
        }

        fun setRequireGender(): Builder {
            isRequireGender = true
            return this
        }

        fun build(): KakaoConfig {
            // v 1.2.5 migrate with V1 -> V2
            // according to https://tinyurl.com/ycaf5yua
            val requestOptions = ArrayList<String>()
            requestOptions.add("properties.nickname")
            requestOptions.add("properties.profile_image")
            requestOptions.add("properties.thumbnail_image")

            if (isRequireEmail) {
                requestOptions.add("kakao_account.email")
            }

            if (isRequireAgeRange) {
                requestOptions.add("kakao_account.age_range")
            }

            if (isRequireBirthday) {
                requestOptions.add("kakao_account.birthday")
            }

            if (isRequireGender) {
                requestOptions.add("kakao_account.gender")
            }

            return KakaoConfig(requestOptions)
        }
    }
}
