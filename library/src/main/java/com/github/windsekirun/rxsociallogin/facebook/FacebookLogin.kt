package com.github.windsekirun.rxsociallogin.facebook

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.github.windsekirun.rxsociallogin.RxSocialLogin
import com.github.windsekirun.rxsociallogin.model.LoginResultItem
import com.github.windsekirun.rxsociallogin.model.PlatformType
import pyxis.uzuki.live.richutilskt.utils.getJSONObject
import pyxis.uzuki.live.richutilskt.utils.getJSONString

class FacebookLogin(activity: Activity) : RxSocialLogin(activity) {
    private val callbackManager: CallbackManager = CallbackManager.Factory.create()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (FacebookSdk.isFacebookRequestCode(requestCode)) {
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun login() {
        val config = getPlatformConfig(PlatformType.FACEBOOK) as FacebookConfig

        if (config.isRequireWritePermissions) {
            LoginManager.getInstance().logInWithPublishPermissions(activity!!, config.requestOptions)
        } else {
            LoginManager.getInstance().logInWithReadPermissions(activity!!, config.requestOptions)
        }

        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                getUserInfo()
            }

            override fun onCancel() {
                if (config.isBehaviorOnCancel) {
                    getUserInfo()
                } else {
                    callbackFail(PlatformType.FACEBOOK)
                }
            }

            override fun onError(error: FacebookException) {
                callbackFail(PlatformType.FACEBOOK)
            }
        })
    }

    override fun logout(clearToken: Boolean) {
        LoginManager.getInstance().logOut()
    }

    fun toObservable() = RxSocialLogin.facebook(this)

    private fun getUserInfo() {
        val config = getPlatformConfig(PlatformType.FACEBOOK) as FacebookConfig

        val callback: GraphRequest.GraphJSONObjectCallback = GraphRequest.GraphJSONObjectCallback { obj, _ ->
            if (obj == null) {
                callbackFail(PlatformType.FACEBOOK)
                return@GraphJSONObjectCallback
            }

            val data = getJSONObject(getJSONObject(obj, "picture"), "data")
            val profilePicture = data!!.getJSONString("url")

            val item = LoginResultItem().apply {
                this.id = obj.getJSONString("id")
                this.name = obj.getJSONString("name")
                this.email = obj.getJSONString("email")
                this.gender = obj.getJSONString("gender")
                this.firstName = obj.getJSONString("first_name")
                this.profilePicture = profilePicture
                this.platform = PlatformType.FACEBOOK
                this.result = true
            }

            callbackItem(item)
        }

        var originField = "id, name, email, gender, birthday, first_name, "
        originField += config.imageEnum.fieldName

        val request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), callback)
        val parameters = Bundle()
        parameters.putString("fields", originField)
        request.parameters = parameters
        request.executeAsync()
    }
}