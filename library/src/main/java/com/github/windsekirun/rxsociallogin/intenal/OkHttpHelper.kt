package com.github.windsekirun.rxsociallogin.intenal

import android.util.Log

import java.io.IOException

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

object OkHttpHelper {
    private val client = OkHttpClient()

    @JvmStatic
    @Throws(IOException::class)
    operator fun get(url: String, authorization: String): String {
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
}