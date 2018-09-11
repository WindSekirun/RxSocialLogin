## RxSocialLogin [![CircleCI](https://circleci.com/gh/WindSekirun/RxSocialLogin.svg?style=svg)](https://circleci.com/gh/WindSekirun/RxSocialLogin) [![](https://jitpack.io/v/WindSekirun/RxSocialLogin.svg)](https://jitpack.io/#WindSekirun/RxSocialLogin)
[![](https://img.shields.io/badge/Android%20Arsenal-RxSocialLogin-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/7028)

Integrated SocialLogin with 11 Platform with RxJava and [Firebase Authentication](https://firebase.google.com/docs/auth/), written in Kotlin. This library is enhanced version of [SocialLogin](https://github.com/WindSekirun/SocialLogin) which maintained by [WindSekirun](https://github.com/WindSekirun) and fully rewritten in Kotlin and integrate with RxJava and Firebase Authentication.

이 라이브러리에 대한 소개글은 PyxisPub 블로그에서 보실 수 있습니다. (한글만 제공됩니다.) https://blog.uzuki.live/introduction-to-rxsociallogin-provides-sociallogin/

### Difference from Original library, [SocialLogin](https://github.com/WindSekirun/SocialLogin)
- RxJava2 integrated.
- CircleCI integrated.
- Firebase Authentication integrated when use Google, Github.
- **Re-write all methods** in Kotlin
- Hold Context in WeakReference to solve memory leak
- Available to login with Github using Firebase and OAuth2 Authentication
- Available to login with LinkedIn, Wordpress, Yahoo, VK using OAuth2 Authentication

## Available Platform
|Platform|Data|
|---|---|
|Facebook|id, name, email, profilePicture, gender, firstName|
|Github|id, name, email, profilePicture, emailVerified|
|Google|id, name, email, profilePicture, emailVerified|
|Kakao|id, name, email, profilePicture, thumbnailImage, ageRange, birthDay, gender, emailVerified|
|LinkedIn|id, name, email, profilePicture, firstName|
|Line|id, name, accessToken|
|Naver|id, name, email, nickname, gender, profilePicture, age, birthDay|
|Twitter|id, name|
|VK||
|Wordpress|id, name, email, profilePicture, emailVerified|
|Yahoo|id, name|

## Usages

*rootProject/build.gradle*
```
allprojects {
    repositories {
    	maven { url 'http://devrepo.kakao.com:8088/nexus/content/groups/public/' }
	    maven { url 'https://jitpack.io' }
    }
}
```

*app/build.gradle*
```
dependencies {
    implementation 'com.github.WindSekirun:RxSocialLogin:1.0.0'
    
    // RxJava
    implementation 'io.reactivex.rxjava2:rxandroid:lastest-version'
    implementation 'io.reactivex.rxjava2:rxjava:lastest-version'
}
```
* RxAndroid: <a href='http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22io.reactivex.rxjava2%22%20a%3A%22rxandroid%22'><img src='http://img.shields.io/maven-central/v/io.reactivex.rxjava2/rxandroid.svg'></a>
* RxJava: <a href='http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22io.reactivex.rxjava2%22%20a%3A%22rxjava%22'><img src='http://img.shields.io/maven-central/v/io.reactivex.rxjava2/rxjava.svg'></a>

## Guide

### Declare xxxLogin variable to use
```java
private KakaoLogin mKakaoLogin;

mKakaoLogin = new KakaoLogin(this);
```

### Call onActivityResult() on onActivityResult
```java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (mKakaoLogin != null) {
        mKakaoLogin.onActivityResult(requestCode, resultCode, data);
    }
}
```

### Subscribe observable by RxSocialLogin
```java
RxSocialLogin.kakao(mKakaoLogin)
        .subscribe(data -> {
            // TODO: do job with LoginResultItem
        }, error -> {
            // TODO: Error on login()
        });
```

#### Limitations
1. Subscribe and observe should occur on Main Thread. if you try to change thread such as ```Schedulers.io``` using ```subscribeOn```, it will be call ```onError()```. instead, ```AndroidSchedulers.mainThread()``` will be fine.
2. You should use ```subscribe(Consumer onNext, Consumer onError)```, not ```subscribe(Consumer onNext)``` whether you don't need onError callback.

### Call login() methods on anywhere
```java
kakaoLogin.onLogin()
```

## Dependencies of service
Requirements are different by service.

### Kakao
It support v2 of Kakao User API. See [Document](https://developers.kakao.com/docs/android/user-management)

#### build.gradle
```
repositories {
    maven { url 'http://devrepo.kakao.com:8088/nexus/content/groups/public/' }
}
```

```
implementation 'com.kakao.sdk:usermgmt:1.11.1'
```

#### AndroidManifest.xml
```XML
 <meta-data
            android:name="com.kakao.sdk.AppKey"
            android:value="YOUR-API-KEY"/>
```

#### MainApplication
```Java
SocialLogin.init(this);
KakaoConfig kakaoConfig = new KakaoConfig.Builder()
                .setRequireEmail()
                .build();

SocialLogin.addType(SocialType.KAKAO, kakaoConfig);
```

### Facebook

#### build.gradle
```
implementation 'com.facebook.android:facebook-android-sdk:4.23.0'
```

#### AndroidManifest.xml
```Java
<activity
            android:name="com.facebook.FacebookActivity"
            android:configChanges="keyboard|keyboardHidden|screenLayout|screenSize|orientation"
            android:label="@string/app_name" />

<meta-data android:name="com.facebook.sdk.ApplicationId"
            android:value="YOUR-API-KEY"/>
```

#### MainApplication
```Java
SocialLogin.init(this);
FacebookConfig facebookConfig = new FacebookConfig.Builder()
                .setApplicationId("YOUR-API-KEY")
                .setRequireEmail()
                .build();


SocialLogin.addType(SocialType.FACEBOOK, facebookConfig);
```

### Naver

#### build.gradle
```
implementation 'com.naver.nid:naveridlogin-android-sdk:4.2.0'
```

#### MainApplication
```Java
SocialLogin.init(this);
NaverConfig naverConfig = new NaverConfig.Builder()
                .setAuthClientId("YOUR-API-KEY")
                .setAuthClientSecret("YOUR-API-KEY")
                .setClientName(getString(R.string.app_name))
                .build();


SocialLogin.addType(SocialType.NAVER, naverConfig);
```

### Line

#### MainApplication
```Java
SocialLogin.init(this);
LineConfig lineConfig = new LineConfig.Builder()
                .setChannelId("<YOUR-API-KEY>")
                .build();


SocialLogin.addType(SocialType.LINE, lineConfig);
```

### Twitter

#### build.gradle
```
implementation 'com.twitter.sdk.android:twitter:3.3.0'
```

#### MainApplication
```Java
SocialLogin.init(this);
TwitterConfig twitterConfig = new TwitterConfig.Builder()
                .setConsumerKey("<YOUR-API-KEY>")
                .setConsumerSecret("<YOUR-API-KEY>")
                .build();


SocialLogin.addType(SocialType.TWITTER, twitterConfig);
```

### Google

#### Precondition
Tutorial to implement Google Login with RxSocialLogin, [English](https://blog.uzuki.live/implement-google-login-with-rxsociallogin-english/), [Korean](https://blog.uzuki.live/implement-google-login-with-rxsociallogin-korean/)

1. In AS 3.1, enter 'Firebase' and click 'Authentication' and click 'Connect to Firebase' and 'Add Firebase Authentication to your app'. in this step, you will save your 'google-services.json' in your app module directory.
2. Enable Google as authentication provider in Firebase Console
3. Find 'Web Client ID' in Firebase console. you can find this information in sub-section of authentication provider.
4. Provide your 'Web Client ID' into GoogleConfig.setClientTokenId()

#### build.gradle
```
implementation 'com.google.android.gms:play-services-auth:15.0.0'
implementation 'com.google.firebase:firebase-auth:15.0.0'
```

#### MainApplication
```Java
SocialLogin.init(this);
GoogleConfig googleConfig = new GoogleConfig.Builder()
                .setRequireEmail()
                .setClientTokenId("<YOUR-WEB-CLIENT-ID>")
                .build();

SocialLogin.addType(SocialType.GOOGLE, googleConfig);
```

### Github (Since 1.0)

#### Precondition
First and second steps are same as Google's Precondition.

1. In AS 3.1, enter 'Firebase' and click 'Authentication' and click 'Connect to Firebase' and 'Add Firebase Authentication to your app'. in this step, you will save your 'google-services.json' in your app module directory.
2. Enable Github as authentication provider in Firebase Console
3. Copy authorization callback url in Github section.
4. Enter into [new OAuth application](https://github.com/settings/applications/new) on Github, and paste your callback url which find in step 3.
5. Press 'register application', and copy Client ID and Client Secret.
6. Provide your Client ID and Client Secret to Github section of Firebase Console
7. Provide your Client ID and Client Secret into GithubConfig.setClientId() and GithubConfig.setClientSecret()

#### build.gradle
```
implementation 'com.google.android.gms:play-services-auth:15.0.0'
implementation 'com.google.firebase:firebase-auth:15.0.0'
```

#### MainApplication
```Java
SocialLogin.init(this);
GithubConfig githubConfig = new GithubConfig.Builder()
                .setClientId("<YOUR CLIENT ID>")
                .setClientSecret("<YOUR CLIENT SECRET>")
                .setClearCookies(true)
                .build();

SocialLogin.addType(SocialType.GITHUB, githubConfig);
```

### LinkedIn (Since 1.0)

#### Precondition
1. Register new application in [LinkedIn apps](https://www.linkedin.com/developer/apps)
2. Provide your Client ID and Client Secret into LinkedinConfig.setClientId() and LinkedinConfig.setClientSecret()
3. Make sure redirectUri has same value between LinkedIn apps and LinkedinConfig.

#### MainApplication
```Java
LinkedinConfig linkedinConfig = new LinkedinConfig.Builder()
                .setClientId("<YOUR CLIENT ID>")
                .setClientSecret("<YOUR CLIENT SECRET>")
                .setRequireEmail()
                .setClearCookies(true)
                .setRedirectUri("http://example.com/oauth/callback")
                .build();

SocialLogin.addType(SocialType.LINKEDIN, linkedinConfig);
```

### Wordpress (Since 1.0)

#### Precondition
1. Register new application in [Wordpress apps](https://developer.wordpress.com/apps/)
2. Provide your Client ID and Client Secret into WordpressConfig.setClientId() and WordpressConfig.setClientSecret()
3. Make sure redirectUri has same value between Wordpress apps and WordpressConfig.

#### MainApplication
```Java
WordpressConfig wordpressConfig = new WordpressConfig.Builder()
                .setClientId("<YOUR CLIENT ID>")
                .setClientSecret("<YOUR CLIENT SECRET>")
                .setClearCookies(true)
                .setRedirectUri("http://example.com/oauth/callback")
                .build();

SocialLogin.addType(SocialType.WORDPRESS, wordpressConfig);
```

### Yahoo (Since 1.0)

#### MainApplication
```Java
YahooConfig yahooConfig = new YahooConfig.Builder()
                .setClientId("<YOUR CLIENT ID>")
                .setClientSecret("<YOUR CLIENT SECRET>")
                .setClearCookies(true)
                .setRedirectUri("http://example.com/oauth/callback")
                .build();

SocialLogin.addType(SocialType.YAHOO, yahooConfig);
```

## Sample
- [MainActivity.kt](https://github.com/WindSekirun/RxSocialLogin/blob/master/demo/src/main/java/com/github/windsekirun/rxsociallogin/test/MainActivity.kt)
- [MainApplication.kt](https://github.com/WindSekirun/RxSocialLogin/blob/master/demo/src/main/java/com/github/windsekirun/rxsociallogin/test/MainApplication.kt)

## Proguard

For usages with Proguard, please apply [these rules](https://github.com/WindSekirun/RxSocialLogin/blob/master/demo/proguard-rules.pro) according [issue #2](https://github.com/WindSekirun/RxSocialLogin/issues/2). 

## License
```
Copyright 2017 - 2018 WindSekirun (DongGil, Seo)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
