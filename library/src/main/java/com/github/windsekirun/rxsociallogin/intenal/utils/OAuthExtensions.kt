package com.github.windsekirun.rxsociallogin.intenal.utils

import android.os.Build
import android.webkit.CookieManager

fun clearCookies() {
    val cookieManager = CookieManager.getInstance()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        cookieManager.removeAllCookies { }
    } else {
        cookieManager.removeAllCookie()
    }
}

fun String.getCode(): String {
    val codeParameter = this.substring(this.lastIndexOf("?code") + 1)
    val tokenCode = codeParameter.split("=")
    val tokenFetchedIds = tokenCode[1]
    val cleanToken = tokenFetchedIds.split("&")

    return cleanToken[0]
}