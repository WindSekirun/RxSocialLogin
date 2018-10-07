package com.github.windsekirun.rxsociallogin.intenal.utils

import com.github.kittinunf.fuel.core.Deserializable
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.deserializers.StringDeserializer
import com.github.kittinunf.fuel.core.response
import com.github.kittinunf.result.Result
import io.reactivex.Single
import java.nio.charset.Charset

fun Request.toResultObservable(charset: Charset = Charsets.UTF_8) = toResultObservable(StringDeserializer(charset))

private fun <T : Any> Request.toResultObservable(deserializable: Deserializable<T>): Single<Result<T, FuelError>> =
        Single.create { emitter ->
            val (_, _, result) = response(deserializable)
            emitter.onSuccess(result)
            emitter.setCancellable { this.cancel() }
        }

