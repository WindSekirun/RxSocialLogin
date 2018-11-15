package com.github.windsekirun.rxsociallogin.intenal.utils

import android.content.pm.PackageManager


/**
 * RxSocialLogin
 * Class: PackageExtensions
 * Created by Pyxis on 2018-11-07.
 *
 * Description:
 */

fun PackageManager.isPackageInstalled(packageName: String): Boolean {
    return try {
        this.getPackageGids(packageName)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }

}