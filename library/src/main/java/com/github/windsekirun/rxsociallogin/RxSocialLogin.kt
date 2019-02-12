@file:JvmName("RxSocialLogin")
@file:JvmMultifileClass

package com.github.windsekirun.rxsociallogin

import android.app.Application
import android.content.Intent
import androidx.annotation.CheckResult
import androidx.fragment.app.FragmentActivity
import com.facebook.FacebookSdk
import com.github.windsekirun.rxsociallogin.amazon.AmazonLogin
import com.github.windsekirun.rxsociallogin.base.BaseSocialLogin
import com.github.windsekirun.rxsociallogin.bitbucket.BitbucketLogin
import com.github.windsekirun.rxsociallogin.discord.DiscordLogin
import com.github.windsekirun.rxsociallogin.disqus.DisqusLogin
import com.github.windsekirun.rxsociallogin.facebook.FacebookConfig
import com.github.windsekirun.rxsociallogin.facebook.FacebookLogin
import com.github.windsekirun.rxsociallogin.foursquare.FoursquareLogin
import com.github.windsekirun.rxsociallogin.github.GithubLogin
import com.github.windsekirun.rxsociallogin.gitlab.GitlabLogin
import com.github.windsekirun.rxsociallogin.google.GoogleLogin
import com.github.windsekirun.rxsociallogin.instagram.InstagramLogin
import com.github.windsekirun.rxsociallogin.intenal.exception.LoginFailedException
import com.github.windsekirun.rxsociallogin.intenal.impl.OnResponseListener
import com.github.windsekirun.rxsociallogin.intenal.model.LoginResultItem
import com.github.windsekirun.rxsociallogin.intenal.model.PlatformType
import com.github.windsekirun.rxsociallogin.intenal.model.PlatformType.*
import com.github.windsekirun.rxsociallogin.intenal.model.SocialConfig
import com.github.windsekirun.rxsociallogin.intenal.rx.SocialObservable
import com.github.windsekirun.rxsociallogin.intenal.utils.weak
import com.github.windsekirun.rxsociallogin.kakao.KakaoLogin
import com.github.windsekirun.rxsociallogin.kakao.KakaoSDKAdapter
import com.github.windsekirun.rxsociallogin.line.LineLogin
import com.github.windsekirun.rxsociallogin.linkedin.LinkedinLogin
import com.github.windsekirun.rxsociallogin.naver.NaverLogin
import com.github.windsekirun.rxsociallogin.twitch.TwitchLogin
import com.github.windsekirun.rxsociallogin.twitter.TwitterConfig
import com.github.windsekirun.rxsociallogin.twitter.TwitterLogin
import com.github.windsekirun.rxsociallogin.twitter.TwitterSDKConfig
import com.github.windsekirun.rxsociallogin.vk.VKLogin
import com.github.windsekirun.rxsociallogin.windows.WindowsLogin
import com.github.windsekirun.rxsociallogin.wordpress.WordpressLogin
import com.github.windsekirun.rxsociallogin.yahoo.YahooLogin
import com.kakao.auth.KakaoSDK
import com.twitter.sdk.android.core.Twitter
import com.twitter.sdk.android.core.TwitterAuthConfig
import com.vk.sdk.VKAccessToken
import com.vk.sdk.VKAccessTokenTracker
import com.vk.sdk.VKSdk
import io.reactivex.Observable
import java.util.*

object RxSocialLogin {
    private var configMap: MutableMap<PlatformType, SocialConfig> = HashMap()
    private var moduleMap: WeakHashMap<PlatformType, BaseSocialLogin<*>> = WeakHashMap()
    private var application: Application? by weak(null)

    const val EXCEPTION_FAILED_RESULT = "Failed to get results."
    const val EXCEPTION_MAIN_THREAD = "Expected to be called on the main thread but was "
    const val EXCEPTION_USER_CANCELLED = "User has cancelled the job."
    const val EXCEPTION_FOURSQUARE_INTENT = "The device doesn't have Foursquare applicaiton"
    const val EXCEPTION_UNKNOWN_ERROR = "Unknown error"
    private const val EXCEPTION_CONFIG_MISSING = "Config object is missing."

    /**
     * Initialize 'RxSocialLogin' in Java.
     * In Kotlin, use [Application.initSocialLogin] instead.
     */
    @JvmStatic
    fun initSocialLogin(application: Application, callback: BuilderFunction) {
        application.initSocialLoginJava {
            callback.invoke(this)
        }
    }

    /**
     * Initialize 'Social module object' in once by Configs on Application class
     * and register Lifecycle Event for handling proper lifecycle-aware process in library.
     *
     * @param fragmentActivity [FragmentActivity] to initialize individual Social module object.
     */
    @JvmStatic
    fun initialize(fragmentActivity: FragmentActivity) {
        val map = configMap.map {
            it.key to when (it.key) {
                AMAZON -> AmazonLogin(fragmentActivity)
                BITBUCKET -> BitbucketLogin(fragmentActivity)
                KAKAO -> KakaoLogin(fragmentActivity)
                GOOGLE -> GoogleLogin(fragmentActivity)
                FACEBOOK -> FacebookLogin(fragmentActivity)
                LINE -> LineLogin(fragmentActivity)
                NAVER -> NaverLogin(fragmentActivity)
                TWITTER -> TwitterLogin(fragmentActivity)
                GITHUB -> GithubLogin(fragmentActivity)
                LINKEDIN -> LinkedinLogin(fragmentActivity)
                WORDPRESS -> WordpressLogin(fragmentActivity)
                YAHOO -> YahooLogin(fragmentActivity)
                VK -> VKLogin(fragmentActivity)
                DISQUS -> DisqusLogin(fragmentActivity)
                FOURSQUARE -> FoursquareLogin(fragmentActivity)
                TWITCH -> TwitchLogin(fragmentActivity)
                WINDOWS -> WindowsLogin(fragmentActivity)
                DISCORD -> DiscordLogin(fragmentActivity)
                GITLAB -> GitlabLogin(fragmentActivity)
                INSTAGRAM -> InstagramLogin(fragmentActivity)
            }
        }.toMap().toMutableMap()

        map.values.map {
            it.addLifecycleEvent(fragmentActivity.lifecycle)
        }.toMutableList()

        moduleMap.clear()
        moduleMap.putAll(map)
    }

