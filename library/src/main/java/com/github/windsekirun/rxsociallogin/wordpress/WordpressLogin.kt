package com.github.windsekirun.rxsociallogin.wordpress

import android.app.Activity
import android.content.Intent
import com.github.windsekirun.rxsociallogin.OAuthConstants
import com.github.windsekirun.rxsociallogin.SocialLogin
import com.github.windsekirun.rxsociallogin.intenal.net.OkHttpHelper
import com.github.windsekirun.rxsociallogin.intenal.oauth.BaseOAuthActivity
import com.github.windsekirun.rxsociallogin.model.LoginResultItem
import com.github.windsekirun.rxsociallogin.model.PlatformType
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import pyxis.uzuki.live.richutilskt.utils.createJSONObject
import pyxis.uzuki.live.richutilskt.utils.getJSONBoolean
import pyxis.uzuki.live.richutilskt.utils.getJSONString

class WordpressLogin(activity: Activity) : SocialLogin(activity) {
    private lateinit var disposable: Disposable

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == OAuthConstants.WORDPRESS_REQUEST_CODE) {
            val jsonStr = data!!.getStringExtra(BaseOAuthActivity.RESPONSE_JSON) ?: "{}"
            analyzeResult(jsonStr)
        } else if (requestCode == OAuthConstants.WORDPRESS_REQUEST_CODE && resultCode != Activity.RESULT_OK) {
            responseFail(PlatformType.WORDPRESS)
        }
    }

    override fun onLogin() {
        val intent = Intent(activity, WordpressOAuthActivity::class.java)
        activity?.startActivityForResult(intent, OAuthConstants.WORDPRESS_REQUEST_CODE)
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
            responseFail(PlatformType.WORDPRESS)
            return
        }

        val url = "https://public-api.wordpress.com/rest/v1.1/me"
        disposable = OkHttpHelper.get(url, "Authorization" to "Bearer $accessToken")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val response = it.createJSONObject()

                    if (response == null) {
                        responseFail(PlatformType.WORDPRESS)
                        return@subscribe
                    }

                    val id = response.getJSONString("ID")
                    val username = response.getJSONString("username")
                    val email = response.getJSONString("email")
                    val profilePicture = response.getJSONString("profile_URL")
                    val emailVerified = response.getJSONBoolean("email_verified")

                    val item = LoginResultItem().apply {
                        this.id = id
                        this.name = username
                        this.email = email
                        this.profilePicture = profilePicture
                        this.emailVerified = emailVerified

                        this.result = true
                        this.platform = PlatformType.WORDPRESS
                    }

                    responseSuccess(item)
                }, {
                    responseFail(PlatformType.WORDPRESS)
                })
    }
}
