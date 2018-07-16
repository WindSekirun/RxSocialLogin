## RxSocialLogin [![CircleCI](https://circleci.com/gh/WindSekirun/RxSocialLogin.svg?style=svg)](https://circleci.com/gh/WindSekirun/RxSocialLogin) [![](https://jitpack.io/v/WindSekirun/RxSocialLogin.svg)](https://jitpack.io/#WindSekirun/RxSocialLogin)
[![](https://img.shields.io/badge/Android%20Arsenal-RxSocialLogin-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/7028)

Integrated SocialLogin such as [Facebook, Kakao, Naver, Line, Twitter, Google] with RxJava and [Firebase Authentication](https://firebase.google.com/docs/auth/), written in Kotlin. This library is enhanced version of [SocialLogin](https://github.com/WindSekirun/SocialLogin) which maintained by [WindSekirun](https://github.com/WindSekirun) and fully rewritten in Kotlin and integrate with RxJava and Firebase Authentication.

이 라이브러리에 대한 소개글은 PyxisPub 블로그에서 보실 수 있습니다. (한글만 제공됩니다.) https://blog.uzuki.live/introduction-to-rxsociallogin-provides-sociallogin/

### Difference from Original library, [SocialLogin](https://github.com/WindSekirun/SocialLogin)
- RxJava2 integrated.
- CircleCI integrated.
- Firebase Authentication integrated when use GoogleLogin.
- Rewrite all methods in Kotlin
- Hold Context in WeakReference to solve memory leak

## Available Feature
|Service|logout|Return Data|Config|
|---|---|---|---|
|Facebook|O|id, name, email, profilePicture, gender, firstName|setRequireEmail, setRequireWritePermission, setApplicationId, setRequireFriends, setBehaviorOnCancel, setPictureSize|
|Kakao|O|id, name, email, profilePicture, thumbnailImage, ageRange, birthDay, gender, emailVerified|setRequireEmail, setRequireAgeRange, setRequireBirthday, setRequireGender|
|Naver|O|id, name, email, nickname, gender, profilePicture, age, birthDay|setAuthClientId, setAuthClientSecret, setClientName|
|Line|X|id, name, accessToken|setChannelId|
|Twitter|X|id, name|setConsumerKey, setConsumerSecret|
|Google|O|id, name, email, profilePicture, emailVerified|setRequireEmail, setClientTokenId|

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
    implementation 'com.github.WindSekirun:RxSocialLogin:0.5.0'
}
```

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
3. Sometimes it happen 'UndeliverableException'. if you prevent this exception, use ```RxJavaPlugins.setErrorHandler { e -> }``` statement. this methods occur change global error handler of RxJava, please use it only when you know exactly what you are doing. You can see [RxJava2 Wiki](https://github.com/ReactiveX/RxJava/wiki/What's-different-in-2.0#error-handling)

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
1. Configure API Project in [here](https://developers.google.com/identity/sign-in/android/start-integrating)
2. In AS 3.1, enter 'Firebase' and click 'Authentication' and click 'Connect to Firebase' and 'Add Firebase Authentication to your app'
 -  in this step, you will save your 'google-services.json' in your app module directory.
3. Enable Google as authentication provider in Firebase Console
4. Find 'Web Client ID' in Firebase console. you can find this information in sub-section of authentication provider.
5. Provide your 'Web Client ID' into GoogleConfig.setClientTokenId()

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
                .setClientTokenId("<YOUR-API-KEY>")
                .build();

SocialLogin.addType(SocialType.GOOGLE, googleConfig);
```

## Sample
- [MainActivity.kt](https://github.com/WindSekirun/RxSocialLogin/blob/master/demo/src/main/java/com/github/windsekirun/rxsociallogin/test/MainActivity.kt)
- [MainApplication.kt](https://github.com/WindSekirun/RxSocialLogin/blob/master/demo/src/main/java/com/github/windsekirun/rxsociallogin/test/MainApplication.kt)

## License
```
Copyright 2017 WindSekirun (DongGil, Seo)

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
