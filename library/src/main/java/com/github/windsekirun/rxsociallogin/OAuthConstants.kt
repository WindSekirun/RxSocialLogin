package com.github.windsekirun.rxsociallogin

/**
 * RxSocialLogin
 * Class: OAuthConstants
 * Created by Pyxis on 9/8/18.
 *
 * Description:
 */

object OAuthConstants {
    const val GITHUB_URL = "https://github.com/login/oauth/authorize"
    const val GITHUB_OAUTH = "https://github.com/login/oauth/access_token"
    const val GITHUB_REQUEST_CODE = 100

    const val LINKEDIN_URL = "https://www.linkedin.com/oauth/v2/authorization"
    const val LINKEDIN_OAUTH = "https://www.linkedin.com/oauth/v2/accessToken"
    const val LINKEDIN_REQUEST_CODE = 101

    const val WORDPRESS_URL = "https://public-api.wordpress.com/oauth2/authorize"
    const val WORDPRESS_OAUTH = "https://public-api.wordpress.com/oauth2/token"
    const val WORDPRESS_REQUEST_CODE = 102

    const val YAHOO_URL = "https://api.login.yahoo.com/oauth2/request_auth"
    const val YAHOO_OAUTH = "https://api.login.yahoo.com/oauth2/get_token"
    const val YAHOO_REQUEST_CODE = 103

    const val VK_URL = "https://oauth.vk.com/authorize"
    const val VK_OAUTH = "https://oauth.vk.com/access_token"
    const val VK_REQUEST_CODE = 104
}