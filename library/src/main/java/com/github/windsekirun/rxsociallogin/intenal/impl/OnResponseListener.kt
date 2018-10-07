package com.github.windsekirun.rxsociallogin.intenal.impl

import com.github.windsekirun.rxsociallogin.intenal.model.LoginResultItem

interface OnResponseListener {
    fun onResult(item: LoginResultItem?, error: Throwable?)
}