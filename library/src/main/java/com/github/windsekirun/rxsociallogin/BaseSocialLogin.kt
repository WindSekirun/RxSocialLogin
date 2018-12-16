package com.github.windsekirun.rxsociallogin

import android.arch.lifecycle.Lifecycle
import android.content.Intent
import android.support.v4.app.FragmentActivity
import com.github.windsekirun.rxsociallogin.intenal.impl.OnResponseListener
import com.github.windsekirun.rxsociallogin.intenal.model.LoginResultItem
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
abstract class BaseSocialLogin constructor(childActivity: FragmentActivity) {
    internal var responseListener: OnResponseListener? = null

    protected val kakaoSDKAdapter: KakaoSDKAdapter by lazy { KakaoSDKAdapter(activity!!.applicationContext) }
    protected val compositeDisposable = CompositeDisposable()
    protected var activity: FragmentActivity? by weak(null)

    init {
        this.activity = childActivity
    }

    abstract fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)

    abstract fun login()

    open fun onDestroy() {
        compositeDisposable.clear()
    }

    @JvmOverloads
    open fun logout(clearToken: Boolean = false) {

    }

    open fun addLifecycleEvent(lifecycle: Lifecycle) {

    }

    open fun removeLifecycleEvent(lifecycle: Lifecycle) {

    }

    open fun onPauseEvent() {

    }

    protected fun callbackAsFail(exception: Exception) {
        responseListener?.onResult(null, exception)
    }

    protected fun callbackAsSuccess(loginResultItem: LoginResultItem) {
        responseListener?.onResult(loginResultItem, null)
    }
}