package com.github.windsekirun.rxsociallogin.intenal.utils

import java.security.SecureRandom

/**
 * RxSocialLogin
 * Class: SecureRandomUtils
 * Created by Pyxis on 10/7/18.
 *
 *
 * Description:
 */

fun randomString(len: Int): String {
    val groupAlphanumeric = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
    val secureRandom = SecureRandom()

    val sb = StringBuilder(len)
    for (i in 0 until len) {
        sb.append(groupAlphanumeric[secureRandom.nextInt(groupAlphanumeric.length)])
    }
    return sb.toString()
}
