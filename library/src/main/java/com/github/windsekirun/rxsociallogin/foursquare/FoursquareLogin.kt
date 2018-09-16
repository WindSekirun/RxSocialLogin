package com.github.windsekirun.rxsociallogin.foursquare

import android.app.Activity
import android.content.Intent
import com.foursquare.android.nativeoauth.FoursquareOAuth
import com.github.windsekirun.rxsociallogin.RxSocialLogin
import com.github.windsekirun.rxsociallogin.SocialLogin
import com.github.windsekirun.rxsociallogin.intenal.net.OkHttpHelper
import com.github.windsekirun.rxsociallogin.model.LoginResultItem
import com.github.windsekirun.rxsociallogin.model.PlatformType
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import pyxis.uzuki.live.richutilskt.utils.*

class FoursquareLogin(activity: Activity) : SocialLogin(activity) {
    private val compositeDisposable = CompositeDisposable()
    private val config: FoursquareConfig by lazy { getConfig(PlatformType.FOURSQUARE) as FoursquareConfig }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CONNECT_REQUEST_CODE) {
            val codeResponse = FoursquareOAuth.getAuthCodeFromResult(resultCode, data)
            if (codeResponse.code != null && !codeResponse.code.isEmpty()) {
                val intent = FoursquareOAuth.getTokenExchangeIntent(activity, config.clientId,
                        config.clientSecret, codeResponse.code)
                activity!!.startActivityForResult(intent, EXCHANGE_REQUEST_CODE)
            } else {
                responseFail(PlatformType.FOURSQUARE)
            }
        } else if (requestCode == EXCHANGE_REQUEST_CODE) {
            val tokenResponse = FoursquareOAuth.getTokenFromResult(resultCode, data)
            if (tokenResponse.accessToken != null && !tokenResponse.accessToken.isEmpty()) {
                getUserInfo(tokenResponse.accessToken)
            } else {
                responseFail(PlatformType.FOURSQUARE)
            }
        }
    }

    override fun onLogin() {
        val intent = FoursquareOAuth.getConnectIntent(activity, config.clientId)
        if (intent.resolveActivity(activity!!.packageManager) != null) {
            activity!!.startActivityForResult(intent, CONNECT_REQUEST_CODE)
        } else {
            responseFail(PlatformType.FOURSQUARE)
        }
    }

    override fun onDestroy() {
        compositeDisposable.clear()
    }

    fun toObservable() = RxSocialLogin.foursquare(this)

    private fun getUserInfo(token: String) {
        val requestUrl = "https://api.foursquare.com/v2/users/self?oauth_token=$token" +
                "&v=${System.currentTimeMillis().asDateString("yyyyMMdd")}"

        val disposable = OkHttpHelper.get(requestUrl, "Content-Type" to "application/json")
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val jsonObject = it.createJSONObject()

                    if (jsonObject == null) {
                        responseFail(PlatformType.FOURSQUARE)
                        return@subscribe
                    }

                    val response = jsonObject.getJSONObject("response")
                    val user = response?.getJSONObject("user")

                    if (user == null) {
                        responseFail(PlatformType.FOURSQUARE)
                        return@subscribe
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

                    responseSuccess(item)
                }) {
                    responseFail(PlatformType.FOURSQUARE)
                }

        compositeDisposable.add(disposable)
    }

    companion object {
        const val CONNECT_REQUEST_CODE = 1001
        const val EXCHANGE_REQUEST_CODE = 1004
    }
}
