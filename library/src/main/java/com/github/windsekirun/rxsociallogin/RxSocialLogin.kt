@file:JvmName("RxSocialLogin")
@file:JvmMultifileClass

package com.github.windsekirun.rxsociallogin

import android.app.Application
import android.content.Intent
import android.support.annotation.CheckResult
import android.support.v4.app.FragmentActivity
import com.facebook.FacebookSdk
import com.github.windsekirun.rxsociallogin.disqus.DisqusLogin
import com.github.windsekirun.rxsociallogin.facebook.FacebookConfig
import com.github.windsekirun.rxsociallogin.facebook.FacebookLogin
import com.github.windsekirun.rxsociallogin.foursquare.FoursquareLogin
import com.github.windsekirun.rxsociallogin.github.GithubLogin
import com.github.windsekirun.rxsociallogin.google.GoogleLogin
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
    private var moduleMap: WeakHashMap<PlatformType, BaseSocialLogin> = WeakHashMap()
    private var application: Application? by weak(null)

    const val EXCEPTION_FAILED_RESULT = "Failed to get results."
    const val EXCEPTION_MAIN_THREAD = "Expected to be called on the main thread but was "
    const val EXCEPTION_USER_CANCELLED = "User has cancelled the job."
    const val EXCEPTION_FOURSQUARE_INTENT = "The device doesn't have Foursquare applicaiton"
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
     *
     * @param fragmentActivity [FragmentActivity] to initialize individual Social module object.
     */
    @JvmStatic
    fun initialize(fragmentActivity: FragmentActivity) {
        val map = configMap.map {
            it.key to when (it.key) {
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
            }
        }.toMap().toMutableMap()

        moduleMap.clear()
        moduleMap.putAll(map)
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
            override fun onResult(item: LoginResultItem) {
                callback(item)
            }
        }

        val newMap = mutableMapOf<PlatformType, BaseSocialLogin>()

        moduleMap.forEach {
            val moduleObject = it.value
            moduleObject?.responseListener = listener
            newMap[it.key] = moduleObject
        }

        moduleMap.clear()
        moduleMap.putAll(newMap)
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

    internal fun getPlatformConfig(type: PlatformType): SocialConfig {
        if (!configMap.containsKey(type)) {
            throw LoginFailedException(EXCEPTION_CONFIG_MISSING)
        }

        return configMap[type]!!
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

    @CheckResult
    @JvmStatic
    @Deprecated("use RxSocialLogin.result instead. See issue #18",
            replaceWith = ReplaceWith("RxSocialLogin.result(login)",
                    "com.github.windsekirun.rxsociallogin.RxSocialLogin"))
    fun facebook(login: FacebookLogin): Observable<LoginResultItem> = RxFacebookLogin(login)

    @CheckResult
    @JvmStatic
    @Deprecated("use RxSocialLogin.result instead. See issue #18",
            replaceWith = ReplaceWith("RxSocialLogin.result(login)",
                    "com.github.windsekirun.rxsociallogin.RxSocialLogin"))
    fun kakao(login: KakaoLogin): Observable<LoginResultItem> = RxKakaoLogin(login)

    @CheckResult
    @JvmStatic
    @Deprecated("use RxSocialLogin.result instead. See issue #18",
            replaceWith = ReplaceWith("RxSocialLogin.result(login)",
                    "com.github.windsekirun.rxsociallogin.RxSocialLogin"))
    fun naver(login: NaverLogin): Observable<LoginResultItem> = RxNaverLogin(login)

    @CheckResult
    @JvmStatic
    @Deprecated("use RxSocialLogin.result instead. See issue #18",
            replaceWith = ReplaceWith("RxSocialLogin.result(login)",
                    "com.github.windsekirun.rxsociallogin.RxSocialLogin"))
    fun line(login: LineLogin): Observable<LoginResultItem> = RxLineLogin(login)

    @CheckResult
    @JvmStatic
    @Deprecated("use RxSocialLogin.result instead. See issue #18",
            replaceWith = ReplaceWith("RxSocialLogin.result(login)",
                    "com.github.windsekirun.rxsociallogin.RxSocialLogin"))
    fun twitter(login: TwitterLogin): Observable<LoginResultItem> = RxTwitterLogin(login)

    @CheckResult
    @JvmStatic
    @Deprecated("use RxSocialLogin.result instead. See issue #18",
            replaceWith = ReplaceWith("RxSocialLogin.result(login)",
                    "com.github.windsekirun.rxsociallogin.RxSocialLogin"))
    fun google(login: GoogleLogin): Observable<LoginResultItem> = RxGoogleLogin(login)

    @CheckResult
    @JvmStatic
    @Deprecated("use RxSocialLogin.result instead. See issue #18",
            replaceWith = ReplaceWith("RxSocialLogin.result(login)",
                    "com.github.windsekirun.rxsociallogin.RxSocialLogin"))
    fun github(login: GithubLogin): Observable<LoginResultItem> = RxGithubLogin(login)

    @CheckResult
    @JvmStatic
    @Deprecated("use RxSocialLogin.result instead. See issue #18",
            replaceWith = ReplaceWith("RxSocialLogin.result(login)",
                    "com.github.windsekirun.rxsociallogin.RxSocialLogin"))
    fun linkedin(login: LinkedinLogin): Observable<LoginResultItem> = RxLinkedinLogin(login)

    @CheckResult
    @JvmStatic
    @Deprecated("use RxSocialLogin.result instead. See issue #18",
            replaceWith = ReplaceWith("RxSocialLogin.result(login)",
                    "com.github.windsekirun.rxsociallogin.RxSocialLogin"))
    fun wordpress(login: WordpressLogin): Observable<LoginResultItem> = RxWordpressLogin(login)

    @CheckResult
    @JvmStatic
    @Deprecated("use RxSocialLogin.result instead. See issue #18",
            replaceWith = ReplaceWith("RxSocialLogin.result(login)",
                    "com.github.windsekirun.rxsociallogin.RxSocialLogin"))
    fun yahoo(login: YahooLogin): Observable<LoginResultItem> = RxYahooLogin(login)

    @CheckResult
    @JvmStatic
    @Deprecated("use RxSocialLogin.result instead. See issue #18",
            replaceWith = ReplaceWith("RxSocialLogin.result(login)",
                    "com.github.windsekirun.rxsociallogin.RxSocialLogin"))
    fun vk(login: VKLogin): Observable<LoginResultItem> = RxVKLogin(login)

    @CheckResult
    @JvmStatic
    @Deprecated("use RxSocialLogin.result instead. See issue #18",
            replaceWith = ReplaceWith("RxSocialLogin.result(login)",
                    "com.github.windsekirun.rxsociallogin.RxSocialLogin"))
    fun windows(login: WindowsLogin): Observable<LoginResultItem> = RxWindowsLogin(login)

    @CheckResult
    @JvmStatic
    @Deprecated("use RxSocialLogin.result instead. See issue #18",
            replaceWith = ReplaceWith("RxSocialLogin.result(login)",
                    "com.github.windsekirun.rxsociallogin.RxSocialLogin"))
    fun disqus(login: DisqusLogin): Observable<LoginResultItem> = RxDisqusLogin(login)

    @CheckResult
    @JvmStatic
    @Deprecated("use RxSocialLogin.result instead. See issue #18",
            replaceWith = ReplaceWith("RxSocialLogin.result(login)",
                    "com.github.windsekirun.rxsociallogin.RxSocialLogin"))
    fun foursquare(login: FoursquareLogin): Observable<LoginResultItem> = RxFoursquareLogin(login)

    @CheckResult
    @JvmStatic
    @Deprecated("use RxSocialLogin.result instead. See issue #18",
            replaceWith = ReplaceWith("RxSocialLogin.result(login)",
                    "com.github.windsekirun.rxsociallogin.RxSocialLogin"))
    fun twitch(login: TwitchLogin): Observable<LoginResultItem> = RxTwitchLogin(login)

    @Deprecated("use SocialObservable instead.")
    internal class RxFacebookLogin(login: FacebookLogin) : SocialObservable(login)

    @Deprecated("use SocialObservable instead.")
    internal class RxGithubLogin(login: GithubLogin) : SocialObservable(login)

    @Deprecated("use SocialObservable instead.")
    internal class RxGoogleLogin(login: GoogleLogin) : SocialObservable(login)

    @Deprecated("use SocialObservable instead.")
    internal class RxKakaoLogin(login: KakaoLogin) : SocialObservable(login)

    @Deprecated("use SocialObservable instead.")
    internal class RxLineLogin(login: LineLogin) : SocialObservable(login)

    @Deprecated("use SocialObservable instead.")
    internal class RxLinkedinLogin(login: LinkedinLogin) : SocialObservable(login)

    @Deprecated("use SocialObservable instead.")
    internal class RxNaverLogin(login: NaverLogin) : SocialObservable(login)

    @Deprecated("use SocialObservable instead.")
    internal class RxTwitterLogin(login: TwitterLogin) : SocialObservable(login)

    @Deprecated("use SocialObservable instead.")
    internal class RxWordpressLogin(login: WordpressLogin) : SocialObservable(login)

    @Deprecated("use SocialObservable instead.")
    internal class RxYahooLogin(login: YahooLogin) : SocialObservable(login)

    @Deprecated("use SocialObservable instead.")
    internal class RxVKLogin(login: VKLogin) : SocialObservable(login)

    @Deprecated("use SocialObservable instead.")
    internal class RxWindowsLogin(login: WindowsLogin) : SocialObservable(login)

    @Deprecated("use SocialObservable instead.")
    internal class RxDisqusLogin(login: DisqusLogin) : SocialObservable(login)

    @Deprecated("use SocialObservable instead.")
    internal class RxFoursquareLogin(login: FoursquareLogin) : SocialObservable(login)

    @Deprecated("use SocialObservable instead.")
    internal class RxTwitchLogin(login: TwitchLogin) : SocialObservable(login)
}