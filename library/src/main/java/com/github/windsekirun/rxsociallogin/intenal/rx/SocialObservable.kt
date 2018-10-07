package com.github.windsekirun.rxsociallogin.intenal.rx

import android.os.Looper
import com.github.windsekirun.rxsociallogin.BaseSocialLogin
import com.github.windsekirun.rxsociallogin.RxSocialLogin
import com.github.windsekirun.rxsociallogin.intenal.exception.LoginFailedException
import com.github.windsekirun.rxsociallogin.intenal.impl.OnResponseListener
import com.github.windsekirun.rxsociallogin.intenal.model.LoginResultItem
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.MainThreadDisposable
import io.reactivex.disposables.Disposables

open class SocialObservable(private val login: BaseSocialLogin) : Observable<LoginResultItem>() {

    override fun subscribeActual(observer: Observer<in LoginResultItem>?) {
        if (observer == null || !checkMainThread(observer)) {
            observer?.onError(LoginFailedException(RxSocialLogin.EXCEPTION_MAIN_THREAD))
            return
        }

        val listener = Listener(login, observer)
        login.responseListener = listener
        observer.onSubscribe(listener)
    }

    private class Listener(val login: BaseSocialLogin, val observer: Observer<in LoginResultItem>?) :
            MainThreadDisposable(), OnResponseListener {

        override fun onDispose() {
            login.onDestroy()
        }

        override fun onResult(item: LoginResultItem) {
            if (!isDisposed) {
                val result = item.result
                if (result) { //
                    observer?.onNext(item)
                } else if (!isDisposed) {
                    observer?.onError(LoginFailedException(RxSocialLogin.EXCEPTION_FAILED_RESULT))
                }
            }
        }
    }

    companion object {

        fun <T> checkMainThread(observer: Observer<T>): Boolean {
            if (Looper.myLooper() != Looper.getMainLooper()) {
                observer.onSubscribe(Disposables.empty())
                observer.onError(LoginFailedException(RxSocialLogin.EXCEPTION_MAIN_THREAD + Thread.currentThread().name))
                return false
            }
            return true
        }
    }
}