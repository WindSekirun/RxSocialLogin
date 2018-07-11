## RxSocialLogin [![CircleCI](https://circleci.com/gh/WindSekirun/RxSocialLogin.svg?style=svg)](https://circleci.com/gh/WindSekirun/RxSocialLogin) [![](https://jitpack.io/v/WindSekirun/RxSocialLogin.svg)](https://jitpack.io/#WindSekirun/RxSocialLogin)

Integrated login feature with Social such Facebook, Kakao, Naver, Line, Twitter, Google

This is enhance version of [SocialLogin](https://github.com/WindSekirun/SocialLogin) which maintained by me, but i need Rx- version to use my application. So i divide repository and put same feature into this repository.

##### Diff from Original library, [SocialLogin](https://github.com/WindSekirun/SocialLogin)
- RxJava2 Integrated
- Written in Kotlin
- change callback object - LoginResultItem
- no need to call onDestroy()
- hold ```Activity``` in WeakReference to solve memory leak
- login with FirebaseAuth in GoogleLogin

## Available Feature
|Service|logout|Return Data|Config|
|---|---|---|---|
|Facebook|O|id, name, email, profilePicture, gender, firstName|setRequireEmail, setRequireWritePermission, setApplicationId, setRequireFriends, setBehaviorOnCancel, setPictureSize|
|Kakao|O|id, name, email, profilePicture, thumbnailImage, ageRange, birthDay, gender, emailVerified|setRequireEmail, setRequireAgeRange, setRequireBirthday, setRequireGender|
|Naver|O|id, name, email, nickname, gender, profilePicture, age, birthDay|setAuthClientId, setAuthClientSecret, setClientName|
|Line|X|id, name, accessToken|setChannelId|
|Twitter|X|id, name|setConsumerKey, setConsumerSecret|
|Google|O|id, name, email, profilePicture, |setRequireEmail|

## Usages
**Warning, this library has pre-released.**

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
    implementation 'com.github.WindSekirun:RxSocialLogin:0.1.0'
}
```

## Guide
It can be copy-paste because each service has same constructure.

### Declare xxxLogin variabnle to use
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
1. Subscribe and observe should occur on Main Thread. if you try to change thread such as ```Schedulers.io```, it will be call ```onError()```  ```AndroidSchedulers.mainThread()``` will be fine.
2. You should use ```subscribe(Consumer onNext, Consumer onError)```, not ```subscribe(Consumer onNext)``` whether you don't need onError callback.

### Call login() methods on anywhere
```java
kakaoLogin.onLogin()
```

## Dependencies of service
Requirements are different by service.

### Kakao

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

### LineLogin

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
1. Connect with Firebase Auth, and put google-services.json to app folder
2. Go to [Firebase Authorization Provider Setting](https://console.firebase.google.com/u/0/project/rxsociallogin/authentication/providers) and enable Google
3. Find 'Web Client ID' in Web Section in Google Section in Firebase Authorization Provider Setting.
4. declare client-id which find in 2 in MainApplication by setClientTokenId

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
