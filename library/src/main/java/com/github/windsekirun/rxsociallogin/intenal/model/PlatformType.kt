package com.github.windsekirun.rxsociallogin.intenal.model

import java.io.Serializable

enum class PlatformType(val supportProperty: String = "", val since: String = "1.0.0") : Serializable {
    AMAZON(supportProperty = "id, name, email", since = "1.3.0"),
    BITBUCKET(supportProperty = "id, name, nickname, accessToken", since = "1.3.0"),
    DISCORD(supportProperty = "id, name, email, nickname, profilePicture, accessToken", since = "1.3.0"),
    DISQUS(supportProperty = "id, name, email, nickname, profilePicture, accessToken", since = "1.3.0"),
    FACEBOOK(supportProperty = "id, name, email, gender, fistName, profilePicture, accessToken", since = "0.5.0"),
    FOURSQUARE(supportProperty = "id, name, email, firstName, gender, birthday, profilePicture, accessToken", since = "1.0.0"),
    GITHUB(supportProperty = "id, name, email, profilePicture, emailVerified", since = "1.0.0"),
    GITLAB(supportProperty = "id, name, email, nickname, profilePicture, accessToken", since = "1.3.0"),
    GOOGLE(supportProperty = "id, name, email, profilePicture, emailVerified", since = "0.5.0"),
    INSTAGRAM(supportProperty = "id, name, profilePicture, accessToken", since = "1.3.0"),
    KAKAO(supportProperty = "id, name, email, profilePicture, thumbnailPicture, gender, ageRange, birthday, emailVerified", since = "0.5.0"),
    LINE(supportProperty = "id, name, profilePicture, accessToken", since = "0.5.0"),
    LINKEDIN(supportProperty = "id, name, email, profilePicture, accessToken, fiestName", since = "1.0.0"),
    NAVER(supportProperty = "id, name, email, nickname, gender, age, birthday, profilePicture, accessToken", since = "0.5.0"),
    TWITCH(supportProperty = "id, name, email, profilePicture, accessToken", since = "0.5.0"),
    TWITTER(supportProperty = "id, name, email, nickname, profilePicture", since = "1.0.0"),
    VK(supportProperty = "id, name, email, birthday, profilePicture, firstName, nickname", since = "1.0.0"),
    WINDOWS(supportProperty = "id, name, email, accessToken", since = "1.0.0"),
    WORDPRESS(supportProperty = "id, name, email, profilePicture, accessToken, emailVerified", since = "1.0.0"),
    YAHOO(supportProperty = "id, name", since = "1.0.0");
}
