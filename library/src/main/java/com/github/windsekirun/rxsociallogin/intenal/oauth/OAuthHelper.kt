package com.github.windsekirun.rxsociallogin.intenal.oauth

import android.os.Build
import android.webkit.CookieManager

/**
 * RxSocialLogin
 * Class: CookieHelper
 * Created by pyxis on 18. 8. 1.
 *
 * Description:
 */

fun clearCookies() {
    val cookieManager = CookieManager.getInstance()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        cookieManager.removeAllCookies { }
    } else {
        cookieManager.removeAllCookie()
    }
}

fun String.getCode(): String {
    val githubCode = this.substring(this.lastIndexOf("?code") + 1)
    val tokenCode = githubCode.split("=")
    val tokenFetchedIds = tokenCode[1]
    val cleanToken = tokenFetchedIds.split("&")

    return cleanToken[0]
}