package com.github.windsekirun.rxsociallogin.intenal.impl

/**
 * RxSocialLogin
 * Class: Function
 * Created by Pyxis on 10/7/18.
 *
 *
 * Description:
 */
interface ConfigFunction<T> {
    fun invoke(config: T)
}

fun <T> invoke(callback: (T.() -> Unit)? = null) = object : ConfigFunction<T> {
    override fun invoke(config: T) {
        callback?.invoke(config)
    }
}