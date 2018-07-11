package com.github.windsekirun.rxsociallogin

import android.app.Activity
import android.app.Application
import android.content.Intent
import com.facebook.FacebookSdk
import com.github.windsekirun.rxsociallogin.facebook.FacebookConfig
import com.github.windsekirun.rxsociallogin.impl.OnResponseListener
import com.github.windsekirun.rxsociallogin.kakao.KakaoSDKAdapter
import com.github.windsekirun.rxsociallogin.model.LoginResultItem
import com.github.windsekirun.rxsociallogin.model.SocialConfig
import com.github.windsekirun.rxsociallogin.model.SocialType
import com.kakao.auth.KakaoSDK
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

    abstract fun logout()

    abstract fun logout(clearToken: Boolean)

    protected fun responseFail(socialType: SocialType) {
        responseSuccess(LoginResultItem.createFail(socialType))
    }

    protected fun responseSuccess(loginResultItem: LoginResultItem) {
        responseListener?.onResult(loginResultItem)
    }

    companion object {
        private var availableTypeMap: MutableMap<SocialType, SocialConfig> = HashMap()
        private var application: Application? = null
        private val alreadyInitializedList = ArrayList<SocialType>()

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
        fun init(application: Application, availableTypeMap: MutableMap<SocialType, SocialConfig>) {
            this.application = application

            if (!availableTypeMap.isEmpty()) {
                this.availableTypeMap = availableTypeMap
                initializeSDK()
            }
        }

        /**
         * add SocialConfig to use
         *
         * @param socialType   [SocialType] object
         * @param socialConfig [SocialConfig] object
         */
        @JvmStatic
        fun addType(socialType: SocialType, socialConfig: SocialConfig) {
            if (application == null) {
                throw RuntimeException("No context is available, please add context by declare SocialLogin.init(this) in Application class")
            }

            availableTypeMap[socialType] = socialConfig
            initializeSDK()
        }

        /**
         * remove SocialConfig in AvailableTypeMap
         *
         * @param socialType [SocialType] object
         */
        @JvmStatic
        fun removeType(socialType: SocialType) {
            availableTypeMap.remove(socialType)
        }

        private fun initializeSDK() {
            for ((key, value) in availableTypeMap) {
                if (alreadyInitializedList.contains(key)) {
                    return
                }

                alreadyInitializedList.add(key)
                when (key) {
                    SocialType.KAKAO -> initializeKakaoSDK()
//                    SocialType.TWITTER -> initializeTwitterSDK(value as TwitterConfig)
                    SocialType.FACEBOOK -> initializeFacebookSDK(value as FacebookConfig)
                }
            }
        }

        internal fun initializeKakaoSDK() {
            KakaoSDK.init(KakaoSDKAdapter(application!!.applicationContext))
        }

        private fun initializeFacebookSDK(config: FacebookConfig) {
            FacebookSdk.setApplicationId(config.applicationId)
            FacebookSdk.sdkInitialize(application)
        }

//        internal fun initializeTwitterSDK(config: TwitterConfig) {
//            val twitterConfig = com.twitter.sdk.android.core.TwitterConfig.Builder(mContext!!)
//                    .twitterAuthConfig(TwitterAuthConfig(config.getConsumerKey(), config.getConsumerSecret()))
//                    .build()
//
//            Twitter.initialize(twitterConfig)
//        }

        internal fun getConfig(type: SocialType): SocialConfig {
            if (!availableTypeMap.containsKey(type)) {
                throw RuntimeException(String.format("No config is available, please add proper config :: SocialType -> %s", type.name))
            }

            return availableTypeMap[type]!!
        }

        private fun clear() {
            availableTypeMap.clear()
            alreadyInitializedList.clear()
        }

    }
}
