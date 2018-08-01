package com.github.windsekirun.rxsociallogin.linkedin

import android.app.Activity
import android.content.Intent
import com.github.windsekirun.rxsociallogin.SocialLogin
import com.github.windsekirun.rxsociallogin.intenal.net.OkHttpHelper
import com.github.windsekirun.rxsociallogin.intenal.oauth.BaseOAuthActivity
import com.github.windsekirun.rxsociallogin.model.LoginResultItem
import com.github.windsekirun.rxsociallogin.model.SocialType
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import pyxis.uzuki.live.richutilskt.utils.createJSONObject
import pyxis.uzuki.live.richutilskt.utils.getJSONString

class LinkedinLogin(activity: Activity) : SocialLogin(activity) {
    private val config: LinkedinConfig by lazy { getConfig(SocialType.LINKEDIN) as LinkedinConfig }
    private lateinit var disposable: Disposable

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == LinkedInOAuthConstants.LINKEDIN_REQUEST_CODE) {
            val jsonStr = data!!.getStringExtra(BaseOAuthActivity.RESPONSE_JSON) ?: "{}"
            analyzeResult(jsonStr)
        } else if (resultCode != Activity.RESULT_OK) {
            responseFail(SocialType.GITHUB)
        }
    }

    override fun onLogin() {
        val intent = Intent(activity, LinkedInOAuthActivity::class.java)
        activity?.startActivityForResult(intent, LinkedInOAuthConstants.LINKEDIN_REQUEST_CODE)
    }

    override fun onDestroy() {
        if (::disposable.isInitialized && !disposable.isDisposed) {
            disposable.dispose()
        }
    }

    private fun analyzeResult(jsonStr: String) {
        val jsonObject = jsonStr.createJSONObject()
        val accessToken = jsonObject?.getJSONString("access_token") ?: ""
        if (accessToken.isEmpty()) {
            responseFail(SocialType.LINKEDIN)
            return
        }

        val parameters = mutableListOf("id", "picture-url", "first-name", "formatted-name")

        if (config.requireEmail) {
            parameters.add("email-address")
        }

        val url = "https://api.linkedin.com/v1/people/~:(${parameters.joinToString(",")})?format=json"

        disposable = OkHttpHelper.get(url, "Authorization" to "Bearer $accessToken")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val response = it.createJSONObject()

                    val firstName = response?.getString("firstName")
                    val id = response?.getString("id")
                    val formattedName = response?.getString("formattedName")
                    val emailAddress = response?.getString("emailAddress")

                    var pictureUrl: String? = ""
                    if (response?.has("pictureUrl") == true) {
                        pictureUrl = response.getString("pictureUrl")
                    }

                    val item = LoginResultItem().apply {
                        this.id = id ?: ""
                        this.firstName = firstName ?: ""
                        this.name = formattedName ?: ""
                        this.email = emailAddress ?: ""
                        this.profilePicture = pictureUrl ?: ""

                        this.result = true
                        this.type = SocialType.LINKEDIN
                    }

                    responseSuccess(item)
                }, {
                    responseFail(SocialType.LINKEDIN)
                })
    }
}
