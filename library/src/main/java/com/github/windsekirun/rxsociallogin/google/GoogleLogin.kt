package com.github.windsekirun.rxsociallogin.google

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import com.github.windsekirun.rxsociallogin.RxSocialLogin
import com.github.windsekirun.rxsociallogin.SocialLogin
import com.github.windsekirun.rxsociallogin.intenal.firebase.signInWithCredential
import com.github.windsekirun.rxsociallogin.model.PlatformType
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class GoogleLogin(activity: AppCompatActivity) : SocialLogin(activity) {
    private val googleApiClient: GoogleApiClient
    private val auth = FirebaseAuth.getInstance()

    init {
        val googleConfig = getConfig(PlatformType.GOOGLE) as GoogleConfig
        val builder = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(googleConfig.clientTokenId)

        if (googleConfig.requireEmail) {
            builder.requestEmail()
        }

        val googleSignInOptions = builder.build()

        googleApiClient = GoogleApiClient.Builder(activity)
                .enableAutoManage(activity) { _ -> }
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                authWithFirebase(account)
            } catch (e: ApiException) {
                responseFail(PlatformType.GOOGLE)
            }
        }
    }

    override fun login() {
        if (googleApiClient.isConnected) googleApiClient.clearDefaultAccountAndReconnect()
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
        activity?.startActivityForResult(signInIntent, REQUEST_CODE_SIGN_IN)
    }

    override fun logout(clearToken: Boolean) {
        if (googleApiClient.isConnected) googleApiClient.clearDefaultAccountAndReconnect()
    }

    fun toObservable() = RxSocialLogin.google(this)

    private fun authWithFirebase(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        val disposable = auth.signInWithCredential(credential, activity, PlatformType.GOOGLE)
                .subscribe({ responseSuccess(it) }, {})
        compositeDisposable.add(disposable)
    }

    companion object {
        private const val REQUEST_CODE_SIGN_IN = 19629
    }
}