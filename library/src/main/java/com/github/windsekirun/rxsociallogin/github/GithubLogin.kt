package com.github.windsekirun.rxsociallogin.github

import android.app.Activity
import android.content.Intent
import com.github.windsekirun.rxsociallogin.SocialLogin
import com.github.windsekirun.rxsociallogin.intenal.signInWithCredential
import com.github.windsekirun.rxsociallogin.model.SocialType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GithubAuthProvider
import io.reactivex.disposables.Disposable
import pyxis.uzuki.live.richutilskt.utils.createJSONObject
import pyxis.uzuki.live.richutilskt.utils.getJSONString

class GithubLogin(activity: Activity) : SocialLogin(activity) {
    private val auth = FirebaseAuth.getInstance()
    private lateinit var disposable: Disposable

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == GithubOAuthConstants.GITHUB_REQUEST_CODE) {
            val jsonStr = data!!.getStringExtra(GithubOAuthConstants.RESPONSE_JSON) ?: "{}"
            analyzeResult(jsonStr)
        }
    }

    override fun onLogin() {
        val intent = Intent(activity, GithubOAuthActivity::class.java)
        activity?.startActivityForResult(intent, GithubOAuthConstants.GITHUB_REQUEST_CODE)
    }

    override fun onDestroy() {
        if (::disposable.isInitialized && !disposable.isDisposed) {
            disposable.dispose()
        }
    }

    override fun logout(clearToken: Boolean) {
        FirebaseAuth.getInstance().signOut()
    }

    private fun analyzeResult(jsonStr: String) {
        val jsonObject = jsonStr.createJSONObject()
        val accessToken = jsonObject?.getJSONString("access_token") ?: ""
        if (accessToken.isEmpty()) return

        val credential = GithubAuthProvider.getCredential(accessToken)
        disposable = auth.signInWithCredential(credential, activity, SocialType.GITHUB)
                .subscribe({ responseSuccess(it) }, {})
    }
}