package com.github.windsekirun.rxsociallogin.github

import android.app.Activity
import android.content.Intent
import com.github.windsekirun.rxsociallogin.OAuthConstants
import com.github.windsekirun.rxsociallogin.RxSocialLogin
import com.github.windsekirun.rxsociallogin.SocialLogin
import com.github.windsekirun.rxsociallogin.intenal.firebase.signInWithCredential
import com.github.windsekirun.rxsociallogin.intenal.oauth.BaseOAuthActivity
import com.github.windsekirun.rxsociallogin.model.PlatformType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GithubAuthProvider
import io.reactivex.disposables.Disposable
import pyxis.uzuki.live.richutilskt.utils.createJSONObject
import pyxis.uzuki.live.richutilskt.utils.getJSONString

class GithubLogin(activity: Activity) : SocialLogin(activity) {
    private val auth = FirebaseAuth.getInstance()
    private lateinit var disposable: Disposable

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == OAuthConstants.GITHUB_REQUEST_CODE) {
            val jsonStr = data!!.getStringExtra(BaseOAuthActivity.RESPONSE_JSON) ?: "{}"
            analyzeResult(jsonStr)
        } else if (requestCode == OAuthConstants.GITHUB_REQUEST_CODE && resultCode != Activity.RESULT_OK) {
            responseFail(PlatformType.GITHUB)
        }
    }

    override fun onLogin() {
        val intent = Intent(activity, GithubOAuthActivity::class.java)
        activity?.startActivityForResult(intent, OAuthConstants.GITHUB_REQUEST_CODE)
    }

    override fun onDestroy() {
        if (::disposable.isInitialized && !disposable.isDisposed) {
            disposable.dispose()
        }
    }

    override fun logout(clearToken: Boolean) {
        FirebaseAuth.getInstance().signOut()
    }

    fun toObservable() = RxSocialLogin.github(this)

    private fun analyzeResult(jsonStr: String) {
        val jsonObject = jsonStr.createJSONObject()
        val accessToken = jsonObject?.getJSONString("access_token") ?: ""
        if (accessToken.isEmpty()) {
            responseFail(PlatformType.GITHUB)
            return
        }

        val credential = GithubAuthProvider.getCredential(accessToken)
        disposable = auth.signInWithCredential(credential, activity, PlatformType.GITHUB)
                .subscribe({ responseSuccess(it) }, {})
    }
}