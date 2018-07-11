package com.github.windsekirun.rxsociallogin.impl

import com.github.windsekirun.rxsociallogin.model.LoginResultItem

interface OnResponseListener {
    fun onResult(item: LoginResultItem)
}