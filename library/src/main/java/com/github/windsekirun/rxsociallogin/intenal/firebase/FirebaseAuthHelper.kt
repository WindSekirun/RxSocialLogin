package com.github.windsekirun.rxsociallogin.intenal.firebase

import android.app.Activity
import com.github.windsekirun.rxsociallogin.model.LoginResultItem
import com.github.windsekirun.rxsociallogin.model.PlatformType
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.reactivex.Observable

internal fun handleSignInResult(user: FirebaseUser?, platformType: PlatformType): LoginResultItem {
    if (user == null) {
        return LoginResultItem.createFail(platformType)
    }

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
                        emitter.onNext(handleSignInResult(user, platformType))
                    } else {
                        emitter.onNext(LoginResultItem.createFail(platformType))
                    }
                }
    }
}