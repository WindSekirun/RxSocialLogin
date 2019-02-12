package com.github.windsekirun.rxsociallogin.amazon

import android.content.Intent
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import com.amazon.identity.auth.device.AuthError
import com.amazon.identity.auth.device.api.Listener
import com.amazon.identity.auth.device.api.authorization.*
import com.amazon.identity.auth.device.api.workflow.RequestContext
import com.github.windsekirun.rxsociallogin.RxSocialLogin
import com.github.windsekirun.rxsociallogin.base.BaseSocialLogin
import com.github.windsekirun.rxsociallogin.intenal.exception.LoginFailedException
import com.github.windsekirun.rxsociallogin.intenal.model.LoginResultItem
import com.github.windsekirun.rxsociallogin.intenal.model.PlatformType


/**
 * RxSocialLogin
 * Class: AmazonLogin
 * Created by Pyxis on 2019-02-11.
 *
 * Description: https://login.amazon.com/android
 */
class AmazonLogin constructor(activity: FragmentActivity) : BaseSocialLogin<AmazonConfig>(activity) {
    private val requestContext: RequestContext by lazy { RequestContext.create(activity) }

    init {
        requestContext.registerListener(object : AuthorizeListener() {
            override fun onSuccess(p0: AuthorizeResult?) {
                fetchProfile()
            }

            override fun onCancel(p0: AuthCancellation?) {
                callbackAsFail(LoginFailedException(RxSocialLogin.EXCEPTION_USER_CANCELLED))
            }

            override fun onError(p0: AuthError?) {
                callbackAsFail(LoginFailedException(RxSocialLogin.EXCEPTION_UNKNOWN_ERROR))
            }

        })
    }

    override fun getPlatformType(): PlatformType = PlatformType.AMAZON

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

    }

    override fun login() {
        AuthorizationManager.authorize(AuthorizeRequest.Builder(requestContext)
                .addScopes(ProfileScope.profile())
                .build())
    }

    override fun logout(clearToken: Boolean) {
        super.logout(clearToken)
        AuthorizationManager.signOut(activity, object : Listener<Void, AuthError> {
            override fun onSuccess(p0: Void?) {

            }

            override fun onError(p0: AuthError?) {

            }
        })
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        val scopes = arrayOf(ProfileScope.profile(), ProfileScope.postalCode())
        AuthorizationManager.getToken(activity, scopes, object : Listener<AuthorizeResult, AuthError> {
            override fun onSuccess(result: AuthorizeResult) {
                if (result.accessToken != null) {
                    /* The user is signed in */
                    fetchProfile()
                } else {
                    /* The user is not signed in */
                }
            }

            override fun onError(ae: AuthError) {
                /* The user is not signed in */
            }
        })
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        requestContext.onResume()
    }

    private fun fetchProfile() {
        User.fetch(activity, object : Listener<User, AuthError> {
            override fun onSuccess(p0: User?) {
                if (p0 == null) {
                    callbackAsFail(LoginFailedException(RxSocialLogin.EXCEPTION_FAILED_RESULT))
                    return
                }

                val item = LoginResultItem().apply {
                    this.id = p0.userId
                    this.name = p0.userName
                    this.email = p0.userEmail
                    this.platform = PlatformType.AMAZON
                    this.result = true
                }

                callbackAsSuccess(item)
            }

            override fun onError(p0: AuthError?) {
                callbackAsFail(LoginFailedException(RxSocialLogin.EXCEPTION_UNKNOWN_ERROR))
            }
        })
    }
}