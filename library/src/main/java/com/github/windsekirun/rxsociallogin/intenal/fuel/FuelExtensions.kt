package com.github.windsekirun.rxsociallogin.intenal.fuel

import com.github.kittinunf.fuel.core.*
import com.github.kittinunf.fuel.core.deserializers.StringDeserializer
import com.github.kittinunf.result.Result
import io.reactivex.Single
import java.nio.charset.Charset

/**
 * RxSocialLogin
 * Class: FuelExtensions
 * Created by Pyxis on 9/16/18.
 *
 * Description:
 */

fun Request.toResultObservable(charset: Charset = Charsets.UTF_8) = toResultObservable(StringDeserializer(charset))

private fun <T : Any> Request.toResultObservable(deserializable: Deserializable<T>): Single<Result<T, FuelError>> =
        Single.create { emitter ->
            val (_, _, result) = response(deserializable)
            emitter.onSuccess(result)
            emitter.setCancellable { this.cancel() }
        }

