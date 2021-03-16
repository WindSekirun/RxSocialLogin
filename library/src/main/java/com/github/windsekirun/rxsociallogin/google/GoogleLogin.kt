package com.github.windsekirun.rxsociallogin.google

import android.content.Intent
import androidx.fragment.app.FragmentActivity
import com.github.windsekirun.rxsociallogin.BaseSocialLogin
import com.github.windsekirun.rxsociallogin.RxSocialLogin.EXCEPTION_FAILED_RESULT
import com.github.windsekirun.rxsociallogin.RxSocialLogin.EXCEPTION_USER_CANCELLED
import com.github.windsekirun.rxsociallogin.RxSocialLogin.getPlatformConfig
import com.github.windsekirun.rxsociallogin.intenal.exception.LoginFailedException
import com.github.windsekirun.rxsociallogin.intenal.model.PlatformType
import com.github.windsekirun.rxsociallogin.intenal.utils.signInWithCredential
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class GoogleLogin constructor(activity: androidx.fragment.app.FragmentActivity) : BaseSocialLogin(activity) {
    private val googleApiClient: GoogleApiClient by lazy {
        val builder = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(config.clientTokenId)

        if (config.requireEmail) {
            builder.requestEmail()
        }

        val googleSignInOptions = builder.build()

        GoogleApiClient.Builder(activity!!)
                .enableAutoManage(activity) { _ -> }
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build()
    }

    private val config: GoogleConfig by lazy { getPlatformConfig(PlatformType.GOOGLE) as GoogleConfig }
    private val auth = FirebaseAuth.getInstance()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java) ?: return
                authWithFirebase(account)
            } catch (e: ApiException) {
                callbackAsFail(LoginFailedException(EXCEPTION_USER_CANCELLED, e))
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

    private fun authWithFirebase(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        val disposable = auth.signInWithCredential(credential, activity, PlatformType.GOOGLE)
                .subscribe({
                    callbackAsSuccess(it)
                }, {
                    callbackAsFail(LoginFailedException(EXCEPTION_FAILED_RESULT, it))
                })
        compositeDisposable.add(disposable)
    }

    companion object {
        private const val REQUEST_CODE_SIGN_IN = 19629
    }
}