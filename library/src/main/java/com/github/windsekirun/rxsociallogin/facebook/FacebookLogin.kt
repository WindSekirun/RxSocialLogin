package com.github.windsekirun.rxsociallogin.facebook

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.github.windsekirun.rxsociallogin.RxSocialLogin
import com.github.windsekirun.rxsociallogin.RxSocialLogin.EXCEPTION_USER_CANCELLED
import com.github.windsekirun.rxsociallogin.base.BaseSocialLogin
import com.github.windsekirun.rxsociallogin.intenal.exception.LoginFailedException
import com.github.windsekirun.rxsociallogin.intenal.model.LoginResultItem
import com.github.windsekirun.rxsociallogin.intenal.model.PlatformType
import pyxis.uzuki.live.richutilskt.utils.getJSONObject
import pyxis.uzuki.live.richutilskt.utils.getJSONString

class FacebookLogin constructor(activity: FragmentActivity) : BaseSocialLogin<FacebookConfig>(activity) {
    override fun getPlatformType(): PlatformType = PlatformType.FACEBOOK

    private val callbackManager: CallbackManager = CallbackManager.Factory.create()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (FacebookSdk.isFacebookRequestCode(requestCode)) {
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun login() {
        if (config.requireEmail) config.requestOptions.add("email")
        if (config.requireFriends) config.requestOptions.add("user_friends")

        if (config.requireWritePermissions) {
            LoginManager.getInstance().logInWithPublishPermissions(activity, config.requestOptions)
        } else {
            LoginManager.getInstance().logInWithReadPermissions(activity, config.requestOptions)
        }

        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                getUserInfo()
            }

            override fun onCancel() {
                if (config.behaviorOnCancel) {
                    getUserInfo()
                } else {
                    callbackAsFail(LoginFailedException("$EXCEPTION_USER_CANCELLED If you prevent this error," +
                            "set 'behaviorOnCancel` in FacebookConfig object."))
                }
            }

            override fun onError(error: FacebookException) {
                callbackAsFail(LoginFailedException(RxSocialLogin.EXCEPTION_FAILED_RESULT, error))
            }
        })
    }

    override fun logout(clearToken: Boolean) {
        LoginManager.getInstance().logOut()
    }

    private fun getUserInfo() {
        val accessToken = AccessToken.getCurrentAccessToken()

        val callback: GraphRequest.GraphJSONObjectCallback = GraphRequest.GraphJSONObjectCallback { obj, _ ->
            if (obj == null) {
                callbackAsFail(LoginFailedException(RxSocialLogin.EXCEPTION_FAILED_RESULT))
                return@GraphJSONObjectCallback
            }

            val data = getJSONObject(getJSONObject(obj, "picture"), "data")
            if (data == null) {
                callbackAsFail(LoginFailedException(RxSocialLogin.EXCEPTION_FAILED_RESULT))
                return@GraphJSONObjectCallback
            }

            val profilePicture = data.getJSONString("url")

            val item = LoginResultItem().apply {
                this.id = obj.getJSONString("id")
                this.name = obj.getJSONString("name")
                this.email = obj.getJSONString("email")
                this.gender = obj.getJSONString("gender")
                this.firstName = obj.getJSONString("first_name")
                this.profilePicture = profilePicture
                this.accessToken = accessToken.toJSONObject().toString()
                this.platform = PlatformType.FACEBOOK
                this.result = true
            }

            callbackAsSuccess(item)
        }

        var originField = "id, name, email, gender, birthday, first_name, "
        originField += config.imageEnum.fieldName

        val request = GraphRequest.newMeRequest(accessToken, callback)
        val parameters = Bundle()
        parameters.putString("fields", originField)
        request.parameters = parameters
        request.executeAsync()
    }
}