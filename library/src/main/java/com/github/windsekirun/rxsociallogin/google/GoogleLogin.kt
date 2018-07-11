package com.github.windsekirun.rxsociallogin.google

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import com.github.windsekirun.rxsociallogin.SocialLogin
import com.github.windsekirun.rxsociallogin.model.LoginResultItem
import com.github.windsekirun.rxsociallogin.model.SocialType
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class GoogleLogin(activity: AppCompatActivity) : SocialLogin(activity) {
    private val mGoogleApiClient: GoogleApiClient
    private val firebaseAuth = FirebaseAuth.getInstance()

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
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                authWithFirebase(account)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
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

    }

    override fun logout(clearToken: Boolean) {
        if (mGoogleApiClient.isConnected) mGoogleApiClient.clearDefaultAccountAndReconnect()
    }

    private fun handleSignInResult(user: FirebaseUser?) {
        if (user == null) {
            responseFail(SocialType.GOOGLE)
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

    private fun authWithFirebase(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity as Activity) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        val user = firebaseAuth.currentUser
                        handleSignInResult(user)
                    } else {
                        responseFail(SocialType.GOOGLE)
                    }
                }
    }

    companion object {
        private const val REQUEST_CODE_SIGN_IN = 19629
    }
}
