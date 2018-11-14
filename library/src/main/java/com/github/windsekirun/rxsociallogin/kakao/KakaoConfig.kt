package com.github.windsekirun.rxsociallogin.kakao

import com.github.windsekirun.rxsociallogin.intenal.impl.ConfigFunction
import com.github.windsekirun.rxsociallogin.intenal.model.SocialConfig

import com.kakao.auth.AuthType

class KakaoConfig : SocialConfig() {
    var requireEmail = false
    var requireAgeRange = false
    var requireBirthday = false
    var requireGender = false
    var authType: AuthType = AuthType.KAKAO_LOGIN_ALL

    companion object {
        internal fun apply(setup: ConfigFunction<KakaoConfig>? = null): KakaoConfig {
            val config = KakaoConfig()
            setup?.invoke(config)
            return config
        }
    }
}
