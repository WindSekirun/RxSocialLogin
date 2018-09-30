package com.github.windsekirun.rxsociallogin

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.support.annotation.CheckResult
import android.support.v4.app.FragmentActivity
import com.facebook.FacebookSdk
import com.github.windsekirun.rxsociallogin.disqus.DisqusLogin
import com.github.windsekirun.rxsociallogin.facebook.FacebookConfig
import com.github.windsekirun.rxsociallogin.facebook.FacebookLogin
import com.github.windsekirun.rxsociallogin.foursquare.FoursquareLogin
import com.github.windsekirun.rxsociallogin.github.GithubLogin
import com.github.windsekirun.rxsociallogin.google.GoogleLogin
import com.github.windsekirun.rxsociallogin.intenal.rx.SocialObservable
import com.github.windsekirun.rxsociallogin.intenal.exception.LoginFailedException
import com.github.windsekirun.rxsociallogin.intenal.impl.OnResponseListener
import com.github.windsekirun.rxsociallogin.intenal.model.LoginResultItem
import com.github.windsekirun.rxsociallogin.intenal.model.PlatformType
import com.github.windsekirun.rxsociallogin.intenal.model.SocialConfig
import com.github.windsekirun.rxsociallogin.intenal.rx.BaseSocialObservable
import com.github.windsekirun.rxsociallogin.intenal.utils.weak
import com.github.windsekirun.rxsociallogin.kakao.KakaoLogin
import com.github.windsekirun.rxsociallogin.kakao.KakaoSDKAdapter
import com.github.windsekirun.rxsociallogin.line.LineLogin
import com.github.windsekirun.rxsociallogin.linkedin.LinkedinLogin
import com.github.windsekirun.rxsociallogin.naver.NaverLogin
import com.github.windsekirun.rxsociallogin.twitch.TwitchLogin
import com.github.windsekirun.rxsociallogin.twitter.TwitterConfig
import com.github.windsekirun.rxsociallogin.twitter.TwitterLogin
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
import io.reactivex.disposables.CompositeDisposable
import java.util.*

abstract class RxSocialLogin @JvmOverloads constructor(childActivity: FragmentActivity? = null) {
    internal var responseListener: OnResponseListener? = null

    protected val kakaoSDKAdapter: KakaoSDKAdapter by lazy { KakaoSDKAdapter(activity!!.applicationContext) }
    protected val compositeDisposable = CompositeDisposable()
    protected var activity: FragmentActivity? by weak(null)
    protected val TAG = RxSocialLogin::class.java.simpleName

    init {
        if (childActivity != null) { // using given activity object when creating instance
            this.activity = childActivity
        } else {
            this.activity = RxSocialLogin.activityReference
            if (this.activity == null) throw LoginFailedException(RxSocialLogin.NOT_HAVE_APPLICATION)
        }
    }

