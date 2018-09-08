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
    var platform: PlatformType = PlatformType.NONE
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
        fun createFail(platform: PlatformType) = LoginResultItem().apply {
            this.result = false
            this.platform = platform
        }
    }

    override fun toString(): String {
        return "LoginResultItem(\n" +
                "platform=$platform,\n" +
                "result=$result,\n" +
                "id='$id',\n" +
                "name='$name',\n" +
                "accessToken='$accessToken',\n" +
                "email='$email',\n" +
                "nickname='$nickname',\n" +
                "profilePicture='$profilePicture',\n" +
                "gender='$gender',\n" +
                "thumbnailPicture='$thumbnailPicture',\n" +
                "emailVerified=$emailVerified,\n" +
                "age='$age'\n" +
                "birthday='$birthday',\n" +
                "firstName='$firstName',\n" +
                "ageRange='$ageRange'\n" +
                ")"
    }


}
