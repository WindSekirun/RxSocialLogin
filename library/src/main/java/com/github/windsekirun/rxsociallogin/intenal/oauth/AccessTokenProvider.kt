package com.github.windsekirun.rxsociallogin.intenal.oauth

import com.chibatching.kotpref.KotprefModel

/**
 * RxSocialLogin
 * Class: AccessTokenProvider
 * Created by Pyxis on 9/16/18.
 *
 * Description:
 */
object AccessTokenProvider : KotprefModel() {
    var githubAccessToken by stringPref("")
}