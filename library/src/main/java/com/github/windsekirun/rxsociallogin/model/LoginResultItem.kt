package com.github.windsekirun.rxsociallogin.model

/**
 * RxSocialLogin
 * Class: LoginResultItem
 * Created by pyxis on 18. 7. 11.
 *
 *
 * Description:
 */
class LoginResultItem {
    var type: SocialType = SocialType.NONE
    var result = false
    var id = ""
    var name = ""
    var accessToken = ""
    var email = ""
    var nickname = ""
    var profilePicture = ""
    var gender = ""
    var thumbnailPicture = ""
    var emailVerified = false
    var age = ""
    var birthday = ""
    var firstName = ""
    var ageRange = ""


    companion object {
        fun createFail(type: SocialType) = LoginResultItem().apply {
            this.result = false
            this.type = type
        }
    }

    override fun toString(): String {
        return "LoginResultItem(type=$type, result=$result, id='$id', name='$name', " +
                "accessToken='$accessToken', email='$email', nickname='$nickname'," +
                " profilePicture='$profilePicture', gender='$gender'," +
                " thumbnailPicture='$thumbnailPicture', emailVerified='$emailVerified', age='$age'," +
                " birthday='$birthday', firstName='$firstName', ageRange='$ageRange')"
    }
}
