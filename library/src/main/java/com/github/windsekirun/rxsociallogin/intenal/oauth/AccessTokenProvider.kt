package com.github.windsekirun.rxsociallogin.intenal.oauth

import com.chibatching.kotpref.KotprefModel

object AccessTokenProvider : KotprefModel() {
    var githubAccessToken by stringPref("")
    var twitchAccessToken by stringPref("")
    var wordpressAccessToken by stringPref("")
}