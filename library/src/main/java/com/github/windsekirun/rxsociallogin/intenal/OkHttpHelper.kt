package com.github.windsekirun.rxsociallogin.intenal

import android.util.Log
import io.reactivex.Single
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException


object OkHttpHelper {
    private val client = OkHttpClient()

    private fun requestGet(url: String, header: Pair<String, String>): String {
        val request = Request.Builder()
                .addHeader(header.first, header.second)
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

    private fun requestPost(url: String, header: Pair<String, String>,
                            formBody: Array<out Pair<String, String>>): String {
        val builder = FormBody.Builder()

        for ((key, value) in formBody) {
            builder.add(key, value)
        }

        val request = Request.Builder()
                .url(url)
                .addHeader(header.first, header.second)
                .post(builder.build())
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
    fun get(url: String, header: Pair<String, String>): Single<String> {
        return Single.create { it.onSuccess(OkHttpHelper.requestGet(url, header)) }
    }

    @JvmStatic
    fun post(url: String, header: Pair<String, String>,
             formBody: Array<out Pair<String, String>>): Single<String> {
        return Single.create { it.onSuccess(OkHttpHelper.requestPost(url, header, formBody)) }
    }
}