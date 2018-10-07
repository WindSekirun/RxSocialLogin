package com.github.windsekirun.rxsociallogin.intenal.rx

import com.github.windsekirun.rxsociallogin.BaseSocialLogin
import com.github.windsekirun.rxsociallogin.intenal.exception.LoginFailedException
import com.github.windsekirun.rxsociallogin.intenal.impl.OnResponseListener
import com.github.windsekirun.rxsociallogin.intenal.model.LoginResultItem
import com.github.windsekirun.rxsociallogin.intenal.utils.Preconditions
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.MainThreadDisposable

open class SocialObservable(private val login: BaseSocialLogin) : Observable<LoginResultItem>() {

    override fun subscribeActual(observer: Observer<in LoginResultItem>?) {
        if (observer == null || !Preconditions.checkMainThread(observer)) {
            observer?.onError(LoginFailedException("Not in MainThread. cancel working."))
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
                    observer?.onError(LoginFailedException("login failed: ${item.platform}"))
                }
            }
        }
    }
}