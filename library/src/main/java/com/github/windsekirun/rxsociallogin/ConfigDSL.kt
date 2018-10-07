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

/**
 * RxSocialLogin
 * Class: ConfigDSL
 * Created by Pyxis on 10/7/18.
 *
 * Description:
 */
fun Application.initSocialLogin(setup: ConfigDSLBuilder.() -> Unit) {
    val builder = ConfigDSLBuilder(this)
    builder.setup()
    builder.build()
}

interface BuilderFunction {
    fun invoke(builder: ConfigDSLBuilder)
}

class ConfigDSLBuilder(val application: Application) {
    private val typeMap: MutableMap<PlatformType, SocialConfig> = mutableMapOf()

    /**
     * Initialize DisqusLogin
     */
    fun disqus(clientId: String, clientSecret: String, redirectUri: String,
               setup: (OAuthConfig.() -> Unit)? = null) {
        typeMap[PlatformType.DISQUS] = OAuthConfig.apply(clientId, clientSecret, redirectUri, invoke(setup))
    }

    /**
     * Initialize DisqusLogin
     * Additional methods for compatibility with Java
     */
    @JvmOverloads
    fun disqus(clientId: String, clientSecret: String, redirectUri: String,
               setup: ConfigFunction<OAuthConfig>? = null) {
        typeMap[PlatformType.DISQUS] = OAuthConfig.apply(clientId, clientSecret, redirectUri, setup)
    }

    /**
     * Initialize FacebookLogin
     */
    fun facebook(applicationId: String, setup: (FacebookConfig.() -> Unit)? = null) {
        typeMap[PlatformType.FACEBOOK] = FacebookConfig.apply(applicationId, invoke(setup))
    }

    /**
     * Initialize FacebookLogin
     * Additional methods for compatibility with Java
     */
    @JvmOverloads
    fun facebook(applicationId: String, setup: ConfigFunction<FacebookConfig>? = null) {
        typeMap[PlatformType.FACEBOOK] = FacebookConfig.apply(applicationId, setup)
    }

    /**
     * Initialize FoursquareLogin
     */
    fun foursquare(clientId: String, clientSecret: String) {
        typeMap[PlatformType.FOURSQUARE] = FoursquareConfig.apply(clientId, clientSecret)
    }

    /**
     * Initialize GithubLogin
     */
    fun github(clientId: String, clientSecret: String,
               setup: (GithubConfig.() -> Unit)? = null) {
        typeMap[PlatformType.GITHUB] = GithubConfig.apply(clientId, clientSecret, invoke(setup))
    }

    /**
     * Initialize GithubLogin
     * Additional methods for compatibility with Java
     */
    @JvmOverloads
    fun github(clientId: String, clientSecret: String,
               setup: ConfigFunction<GithubConfig>? = null) {
        typeMap[PlatformType.GITHUB] = GithubConfig.apply(clientId, clientSecret, setup)
    }

    /**
     * Initialize GoogleLogin
     */
    fun google(clientTokenId: String, setup: (GoogleConfig.() -> Unit)? = null) {
        typeMap[PlatformType.GOOGLE] = GoogleConfig.apply(clientTokenId, invoke(setup))
    }

    /**
     * Initialize GoogleLogin
     * Additional methods for compatibility with Java
     */
    @JvmOverloads
    fun google(clientTokenId: String, setup: ConfigFunction<GoogleConfig>? = null) {
        typeMap[PlatformType.GOOGLE] = GoogleConfig.apply(clientTokenId, setup)
    }

    /**
     * Initialize KakaoLogin
     */
    fun kakao(setup: (KakaoConfig.() -> Unit)? = null) {
        typeMap[PlatformType.KAKAO] = KakaoConfig.apply(invoke(setup))
    }

    /**
     * Initialize KakaoLogin
     * Additional methods for compatibility with Java
     */
    @JvmOverloads
    fun kakao(setup: ConfigFunction<KakaoConfig>? = null) {
        typeMap[PlatformType.KAKAO] = KakaoConfig.apply(setup)
    }

    internal fun build() {
        RxSocialLogin.initializeInternal(application, typeMap)
    }
}