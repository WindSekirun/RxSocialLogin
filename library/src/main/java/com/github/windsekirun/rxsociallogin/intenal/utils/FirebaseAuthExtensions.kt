package com.github.windsekirun.rxsociallogin.intenal.utils

import android.app.Activity
import com.github.windsekirun.rxsociallogin.RxSocialLogin
import com.github.windsekirun.rxsociallogin.intenal.exception.LoginFailedException
import com.github.windsekirun.rxsociallogin.intenal.model.LoginResultItem
import com.github.windsekirun.rxsociallogin.intenal.model.PlatformType
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.reactivex.Observable

internal fun handleSignInResult(user: FirebaseUser, platformType: PlatformType): LoginResultItem {
    return LoginResultItem().apply {
        name = user.displayName ?: ""
        email = user.email ?: ""
        profilePicture = user.photoUrl?.toString() ?: ""
        id = user.uid
        emailVerified = user.isEmailVerified
        result = true
        platform = platformType
    }
}

internal fun FirebaseAuth.signInWithCredential(credential: AuthCredential, activity: Activity?,
                                               platformType: PlatformType): Observable<LoginResultItem> {
    return Observable.create { emitter ->
        this.signInWithCredential(credential)
                .addOnCompleteListener(activity as Activity) {
                    if (it.isSuccessful) {
                        val user = this.currentUser
                        if (user == null) {
                            emitter.onError(LoginFailedException(RxSocialLogin.EXCEPTION_FAILED_RESULT))
                            return@addOnCompleteListener
                        }

                        emitter.onNext(handleSignInResult(user, platformType))
                    } else {
                        emitter.onError(it.exception ?: Exception(RxSocialLogin.EXCEPTION_UNKNOWN_ERROR))
                    }
                }
    }
}