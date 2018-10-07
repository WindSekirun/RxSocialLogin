package com.github.windsekirun.rxsociallogin.foursquare

import android.content.Intent
import android.support.v4.app.FragmentActivity
import com.foursquare.android.nativeoauth.FoursquareOAuth
import com.github.kittinunf.fuel.httpGet
import com.github.windsekirun.rxsociallogin.BaseSocialLogin
import com.github.windsekirun.rxsociallogin.RxSocialLogin
import com.github.windsekirun.rxsociallogin.RxSocialLogin.getPlatformConfig
import com.github.windsekirun.rxsociallogin.intenal.utils.toResultObservable
import com.github.windsekirun.rxsociallogin.intenal.model.LoginResultItem
import com.github.windsekirun.rxsociallogin.intenal.model.PlatformType
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import pyxis.uzuki.live.richutilskt.utils.*

class FoursquareLogin constructor(activity: FragmentActivity) : BaseSocialLogin(activity) {
    private val config: FoursquareConfig by lazy { getPlatformConfig(PlatformType.FOURSQUARE) as FoursquareConfig }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CONNECT_REQUEST_CODE) {
            val codeResponse = FoursquareOAuth.getAuthCodeFromResult(resultCode, data)
            if (codeResponse.code != null && !codeResponse.code.isEmpty()) {
                val intent = FoursquareOAuth.getTokenExchangeIntent(activity, config.clientId,
                        config.clientSecret, codeResponse.code)
                activity!!.startActivityForResult(intent, EXCHANGE_REQUEST_CODE)
            } else {
                callbackFail(PlatformType.FOURSQUARE)
            }
        } else if (requestCode == EXCHANGE_REQUEST_CODE) {
            val tokenResponse = FoursquareOAuth.getTokenFromResult(resultCode, data)
            if (tokenResponse.accessToken != null && !tokenResponse.accessToken.isEmpty()) {
                getUserInfo(tokenResponse.accessToken)
            } else {
                callbackFail(PlatformType.FOURSQUARE)
            }
        }
    }

    override fun login() {
        val intent = FoursquareOAuth.getConnectIntent(activity, config.clientId)
        if (intent.resolveActivity(activity!!.packageManager) != null) {
            activity!!.startActivityForResult(intent, CONNECT_REQUEST_CODE)
        } else {
            callbackFail(PlatformType.FOURSQUARE)
        }
    }

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated("use RxSocialLogin.result instead")
    fun toObservable() = RxSocialLogin.foursquare(this)

    private fun getUserInfo(token: String) {
        val requestUrl = "https://api.foursquare.com/v2/users/self?oauth_token=$token" +
                "&v=${System.currentTimeMillis().asDateString("yyyyMMdd")}"

        val disposable = requestUrl.httpGet()
                .header("Content-Type" to "application/json")
                .toResultObservable()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { result, error ->
                    if (error == null && result.component1() != null) {
                        parseUserJson(result.component1())
                    } else {
                        callbackFail(PlatformType.FOURSQUARE)
                    }
                }

        compositeDisposable.add(disposable)
    }

    private fun parseUserJson(jsonStr: String?) {
        val jsonObject = jsonStr?.createJSONObject()

        if (jsonObject == null) {
            callbackFail(PlatformType.FOURSQUARE)
            return
        }

        val response = jsonObject.getJSONObject("response")
        val user = response?.getJSONObject("user")

        if (user == null) {
            callbackFail(PlatformType.FOURSQUARE)
            return
        }
        val photo = user.getJSONObject("photo")
        val contact = user.getJSONObject("contact")

        val profilePicture = photo.getJSONString("prefix") +
                photo.getJSONString("suffix").replace("/", "")
        val email = contact.getJSONString("email")
        val name = "${user.getJSONString("firstName")} ${user.getJSONString("lastName")}"

        val item = LoginResultItem().apply {
            this.id = user.getJSONString("id")
            this.name = name
            this.firstName = user.getJSONString("firstName")
            this.gender = user.getJSONString("gender")
            this.birthday = (user.getJSONInt("birthday").toLong() * 1000).asDateString("yyyy-MM-dd")
            this.profilePicture = profilePicture
            this.email = email
            this.platform = PlatformType.FOURSQUARE
            this.result = true
        }

        callbackItem(item)
    }

    companion object {
        const val CONNECT_REQUEST_CODE = 1001
        const val EXCHANGE_REQUEST_CODE = 1004
    }
}
