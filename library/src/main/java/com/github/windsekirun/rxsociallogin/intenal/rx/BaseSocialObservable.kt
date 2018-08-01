package com.github.windsekirun.rxsociallogin.intenal.rx

import com.github.windsekirun.rxsociallogin.SocialLogin
import com.github.windsekirun.rxsociallogin.impl.OnResponseListener
import com.github.windsekirun.rxsociallogin.intenal.exception.LoginFailedException
import com.github.windsekirun.rxsociallogin.intenal.utils.Preconditions
import com.github.windsekirun.rxsociallogin.model.LoginResultItem
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.MainThreadDisposable

/**
 * RxSocialLogin
 * Class: BaseSocialObservable
 * Created by pyxis on 18. 7. 11.
 *
 *
 * Description:
 */
open class BaseSocialObservable<T : SocialLogin>(private val login: T) : Observable<LoginResultItem>() {

    override fun subscribeActual(observer: Observer<in LoginResultItem>?) {
        if (!Preconditions.checkMainThread(observer)) {
            return
        }

        val listener = Listener(login, observer)
        login.responseListener = listener
        observer?.onSubscribe(listener)
    }

    private class Listener<out T : SocialLogin>(val login: T, val observer: Observer<in LoginResultItem>?) :
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
                    observer?.onError(LoginFailedException("login failed: ${item.type}"))
                }
            }
        }
    }
}