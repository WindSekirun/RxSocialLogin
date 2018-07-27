package com.github.windsekirun.rxsociallogin.github

import android.app.Activity
import android.content.Intent
import com.github.windsekirun.rxsociallogin.SocialLogin
import com.github.windsekirun.rxsociallogin.model.LoginResultItem
import com.github.windsekirun.rxsociallogin.model.SocialType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GithubAuthProvider
import pyxis.uzuki.live.richutilskt.utils.createJSONObject
import pyxis.uzuki.live.richutilskt.utils.getJSONString


/**
 * RxSocialLogin
 * Class: GithubLogin
 * Created by pyxis on 18. 7. 27.
 *
 * Description:
 */
class GithubLogin(activity: Activity) : SocialLogin(activity) {
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == GithubOAuthConstants.GITHUB_REQUEST_CODE) {
            val jsonStr = data!!.getStringExtra(GithubOAuthConstants.RESPONSE_JSON) ?: "{}"
            analyzeResult(jsonStr)
        }
    }

    override fun onLogin() {
        val intent = Intent(activity, GithubOAuthActivity::class.java)
        activity!!.startActivityForResult(intent, GithubOAuthConstants.GITHUB_REQUEST_CODE)
    }

    override fun onDestroy() {

    }

    override fun logout(clearToken: Boolean) {
        FirebaseAuth.getInstance().signOut()
    }

    private fun analyzeResult(jsonStr: String) {
        val jsonObject = jsonStr.createJSONObject()
        val accessToken = jsonObject?.getJSONString("access_token") ?: ""
        if (accessToken.isEmpty()) return

        val credential = GithubAuthProvider.getCredential(accessToken)
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val user = firebaseAuth.currentUser
                        handleSignInResult(user)
                    } else {
                        responseFail(SocialType.GITHUB)
                    }
                }
    }

    private fun handleSignInResult(user: FirebaseUser?) {
        if (user == null) {
            responseFail(SocialType.GITHUB)
            return
        }

        val item = LoginResultItem().apply {
            this.name = user.displayName ?: ""
            this.email = user.email ?: ""
            this.profilePicture = user.photoUrl?.toString() ?: ""
            this.id = user.uid
            this.emailVerified = user.isEmailVerified
            this.result = true
            this.type = SocialType.GOOGLE
        }

        responseSuccess(item)
    }
}
