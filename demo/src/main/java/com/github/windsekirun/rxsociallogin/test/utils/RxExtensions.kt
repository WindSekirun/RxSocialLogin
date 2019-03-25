package com.github.windsekirun.rxsociallogin.test.utils

import android.util.Log
import io.reactivex.Observable
import io.reactivex.annotations.CheckReturnValue
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer

/**
 * RxSocialLogin
 * Class: RxExtensions
 * Created by Pyxis on 2019-02-07.
 *
 * Description:
 */
@CheckReturnValue
fun <T> Observable<T>.subscribe(callback: (T?, Throwable?) -> Unit): Disposable {
    return this.subscribe({
        callback.invoke(it, null)
    }, {
        callback.invoke(null, it)
    })
}

@JvmField
val ignoreError = Consumer<Throwable> { t -> Log.e("ignore", t.message, t) }

fun Disposable?.safeDispose() {
    if (this != null && !this.isDisposed) this.dispose()
}