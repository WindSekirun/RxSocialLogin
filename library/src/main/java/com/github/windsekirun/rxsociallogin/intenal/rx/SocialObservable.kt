package com.github.windsekirun.rxsociallogin.intenal.rx

import android.os.Looper
import com.github.windsekirun.rxsociallogin.base.BaseSocialLogin
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
        override fun onResult(item: LoginResultItem?, error: Throwable?) {
            if (!isDisposed) {
                when {
                    item != null && item.result -> observer?.onNext(item)
                    error != null -> observer?.onError(error)
                    else -> observer?.onError(LoginFailedException(RxSocialLogin.EXCEPTION_UNKNOWN_ERROR))
                }
            }
        }

        override fun onDispose() {
            login.onDestroy()
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