    /**
     * remove lifecycle event in Modules
     */
    @JvmStatic
    fun removeLifecycleEvent(fragmentActivity: FragmentActivity) {
        moduleMap.values.map {
            it.removeLifecycleEvent(fragmentActivity.lifecycle)
        }.toMutableList()
    }

    /**
     * Try Login of [BaseSocialLogin] using given [PlatformType]
     */
    @JvmStatic
    fun login(platformType: PlatformType) {
        val socialLogin = moduleMap[platformType]
                ?: throw LoginFailedException(EXCEPTION_CONFIG_MISSING)
        socialLogin.login()
    }

    @JvmStatic
    @JvmOverloads
    fun logout(platformType: PlatformType, clearToken: Boolean = false) {
        val socialLogin = moduleMap[platformType]
                ?: throw LoginFailedException(EXCEPTION_CONFIG_MISSING)
        socialLogin.logout(clearToken)
    }

    /**
     * Receive [FragmentActivity.onActivityResult] event to handle result of platform process
     */
    @JvmStatic
    fun activityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        moduleMap.values.forEach {
            it?.onActivityResult(requestCode, resultCode, data)
        }
    }

    /**
     * Observe SocialLogin result by RxJava2 way
     */
    @CheckResult
    @JvmStatic
    @JvmOverloads
    fun result(fragmentActivity: FragmentActivity? = null): Observable<LoginResultItem> {
        if (moduleMap.isEmpty() && fragmentActivity != null) initialize(fragmentActivity)
        return Observable.merge(moduleMap.values.map { SocialObservable(it) })
    }

    /**
     * Observe SocialLogin result by traditional (Listener) way
     */
    @JvmOverloads
    fun result(callback: (LoginResultItem) -> Unit, fragmentActivity: FragmentActivity? = null) {
        if (moduleMap.isEmpty() && fragmentActivity != null) initialize(fragmentActivity)

        val listener = object : OnResponseListener {
            override fun onResult(item: LoginResultItem?, error: Throwable?) {
                if (item != null && error == null) {
                    callback(item)
                } else if (error != null) {
                    throw LoginFailedException(error)
                } else {
                    throw LoginFailedException(EXCEPTION_UNKNOWN_ERROR)
                }
            }
        }

        val newMap = mutableMapOf<PlatformType, BaseSocialLogin<*>>()

        moduleMap.forEach {
            val moduleObject = it.value
            moduleObject?.responseListener = listener
            newMap[it.key] = moduleObject
        }

        moduleMap.clear()
        moduleMap.putAll(newMap)
    }

    /**
     * get [SocialConfig] object with given [PlatformType]
     */
    @JvmStatic
    fun getPlatformConfig(type: PlatformType): SocialConfig {
        if (!configMap.containsKey(type)) {
            throw LoginFailedException(EXCEPTION_CONFIG_MISSING)
        }

        return configMap[type]!!
    }

    /**
     * set [SocialConfig] object with given [PlatformType]
     * Additional settings for platforms not created through the Application class are not allowed.
     */
    fun setPlatformConfig(type: PlatformType, config: SocialConfig) {
        if (!configMap.containsKey(type)) {
            throw LoginFailedException(EXCEPTION_CONFIG_MISSING)
        }

        configMap[type] = config
    }

    internal fun initializeInternal(application: Application, map: Map<PlatformType, SocialConfig>) {
        this.application = application
        configMap.putAll(map)
        configMap.forEach {
            when (it.key) {
                KAKAO -> initKakao()
                TWITTER -> initTwitter(it.value as TwitterConfig)
                FACEBOOK -> initFacebook(it.value as FacebookConfig)
                VK -> initVK()
                else -> {
                }
            }
        }
    }

    private fun initKakao() {
        KakaoSDK.init(KakaoSDKAdapter(application!!.applicationContext))
    }

    private fun initFacebook(config: FacebookConfig) {
        FacebookSdk.setApplicationId(config.applicationId)
    }

    private fun initTwitter(config: TwitterConfig) {
        val twitterConfig = TwitterSDKConfig(application!!)
                .twitterAuthConfig(TwitterAuthConfig(config.consumerKey, config.consumerSecret))
                .build()

        Twitter.initialize(twitterConfig)
    }

    private fun initVK() {
        val vkAccessTokenTracker = object : VKAccessTokenTracker() {
            override fun onVKAccessTokenChanged(oldToken: VKAccessToken?, newToken: VKAccessToken?) {
                if (newToken == null) {
                    // VKAccessToken is invalid
                }
            }
        }

        vkAccessTokenTracker.startTracking()
        VKSdk.initialize(application)
    }
}