package com.github.windsekirun.rxsociallogin.google

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import com.github.windsekirun.rxsociallogin.SocialLogin
import com.github.windsekirun.rxsociallogin.model.LoginResultItem
import com.github.windsekirun.rxsociallogin.model.SocialType
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.api.GoogleApiClient
import java.util.*


class GoogleLogin(activity: AppCompatActivity) : SocialLogin(activity) {

    private val mGoogleApiClient: GoogleApiClient

    init {
        val googleConfig = getConfig(SocialType.GOOGLE) as GoogleConfig

        val builder = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        if (googleConfig.requireEmail) {
            builder.requestEmail()
        }

        val gso = builder.build()

        mGoogleApiClient = GoogleApiClient.Builder(activity)
                .enableAutoManage(activity) { _ ->

                }
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            handleSignInResult(result)
        }
    }

    override fun onLogin() {
        if (mGoogleApiClient.isConnected) mGoogleApiClient.clearDefaultAccountAndReconnect()
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
        activity?.startActivityForResult(signInIntent, REQUEST_CODE_SIGN_IN)
    }

    override fun onDestroy() {

    }

    override fun logout(clearToken: Boolean) {
        if (mGoogleApiClient.isConnected) mGoogleApiClient.clearDefaultAccountAndReconnect()
    }

    private fun handleSignInResult(result: GoogleSignInResult) {
        if (result.isSuccess) {
            val account = result.signInAccount

            if (account == null) {
                responseFail(SocialType.GOOGLE)
                return
            }

            val item = LoginResultItem().apply {
                this.name = account.displayName ?: ""
                this.email = account.email ?: ""
                this.id = account.id ?: ""
                this.accessToken = account.idToken ?: ""
                this.result = true
                this.type = SocialType.GOOGLE
            }

            responseSuccess(item)
        } else {
            responseFail(SocialType.GOOGLE)
        }
    }

    companion object {
        private val REQUEST_CODE_SIGN_IN = 19629
    }
}
