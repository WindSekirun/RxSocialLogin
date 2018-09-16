package com.github.windsekirun.rxsociallogin.twitch

import android.app.Activity
import android.content.Intent
import com.github.windsekirun.rxsociallogin.OAuthConstants
import com.github.windsekirun.rxsociallogin.RxSocialLogin
import com.github.windsekirun.rxsociallogin.SocialLogin
import com.github.windsekirun.rxsociallogin.intenal.net.OkHttpHelper
import com.github.windsekirun.rxsociallogin.intenal.oauth.BaseOAuthActivity
import com.github.windsekirun.rxsociallogin.model.LoginResultItem
import com.github.windsekirun.rxsociallogin.model.PlatformType
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import pyxis.uzuki.live.richutilskt.utils.createJSONObject
import pyxis.uzuki.live.richutilskt.utils.getJSONString
import pyxis.uzuki.live.richutilskt.utils.isEmpty

class TwitchLogin(activity: Activity) : SocialLogin(activity) {
    private val compositeDisposable = CompositeDisposable()
    private val config: TwitchConfig by lazy { getConfig(PlatformType.TWITCH) as TwitchConfig }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == OAuthConstants.TWITCH_REQUEST_CODE) {
            val jsonStr = data!!.getStringExtra(BaseOAuthActivity.RESPONSE_JSON) ?: "{}"
            analyzeResult(jsonStr)
        } else if (requestCode == OAuthConstants.TWITCH_REQUEST_CODE && resultCode != Activity.RESULT_OK) {
            responseFail(PlatformType.TWITCH)
        }
    }

    override fun onLogin() {
        val intent = Intent(activity, TwitchOAuthActivity::class.java)
        activity?.startActivityForResult(intent, OAuthConstants.TWITCH_REQUEST_CODE)
    }

    override fun onDestroy() {
        compositeDisposable.clear()
    }

    fun toObservable() = RxSocialLogin.twitch(this)

    private fun analyzeResult(jsonStr: String) {
        val result = jsonStr.createJSONObject()
        if (result == null) {
            responseFail(PlatformType.TWITCH)
            return
        }

        val accessToken = result.getJSONString("access_token", "")
        if (accessToken.isEmpty()) {
            responseFail(PlatformType.TWITCH)
            return
        }

        val requestUrl = "https://api.twitch.tv/helix/users"
        val authorization = "Bearer $accessToken"

        val disposable = OkHttpHelper.get(requestUrl, "Authorization" to authorization)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val jsonObject = it.createJSONObject()
                    val responseArray = jsonObject?.getJSONArray("data")

                    if (responseArray == null) {
                        responseFail(PlatformType.TWITCH)
                        return@subscribe
                    }

                    val responseObject = responseArray.getJSONObject(0)

                    if (responseObject == null) {
                        responseFail(PlatformType.TWITCH)
                        return@subscribe
                    }

                    val item = LoginResultItem().apply {
                        this.result = true
                        this.platform = PlatformType.TWITCH
                        this.id = responseObject.getJSONString("id")
                        this.name = responseObject.getJSONString("display_name")
                        this.email = responseObject.getJSONString("email", "")
                        this.profilePicture = responseObject.getJSONString("profile_image_url", "")
                    }

                    responseSuccess(item)
                }) {
                    responseFail(PlatformType.TWITCH)
                }

        compositeDisposable.add(disposable)
    }
}