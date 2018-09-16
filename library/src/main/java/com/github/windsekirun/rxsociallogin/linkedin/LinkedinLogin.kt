package com.github.windsekirun.rxsociallogin.linkedin

import android.app.Activity
import android.content.Intent
import com.github.kittinunf.fuel.httpGet
import com.github.windsekirun.rxsociallogin.OAuthConstants
import com.github.windsekirun.rxsociallogin.RxSocialLogin
import com.github.windsekirun.rxsociallogin.SocialLogin
import com.github.windsekirun.rxsociallogin.intenal.fuel.toResultObservable
import com.github.windsekirun.rxsociallogin.intenal.oauth.BaseOAuthActivity
import com.github.windsekirun.rxsociallogin.model.LoginResultItem
import com.github.windsekirun.rxsociallogin.model.PlatformType
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import pyxis.uzuki.live.richutilskt.utils.createJSONObject
import pyxis.uzuki.live.richutilskt.utils.getJSONString

class LinkedinLogin(activity: Activity) : SocialLogin(activity) {
    private val config: LinkedinConfig by lazy { getConfig(PlatformType.LINKEDIN) as LinkedinConfig }
    private lateinit var disposable: Disposable

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == OAuthConstants.LINKEDIN_REQUEST_CODE) {
            val jsonStr = data!!.getStringExtra(BaseOAuthActivity.RESPONSE_JSON) ?: "{}"
            analyzeResult(jsonStr)
        } else if (requestCode == OAuthConstants.LINKEDIN_REQUEST_CODE && resultCode != Activity.RESULT_OK) {
            responseFail(PlatformType.LINKEDIN)
        }
    }

    override fun onLogin() {
        val intent = Intent(activity, LinkedInOAuthActivity::class.java)
        activity?.startActivityForResult(intent, OAuthConstants.LINKEDIN_REQUEST_CODE)
    }

    override fun onDestroy() {
        if (::disposable.isInitialized && !disposable.isDisposed) {
            disposable.dispose()
        }
    }

    fun toObservable() = RxSocialLogin.linkedin(this)

    private fun analyzeResult(jsonStr: String) {
        val jsonObject = jsonStr.createJSONObject()
        val accessToken = jsonObject?.getJSONString("access_token") ?: ""
        if (accessToken.isEmpty()) {
            responseFail(PlatformType.LINKEDIN)
            return
        }

        val parameters = mutableListOf("id", "picture-url", "first-name", "formatted-name")

        if (config.requireEmail) {
            parameters.add("email-address")
        }

        val url = "https://api.linkedin.com/v1/people/~:(${parameters.joinToString(",")})?format=json"

        disposable = url.httpGet()
                .header("Authorization" to "Bearer $accessToken")
                .toResultObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { result, error ->
                    if (error == null && result.component1() != null) {
                        parseUserInfo(result.component1())
                    } else {
                        responseFail(PlatformType.LINKEDIN)
                    }
                }
    }

    private fun parseUserInfo(jsonStr: String?) {
        val response = jsonStr?.createJSONObject()

        if (response == null) {
            responseFail(PlatformType.LINKEDIN)
            return
        }

        val firstName = response.getJSONString("firstName")
        val id = response.getJSONString("id")
        val formattedName = response.getJSONString("formattedName")
        val emailAddress = response.getJSONString("emailAddress")

        var pictureUrl: String? = ""
        if (response.has("pictureUrl") == true) {
            pictureUrl = response.getJSONString("pictureUrl")
        }

        val item = LoginResultItem().apply {
            this.id = id
            this.firstName = firstName
            this.name = formattedName
            this.email = emailAddress
            this.profilePicture = pictureUrl ?: ""

            this.result = true
            this.platform = PlatformType.LINKEDIN
        }

        responseSuccess(item)
    }
}
