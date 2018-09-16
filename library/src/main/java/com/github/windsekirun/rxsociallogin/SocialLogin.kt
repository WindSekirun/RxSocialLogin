package com.github.windsekirun.rxsociallogin

import android.app.Activity
import android.app.Application
import android.content.Intent
import com.facebook.FacebookSdk
import com.github.windsekirun.rxsociallogin.facebook.FacebookConfig
import com.github.windsekirun.rxsociallogin.impl.OnResponseListener
import com.github.windsekirun.rxsociallogin.intenal.exception.LoginFailedException
import com.github.windsekirun.rxsociallogin.kakao.KakaoSDKAdapter
import com.github.windsekirun.rxsociallogin.model.LoginResultItem
import com.github.windsekirun.rxsociallogin.model.PlatformType
import com.github.windsekirun.rxsociallogin.model.SocialConfig
import com.github.windsekirun.rxsociallogin.twitter.TwitterConfig
import com.kakao.auth.KakaoSDK
import com.twitter.sdk.android.core.Twitter
import com.twitter.sdk.android.core.TwitterAuthConfig
import com.vk.sdk.VKAccessToken
import com.vk.sdk.VKAccessTokenTracker
import com.vk.sdk.VKSdk
import java.lang.ref.WeakReference
import java.util.*


/**
 * SocialLogin
 * Class: SocialLogin
 * Created by Pyxis on 2017-10-27.
 *
 *
 * Description:
 */

abstract class SocialLogin(activity: Activity) {
    var responseListener: OnResponseListener? = null
    private val mActivityWeakReference: WeakReference<Activity> = WeakReference(activity)
    protected val kakaoSDKAdapter = KakaoSDKAdapter(activity.applicationContext)

    protected val activity: Activity?
        get() = mActivityWeakReference.get()

    abstract fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)

    abstract fun onLogin()

    abstract fun onDestroy()

    @JvmOverloads
    open fun logout(clearToken: Boolean = false) {

    }

    protected fun responseFail(platformType: PlatformType) {
        responseSuccess(LoginResultItem.createFail(platformType))
    }

    protected fun responseSuccess(loginResultItem: LoginResultItem) {
        responseListener?.onResult(loginResultItem)
    }

    companion object {
        private var sAvailableTypeMap: MutableMap<PlatformType, SocialConfig> = HashMap()
        private var application: Application? = null
        private val alreadyInitializedList = ArrayList<PlatformType>()

        /**
         * Initialize SocialLogin
         * @param application Application
         */
        @JvmStatic
        fun init(application: Application) {
            this.application = application
            clear()
        }

        /**
         * Initialize SocialLogin with pre-configured AvailableTypeMap
         *
         * @param availableTypeMap
         */
        @JvmStatic
        fun init(application: Application, availableTypeMap: MutableMap<PlatformType, SocialConfig>) {
            this.application = application

            if (!availableTypeMap.isEmpty()) {
                this.sAvailableTypeMap = availableTypeMap
                initializeSDK()
            }
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
                throw LoginFailedException("No context is available, please declare SocialLogin.init(this)")
            }

            sAvailableTypeMap[platformType] = socialConfig
            initializeSDK()
        }

        /**
         * remove SocialConfig in AvailableTypeMap
         *
         * @param platformType [PlatformType] object
         */
        @JvmStatic
        fun removeType(platformType: PlatformType) {
            sAvailableTypeMap.remove(platformType)
        }

        private fun initializeSDK() {
            for ((key, value) in sAvailableTypeMap) {
                if (alreadyInitializedList.contains(key)) {
                    continue
                }

                alreadyInitializedList.add(key)
                when (key) {
                    PlatformType.KAKAO -> initializeKakaoSDK()
                    PlatformType.TWITTER -> initializeTwitterSDK(value as TwitterConfig)
                    PlatformType.FACEBOOK -> initializeFacebookSDK(value as FacebookConfig)
                    PlatformType.VK -> initializeVKSDK()
                    else -> {
                    }
                }
            }
        }

        fun getConfig(type: PlatformType): SocialConfig {
            if (!sAvailableTypeMap.containsKey(type)) {
                throw LoginFailedException(String.format("No config is available :: Platform -> ${type.name}"))
            }

            return sAvailableTypeMap[type]!!
        }

        private fun initializeKakaoSDK() {
            KakaoSDK.init(KakaoSDKAdapter(application!!.applicationContext))
        }

        private fun initializeFacebookSDK(config: FacebookConfig) {
            FacebookSdk.setApplicationId(config.applicationId)
            FacebookSdk.sdkInitialize(application)
        }

        private fun initializeTwitterSDK(config: TwitterConfig) {
            val twitterConfig = com.twitter.sdk.android.core.TwitterConfig.Builder(application!!)
                    .twitterAuthConfig(TwitterAuthConfig(config.consumerKey, config.consumerSecret))
                    .build()

            Twitter.initialize(twitterConfig)
        }

        private fun initializeVKSDK() {
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
            sAvailableTypeMap.clear()
            alreadyInitializedList.clear()
        }

    }
}