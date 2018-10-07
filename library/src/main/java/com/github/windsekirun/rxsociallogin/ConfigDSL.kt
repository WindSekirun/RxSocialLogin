package com.github.windsekirun.rxsociallogin

import android.app.Application
import com.github.windsekirun.rxsociallogin.facebook.FacebookConfig
import com.github.windsekirun.rxsociallogin.foursquare.FoursquareConfig
import com.github.windsekirun.rxsociallogin.github.GithubConfig
import com.github.windsekirun.rxsociallogin.google.GoogleConfig
import com.github.windsekirun.rxsociallogin.intenal.impl.ConfigFunction
import com.github.windsekirun.rxsociallogin.intenal.impl.invoke
import com.github.windsekirun.rxsociallogin.intenal.model.PlatformType
import com.github.windsekirun.rxsociallogin.intenal.model.SocialConfig
import com.github.windsekirun.rxsociallogin.intenal.oauth.OAuthConfig
import com.github.windsekirun.rxsociallogin.kakao.KakaoConfig
import com.github.windsekirun.rxsociallogin.line.LineConfig
import com.github.windsekirun.rxsociallogin.linkedin.LinkedinConfig
import com.github.windsekirun.rxsociallogin.naver.NaverConfig
import com.github.windsekirun.rxsociallogin.twitch.TwitchConfig
import com.github.windsekirun.rxsociallogin.twitter.TwitterConfig
import com.github.windsekirun.rxsociallogin.vk.VKConfig

fun Application.initSocialLogin(setup: ConfigDSLBuilder.() -> Unit) {
    val builder = ConfigDSLBuilder(this)
    builder.setup()
    builder.build()
}

fun Application.initSocialLoginJava(setup: ConfigBuilder.() -> Unit) {
    val builder = ConfigBuilder(this)
    builder.setup()
    builder.build()
}

interface BuilderFunction {
    fun invoke(builder: ConfigBuilder)
}

open class BaseConfigDSLBuilder(val application: Application) {
    internal val typeMap: MutableMap<PlatformType, SocialConfig> = mutableMapOf()

    fun foursquare(clientId: String, clientSecret: String) {
        typeMap[PlatformType.FOURSQUARE] = FoursquareConfig.apply(clientId, clientSecret)
    }

    fun line(channelId: String) {
        typeMap[PlatformType.LINE] = LineConfig.apply(channelId)
    }

    fun naver(authClientId: String, authClientSecret: String, clientName: String) {
        typeMap[PlatformType.NAVER] = NaverConfig.apply(authClientId, authClientSecret, clientName)
    }

    fun twitter(consumerKey: String, consumerSecret: String) {
        typeMap[PlatformType.TWITTER] = TwitterConfig.apply(consumerKey, consumerSecret)
    }

    internal fun build() {
        RxSocialLogin.initializeInternal(application, typeMap)
    }
}

class ConfigDSLBuilder(application: Application) : BaseConfigDSLBuilder(application) {

    fun disqus(clientId: String, clientSecret: String, redirectUri: String,
               setup: (OAuthConfig.() -> Unit)? = null) {
        typeMap[PlatformType.DISQUS] = OAuthConfig.apply(clientId, clientSecret, redirectUri, invoke(setup))
    }

    fun facebook(applicationId: String, setup: (FacebookConfig.() -> Unit)? = null) {
        typeMap[PlatformType.FACEBOOK] = FacebookConfig.apply(applicationId, invoke(setup))
    }

    fun github(clientId: String, clientSecret: String, setup: (GithubConfig.() -> Unit)? = null) {
        typeMap[PlatformType.GITHUB] = GithubConfig.apply(clientId, clientSecret, invoke(setup))
    }

    fun google(clientTokenId: String, setup: (GoogleConfig.() -> Unit)? = null) {
        typeMap[PlatformType.GOOGLE] = GoogleConfig.apply(clientTokenId, invoke(setup))
    }

    fun kakao(setup: (KakaoConfig.() -> Unit)? = null) {
        typeMap[PlatformType.KAKAO] = KakaoConfig.apply(invoke(setup))
    }

    fun linkedin(clientId: String, clientSecret: String, redirectUri: String,
                 setup: (LinkedinConfig.() -> Unit)? = null) {
        typeMap[PlatformType.LINKEDIN] = LinkedinConfig.apply(clientId, clientSecret, redirectUri, invoke(setup))
    }

    fun twitch(clientId: String, clientSecret: String, redirectUri: String,
               setup: (TwitchConfig.() -> Unit)? = null) {
        typeMap[PlatformType.TWITCH] = TwitchConfig.apply(clientId, clientSecret, redirectUri, invoke(setup))
    }

    fun vk(setup: (VKConfig.() -> Unit)? = null) {
        typeMap[PlatformType.VK] = VKConfig.apply(invoke(setup))
    }
}

class ConfigBuilder(application: Application) : BaseConfigDSLBuilder(application) {

    @JvmOverloads
    fun disqus(clientId: String, clientSecret: String, redirectUri: String,
               setup: ConfigFunction<OAuthConfig>? = null) {
        typeMap[PlatformType.DISQUS] = OAuthConfig.apply(clientId, clientSecret, redirectUri, setup)
    }

    @JvmOverloads
    fun facebook(applicationId: String, setup: ConfigFunction<FacebookConfig>? = null) {
        typeMap[PlatformType.FACEBOOK] = FacebookConfig.apply(applicationId, setup)
    }

    @JvmOverloads
    fun github(clientId: String, clientSecret: String, setup: ConfigFunction<GithubConfig>? = null) {
        typeMap[PlatformType.GITHUB] = GithubConfig.apply(clientId, clientSecret, setup)
    }

    @JvmOverloads
    fun google(clientTokenId: String, setup: ConfigFunction<GoogleConfig>? = null) {
        typeMap[PlatformType.GOOGLE] = GoogleConfig.apply(clientTokenId, setup)
    }

    @JvmOverloads
    fun kakao(setup: ConfigFunction<KakaoConfig>? = null) {
        typeMap[PlatformType.KAKAO] = KakaoConfig.apply(setup)
    }

    @JvmOverloads
    fun linkedin(clientId: String, clientSecret: String, redirectUri: String,
                 setup: ConfigFunction<LinkedinConfig>? = null) {
        typeMap[PlatformType.LINKEDIN] = LinkedinConfig.apply(clientId, clientSecret, redirectUri, setup)
    }

    @JvmOverloads
    fun twitch(clientId: String, clientSecret: String, redirectUri: String,
               setup: ConfigFunction<TwitchConfig>? = null) {
        typeMap[PlatformType.TWITCH] = TwitchConfig.apply(clientId, clientSecret, redirectUri, setup)
    }

    @JvmOverloads
    fun vk(setup: ConfigFunction<VKConfig>? = null) {
        typeMap[PlatformType.VK] = VKConfig.apply(setup)
    }
}