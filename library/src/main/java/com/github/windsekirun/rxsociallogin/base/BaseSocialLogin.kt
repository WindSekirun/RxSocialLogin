package com.github.windsekirun.rxsociallogin.base

import android.content.Intent
import androidx.fragment.app.FragmentActivity
import com.github.windsekirun.rxsociallogin.RxSocialLogin.getPlatformConfig
import com.github.windsekirun.rxsociallogin.intenal.impl.OnResponseListener
import com.github.windsekirun.rxsociallogin.intenal.model.LoginResultItem
import com.github.windsekirun.rxsociallogin.intenal.model.PlatformType
import com.github.windsekirun.rxsociallogin.intenal.model.SocialConfig
import com.github.windsekirun.rxsociallogin.intenal.utils.weak
import com.github.windsekirun.rxsociallogin.kakao.KakaoSDKAdapter
import io.reactivex.disposables.CompositeDisposable

/**
 * RxSocialLogin
 * Class: BaseSocialLogin
 * Created by Pyxis on 10/7/18.
 *
 * Description:
 */
abstract class BaseSocialLogin<T : SocialConfig> constructor(val activity: FragmentActivity) {
    internal var responseListener: OnResponseListener? = null

    abstract fun getPlatformType(): PlatformType

    @Suppress("UNCHECKED_CAST") val config: T by lazy { getPlatformConfig(getPlatformType()) as T }

    protected val kakaoSDKAdapter: KakaoSDKAdapter by lazy { KakaoSDKAdapter(activity.applicationContext) }
    protected val compositeDisposable = CompositeDisposable()

    abstract fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)

    abstract fun login()

    open fun onDestroy() {
        compositeDisposable.clear()
    }

    @JvmOverloads
    open fun logout(clearToken: Boolean = false) {

    }

    protected fun callbackAsFail(exception: Exception) {
        responseListener?.onResult(null, exception)
    }

    protected fun callbackAsSuccess(loginResultItem: LoginResultItem) {
        responseListener?.onResult(loginResultItem, null)
    }
}