package com.github.windsekirun.rxsociallogin.apple

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import com.github.windsekirun.rxsociallogin.BaseSocialLogin
import com.github.windsekirun.rxsociallogin.OAuthConstants
import com.github.windsekirun.rxsociallogin.RxSocialLogin
import com.github.windsekirun.rxsociallogin.intenal.exception.LoginFailedException
import com.github.windsekirun.rxsociallogin.intenal.model.LoginResultItem
import com.github.windsekirun.rxsociallogin.intenal.model.PlatformType
import com.github.windsekirun.rxsociallogin.intenal.oauth.LoginOAuthActivity
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import io.github.sooakim.rxfirebase.rxSingle
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableSingleObserver
import pyxis.uzuki.live.richutilskt.utils.createJSONObject
import pyxis.uzuki.live.richutilskt.utils.getJSONString
import java.util.*

class AppleLogin constructor(fragmentActivity: FragmentActivity) :
    BaseSocialLogin(fragmentActivity) {
    private val config: AppleConfig by lazy { RxSocialLogin.getPlatformConfig(PlatformType.APPLE) as AppleConfig }
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    }

    override fun login() {
        val fragmentActivity = activity as? Activity ?: return
        val provider = OAuthProvider.newBuilder(OAuthConstants.APPLE_DOMAIN)
            .setScopes(config.scopes.map { it.name.toLowerCase(Locale.ENGLISH) })
            .addCustomParameter(QUERY_LOCALE, Locale.getDefault().toString())

        val singleObserver = object : DisposableSingleObserver<AuthResult>() {
            override fun onSuccess(t: AuthResult) {
                val user = t.user ?: return
                callbackAsSuccess(LoginResultItem().apply {
                    name = user.displayName ?: ""
                    email = user.email ?: ""
                    profilePicture = user.photoUrl?.toString() ?: ""
                    id = user.uid
                    emailVerified = user.isEmailVerified
                    result = true
                    platform = PlatformType.APPLE
                })
            }

            override fun onError(e: Throwable) {
               callbackAsFail(LoginFailedException(RxSocialLogin.EXCEPTION_FAILED_RESULT))
            }
        }

        auth.pendingAuthResult?.rxSingle()
            ?.subscribeWith(singleObserver)
            ?.let(compositeDisposable::add)
            ?: auth.startActivityForSignInWithProvider(fragmentActivity, provider.build())
                .rxSingle()
                .subscribeWith(singleObserver)
                .let(compositeDisposable::add)
    }

    override fun logout(clearToken: Boolean) {
        super.logout(clearToken)
        auth.signOut()
    }

    companion object {
        private const val QUERY_LOCALE = "locale"
    }
}