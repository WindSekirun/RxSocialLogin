package com.github.windsekirun.rxsociallogin.google

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import com.github.windsekirun.rxsociallogin.SocialLogin
import com.github.windsekirun.rxsociallogin.intenal.signInWithCredential
import com.github.windsekirun.rxsociallogin.model.SocialType
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import io.reactivex.disposables.Disposable

class GoogleLogin(activity: AppCompatActivity) : SocialLogin(activity) {
    private val mGoogleApiClient: GoogleApiClient
    private val auth = FirebaseAuth.getInstance()
    private lateinit var disposable: Disposable

    init {
        val googleConfig = getConfig(SocialType.GOOGLE) as GoogleConfig
        val builder = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(googleConfig.clientTokenId)

        if (googleConfig.requireEmail) {
            builder.requestEmail()
        }

        val googleSignInOptions = builder.build()

        mGoogleApiClient = GoogleApiClient.Builder(activity)
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
                responseFail(SocialType.GOOGLE)
            }
        }
    }

    override fun onLogin() {
        if (mGoogleApiClient.isConnected) mGoogleApiClient.clearDefaultAccountAndReconnect()
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
        activity?.startActivityForResult(signInIntent, REQUEST_CODE_SIGN_IN)
    }

    override fun onDestroy() {
        if (::disposable.isInitialized && !disposable.isDisposed) {
            disposable.dispose()
        }
    }

    override fun logout(clearToken: Boolean) {
        if (mGoogleApiClient.isConnected) mGoogleApiClient.clearDefaultAccountAndReconnect()
    }

    private fun authWithFirebase(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        disposable = auth.signInWithCredential(credential, activity, SocialType.GOOGLE)
                .subscribe({ responseSuccess(it) }, {})
    }

    companion object {
        private const val REQUEST_CODE_SIGN_IN = 19629
    }
}