    abstract fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)

    abstract fun login()

    open fun onDestroy() {
        compositeDisposable.clear()
    }

    @JvmOverloads
    open fun logout(clearToken: Boolean = false) {

    }

    protected fun callbackFail(platformType: PlatformType) {
        callbackItem(LoginResultItem.createFail(platformType))
    }

    protected fun callbackItem(loginResultItem: LoginResultItem) {
        responseListener?.onResult(loginResultItem)
    }

    companion object {
        private var availableTypeMap: MutableMap<PlatformType, SocialConfig> = HashMap()
        private var application: Application? by weak(null)
        private var activityReference: FragmentActivity? by weak(null)
        private val alreadyInitializedList = ArrayList<PlatformType>()

        const val NOT_HAVE_APPLICATION = "No context is available, please declare RxSocialLogin.init(this)"
        const val NOT_NEED_ACTIVITY_RESULT = "Not need to call onActivityResult in "

        private val lifecycleCallbacks = object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
                setActivityWeakReference(activity)
            }

            override fun onActivityStarted(activity: Activity) {
                setActivityWeakReference(activity)
            }

            override fun onActivityResumed(activity: Activity) {
                setActivityWeakReference(activity)
            }

            override fun onActivityDestroyed(activity: Activity) {}
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle?) {}
        }

        /**
         * Initialize RxSocialLogin
         * @param application Application
         */
        @JvmStatic
        fun init(application: Application) {
            this.application = application
            this.application?.registerActivityLifecycleCallbacks(lifecycleCallbacks)

            clear()
        }

        /**
         * add SocialConfig to use
         *
         * @param platformType   [PlatformType] object
         * @param socialConfig [SocialConfig] object
         */
        @JvmStatic
        fun addType(platformType: PlatformType, socialConfig: SocialConfig) {
            if (application == null) {
                throw LoginFailedException(NOT_HAVE_APPLICATION)
            }

            availableTypeMap[platformType] = socialConfig
            initPlatform()
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

        @CheckResult
        @JvmStatic
        fun result(login: RxSocialLogin): Observable<LoginResultItem> = SocialObservable(login)

        @CheckResult
        @JvmStatic
        fun result(vararg login: RxSocialLogin): Observable<LoginResultItem> = Observable.concat(login.map { SocialObservable(it) })

        internal fun getPlatformConfig(type: PlatformType): SocialConfig {
            if (!availableTypeMap.containsKey(type)) {
                throw LoginFailedException(String.format("No config is available :: Platform -> ${type.name}"))
            }

            return availableTypeMap[type]!!
        }

        @Deprecated("use SocialObservable instead.")
        internal class RxFacebookLogin(login: FacebookLogin) : BaseSocialObservable<FacebookLogin>(login)

        @Deprecated("use SocialObservable instead.")
        internal class RxGithubLogin(login: GithubLogin) : BaseSocialObservable<GithubLogin>(login)

        @Deprecated("use SocialObservable instead.")
        internal class RxGoogleLogin(login: GoogleLogin) : BaseSocialObservable<GoogleLogin>(login)

        @Deprecated("use SocialObservable instead.")
        internal class RxKakaoLogin(login: KakaoLogin) : BaseSocialObservable<KakaoLogin>(login)

        @Deprecated("use SocialObservable instead.")
        internal class RxLineLogin(login: LineLogin) : BaseSocialObservable<LineLogin>(login)

        @Deprecated("use SocialObservable instead.")
        internal class RxLinkedinLogin(login: LinkedinLogin) : BaseSocialObservable<LinkedinLogin>(login)

        @Deprecated("use SocialObservable instead.")
        internal class RxNaverLogin(login: NaverLogin) : BaseSocialObservable<NaverLogin>(login)

        @Deprecated("use SocialObservable instead.")
        internal class RxTwitterLogin(login: TwitterLogin) : BaseSocialObservable<TwitterLogin>(login)

        @Deprecated("use SocialObservable instead.")
        internal class RxWordpressLogin(login: WordpressLogin) : BaseSocialObservable<WordpressLogin>(login)

        @Deprecated("use SocialObservable instead.")
        internal class RxYahooLogin(login: YahooLogin) : BaseSocialObservable<YahooLogin>(login)

        @Deprecated("use SocialObservable instead.")
        internal class RxVKLogin(login: VKLogin) : BaseSocialObservable<VKLogin>(login)

        @Deprecated("use SocialObservable instead.")
        internal class RxWindowsLogin(login: WindowsLogin) : BaseSocialObservable<WindowsLogin>(login)

        @Deprecated("use SocialObservable instead.")
        internal class RxDisqusLogin(login: DisqusLogin) : BaseSocialObservable<DisqusLogin>(login)

        @Deprecated("use SocialObservable instead.")
        internal class RxFoursquareLogin(login: FoursquareLogin) : BaseSocialObservable<FoursquareLogin>(login)

        @Deprecated("use SocialObservable instead.")
        internal class RxTwitchLogin(login: TwitchLogin) : BaseSocialObservable<TwitchLogin>(login)

        private fun initPlatform() {
            for ((key, value) in availableTypeMap) {
                if (alreadyInitializedList.contains(key)) {
                    continue
                }

                alreadyInitializedList.add(key)
                when (key) {
                    PlatformType.KAKAO -> initKakao()
                    PlatformType.TWITTER -> initTwitter(value as TwitterConfig)
                    PlatformType.FACEBOOK -> initFacebook(value as FacebookConfig)
                    PlatformType.VK -> initVK()
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
            val twitterConfig = com.twitter.sdk.android.core.TwitterConfig.Builder(application!!)
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

        private fun clear() {
            availableTypeMap.clear()
            alreadyInitializedList.clear()
        }

        private fun setActivityWeakReference(activity: Activity) {
            if ((activityReference == null || activity != activityReference) && (activity is FragmentActivity)) {
                activityReference = activity
            }
        }
    }
}