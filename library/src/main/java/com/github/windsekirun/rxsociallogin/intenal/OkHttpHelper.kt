package com.github.windsekirun.rxsociallogin.intenal

import android.util.Log
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

object OkHttpHelper {
    private val client = OkHttpClient()

    private fun getResponse(url: String, authorization: String): String {
        val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", authorization)
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
    fun get(url: String, authorization: String): Single<String> {
        return Single.create { it.onSuccess(OkHttpHelper.getResponse(url, authorization)) }
    }
}