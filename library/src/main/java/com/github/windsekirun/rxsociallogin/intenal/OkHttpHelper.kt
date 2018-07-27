package com.github.windsekirun.rxsociallogin.intenal

import android.util.Log
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

object OkHttpHelper {
    private val client = OkHttpClient()

    private fun getResponse(url: String, key: String, value: String): String {
        val request = Request.Builder()
                .addHeader(key, value)
                .url(url)
                .build()

        try {
            val res = client.newCall(request).execute()
            return res.body()!!.string()
        } catch (e: IOException) {
            Log.e(OkHttpHelper::class.java.simpleName, e.message)
            throw e
        }
    }

    @JvmStatic
    fun get(url: String, key: String, value: String): Single<String> {
        return Single.create { it.onSuccess(OkHttpHelper.getResponse(url, key, value)) }
    }
}