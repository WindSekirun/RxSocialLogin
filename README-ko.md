# RxSocialLogin [![CircleCI](https://circleci.com/gh/WindSekirun/RxSocialLogin.svg?style=svg)](https://circleci.com/gh/WindSekirun/RxSocialLogin) [![](https://jitpack.io/v/WindSekirun/RxSocialLogin.svg)](https://jitpack.io/#WindSekirun/RxSocialLogin)

 [![](https://img.shields.io/badge/Android%20Arsenal-RxSocialLogin-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/7028) [![Awesome Kotlin Badge](https://kotlin.link/awesome-kotlin.svg)](https://github.com/KotlinBy/awesome-kotlin)


![](graphics/logo.png)

*The license information for logo is located at the bottom of the document.*

These instructions are available in their respective languages.

* [English](README.md) - Latest update: 2018-10-08, [@WindSekirun](https://github.com/windsekirun)
* [한국어](README-ko.md) - Latest update: 2018-10-08, [@WindSekirun](https://github.com/windsekirun)
* [日本語](README-jp.md) - Latest update: 2018-10-08, [@WindSekirun](https://github.com/windsekirun)

## 소개

본 안드로이드 라이브러리는 15개의 플랫폼에 대해 소셜 로그인을 제공하는 라이브러리로, [RxJava2](https://github.com/ReactiveX/RxJava) 와 [Kotlin](http://kotlinlang.org/), 그리고 [Firebase 인증](https://firebase.google.com/docs/auth/) 의 도움으로 제공됩니다.

이 라이브러리는 제작자인 [@WindSekirun](https://github.com/windsekirun) 의 [SocialLogin](https://github.com/WindSekirun/SocialLogin) 라는 라이브러리의 개선 버전이며, 아래와 같은 차이점을 가지고 있습니다.

* 결과 전달 방식이 Listener 가 아닌 RxJava 로 통해 전달되는 것으로 변경되었습니다.
* 원본이 Java 로 작성된 것에 비해, 개선 버전은 Kotlin 으로만 작성되었습니다.
* 원본이 6개의 플랫폼을 지원했던 반면, 개선판은 15개의 플랫폼을 제공합니다.
* Provide *Type-Safe builder* with Kotlin DSL
* 모든 메서드와 코드를 재작성 하였습니다.
* Kotlin 으로 작성되었지만 Java 와 호환되도록 고려되었습니다.

## 지원되는 플랫폼

| 플랫폼                                                       | 데이터                                                       | 버전  |
| ------------------------------------------------------------ | ------------------------------------------------------------ | ----- |
| [Disqus](https://github.com/WindSekirun/RxSocialLogin/wiki/Guide-to-Disqus) | id, name, email, nickname, profilePicture                    | 1.0.0 |
| [Facebook](https://github.com/WindSekirun/RxSocialLogin/wiki/Guide-to-Facebook) | id, name, email, profilePicture, gender, firstName           | 0.5.0 |
| [Foursquare](https://github.com/WindSekirun/RxSocialLogin/wiki/Guide-to-Foursquare) | id, name, email, firstName, gender, birthDay, profilePicture | 1.0.0 |
| [Github](https://github.com/WindSekirun/RxSocialLogin/wiki/Guide-to-Github) | id, name, email, profilePicture, emailVerified               | 1.0.0 |
| [Google](https://github.com/WindSekirun/RxSocialLogin/wiki/Guide-to-Google) | id, name, email, profilePicture, emailVerified               | 0.5.0 |
| [Kakao](https://github.com/WindSekirun/RxSocialLogin/wiki/Guide-to-Kakao) | id, name, email, profilePicture, thumbnailImage, ageRange, birthDay, gender, emailVerified | 0.5.0 |
| [Line](https://github.com/WindSekirun/RxSocialLogin/wiki/Guide-to-Line) | id, name, accessToken                                        | 0.5.0 |
| [LinkedIn](https://github.com/WindSekirun/RxSocialLogin/wiki/Guide-to-LinkedIn) | id, name, email, profilePicture, firstName                   | 1.0.0 |
| [Naver](https://github.com/WindSekirun/RxSocialLogin/wiki/Guide-to-Naver) | id, name, email, nickname, gender, profilePicture, age, birthDay | 0.5.0 |
| [Twitch](https://github.com/WindSekirun/RxSocialLogin/wiki/Guide-to-Twitch) | id, name, email,  profilePicture                             | 1.0.0 |
| [Twitter](https://github.com/WindSekirun/RxSocialLogin/wiki/Guide-to-Twitter) | id, name, nickname, email, profilePicture                    | 0.5.0 |
| [VK](https://github.com/WindSekirun/RxSocialLogin/wiki/Guide-to-VK) | id, name, email, profilePicture, nickname, firstName, birthDay | 1.0.0 |
| [Windows](https://github.com/WindSekirun/RxSocialLogin/wiki/Guide-to-Windows) | id, name, email                                              | 1.0.0 |
| [Wordpress](https://github.com/WindSekirun/RxSocialLogin/wiki/Guide-to-Wordpress) | id, name, email, profilePicture, emailVerified               | 1.0.0 |
| [Yahoo](https://github.com/WindSekirun/RxSocialLogin/wiki/Guide-to-Yahoo) | id, name                                                     | 1.0.0 |

각 플랫폼의 이름을 누르면 해당 플랫폼의 적용 방법 가이드로 이동합니다.

## 불러오기

루트 폴더의 `build.gradle` 에 아래 주소를 추가합니다.

```groovy
allprojects {
	repositories {
		maven { url 'http://devrepo.kakao.com:8088/nexus/content/groups/public/' }
		maven { url 'https://jitpack.io' }
	}
}
```

사용할 모듈의 `build.gradle` 에 아래의 의존성을 추가합니다.

```groovy
dependencies {
	implementation 'com.github.WindSekirun:RxSocialLogin:1.0.0'
    
	// RxJava
	implementation 'io.reactivex.rxjava2:rxandroid:lastest-version'
	implementation 'io.reactivex.rxjava2:rxjava:lastest-version'
}
```

RxJava는 활동이 활발한 라이브러리로, 새로운 개선 사항을 적용하려면 항상 최신 버전을 유지해야 합니다. 따라서 RxJava 를 의존성의 맨 아래에 추가하는 것을 권장합니다.

* RxAndroid: <a href='http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22io.reactivex.rxjava2%22%20a%3A%22rxandroid%22'><img src='http://img.shields.io/maven-central/v/io.reactivex.rxjava2/rxandroid.svg'></a>

* RxJava: <a href='http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22io.reactivex.rxjava2%22%20a%3A%22rxjava%22'><img src='http://img.shields.io/maven-central/v/io.reactivex.rxjava2/rxjava.svg'></a>

#### Migrate from 1.0.0

1.1.0 has **MASSIVE** breaking changes you should know about that. 

The following are major changes.

- Migrate to Java Builder to DSL Builder
- Initialize in RxSocialLogin as once
- Call onActivityResult as once
- Migrate receive result with RxSocialLogin.result()

[Release Notes are here](https://github.com/WindSekirun/RxSocialLogin/pull/26)

## 아주 쉬운 5단계 사용법

First, Initialize the module using `ConfigDSLBuilder`. `ConfigDSLBuilder` allows you to configure settings for each platform. 

```kotlin
initSocialLogin {
    facebook(getString(R.string.facebook_api_key)) {
        behaviorOnCancel = true
        requireWritePermissions = false
        imageEnum = FacebookConfig.FacebookImageEnum.Large
    }
}
```

Inside `initSocialLogin` block, you can **use methods which have platform name** such as facebook and google. All parameters except `setup` will necessary information to use SocialLogin feature.

`setup` parameter is function that **provide generate platform config object**(ex, FacebookConfig) and apply additional options such as `behaviorOnCancel`, `imageEnum`. It can be optional, but not nullable parameters.

Although `ConfigDSLBuilder` is *Kotlin Type-Safe builders*, but **it has compatitable with Java language**. we provide `ConfigFunction` with same feature with original `setup` higher-order function.

You can see full examples of `ConfigDSLBuilder` both in [Kotlin](https://github.com/WindSekirun/RxSocialLogin/blob/1.1-dev/demo/src/main/java/com/github/windsekirun/rxsociallogin/test/MainApplication.kt) and [Java](https://github.com/WindSekirun/RxSocialLogin/blob/1.1-dev/demo/src/main/java/com/github/windsekirun/rxsociallogin/test/JavaApplication.java)

Next, Call `RxSocialLogin.initialize(this)` in `onStart` methods. 

```kotlin
override fun onStart() {
    super.onStart()
    RxSocialLogin.initialize(this)
}
```

From 1.0.0, `RxSocialLogin` class will manage instance of Login object, so you don't need to care about initialization. 

Next, Call `RxSocialLogin.activityResult(requestCode, resultCode, data)` in `onActivityResult` methods.

```kotlin
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent ? ) {
    super.onActivityResult(requestCode, resultCode, data)
    RxSocialLogin.activityResult(requestCode, resultCode, data)
}
```

Next, Call `RxSocialLogin.result`  where you want the results. Outside of Activity will be fine.

```kotlin
RxSocialLogin.result()
    .subscribe({ item -> 

    }, { throwable ->

    }).addTo(compositeDisposable)
```

Final, Call `RxSocialLogin.login(PlatformType.FACEBOOK)` to start SocialLogin feature.

### 사용시의 안내사항

#### 프로가드(Proguard) 적용

[샘플 앱의 프로가드 규칙](https://github.com/WindSekirun/RxSocialLogin/blob/master/demo/proguard-rules.pro) 을 참고하여 적용하기 바랍니다.

#### 제약 - 모든 행동은 메인 스레드를 유지해야 함

모든 행동은 메인 스레드 내에서 작동되야 합니다. 라이브러리 내부에서 네트워크를 사용할 경우에는 내부에서 [Fuel](https://github.com/kittinunf/Fuel) 를 사용하여 올바르게 처리되니, `RxSocialLogin` 으로 반환된 `Observable` 는 메인 스레드를 유지해야 합니다. 만일 메인 스레드가 아닐 경우 바로 로그인 실패가 이루어집니다.

즉, 아래의 경우에는 처리되지 않고 바로 `LoginFailedException` 으로 처리됩니다.

```kotlin
RxSocialLogin.result()
		.subscribeOn(Schedulers.io())
		.observeOn(AndroidSchedulers.mainThread())
		...
```

이 주의점으로 인해 **네트워크 처리 후 `flatMap` 등으로 바로 소셜 로그인을 시작하는 것이 허용되지 않습니다.** 이러한 케이스를 처리해야 될 경우에는 네트워크 처리 후 subscribe 내에서 따로 `RxSocialLogin` 를 호출하는 것이 바람직합니다.

#### OnErrorNotImplementedException가 발생

공통적인 오류로 [OnErrorNotImplementedException](http://reactivex.io/RxJava/javadoc/io/reactivex/exceptions/OnErrorNotImplementedException.html) 가 발생할 경우가 있는데, 이는 `subscribe` 당시 `onError` 에 대해 처리하지 않았기 때문입니다. 

#### UndeliverableException가 발생

0.5.0 기준으로 [UndeliverableException](http://reactivex.io/RxJava/javadoc/io/reactivex/exceptions/UndeliverableException.html) 가 발생할 경우가 있는데, 이는 Exception 이 `onError` 으로 전달되지 못하였을 때 발생하는 문제입니다. `RxJavaPlugins.setErrorHandler { e -> }` 라는 문구로 해결이 가능하나, 이는 RxJavaPlugins 의 전체 행동을 변경하므로 주의하시기 바랍니다. 

1.0.0 이상에서는 이 문제가 발생하지 않도록 `LoginFailedException` 가 `IllegalStateException` 를 상속하도록 변경되었습니다. 따라서 최신 버전에서는 발생하지 않도록 의도되었습니다.

이에 대한 자세한 사항은 [Error handling](https://github.com/ReactiveX/RxJava/wiki/What's-different-in-2.0#error-handling) 문서를 참조하시기 바랍니다.

#### Targeting below of API 21

Currently(1.1.0), **we support API 16 as minSdkVersion**, but `com.microsoft.identify.client:msal` library support API 21 as minSdkVersion.

According [issue #263 of AzureAD/microsoft-authentication-library-for-android](https://github.com/AzureAD/microsoft-authentication-library-for-android/issues/263), You can override this library to avoid conflicts of minSdkVersion.

Place this statement in AndroidManifest.xml to solve this conflicts. we hope microsoft solve this problem asap.

```xml
<uses-sdk tools:overrideLibrary="com.microsoft.identity.msal"/>
```

## 제작자 & 기여자

* 제작자: [@WindSekirun](https://github.com/windsekirun), 메일 [pyxis@uzuki.live](mailto:pyxis@uzuki.live)
* 기여자: [Contributors](https://github.com/WindSekirun/RxSocialLogin/graphs/contributors)

버그 발견 사항, 개선 사항, 새로운 플랫폼 추가 등 다양한 사항을 [이슈 트래커](https://github.com/WindSekirun/RxSocialLogin/issues) 에서 접수받고 있습니다. [Pull Requests](https://github.com/WindSekirun/RxSocialLogin/pulls) 도 언제나 환영입니다.

## 라이센스

* ReactiveX 로고는 [Seeklogo](https://seeklogo.com/vector-logo/284342/reactivex) 에서 가져왔습니다. 
* 로고에 사용된 폰트는 Hanken Design Co. 의 [Hanken round](https://www.behance.net/gallery/18871499/Hanken-Round-Free-Typeface) 이며 본 폰트는 SIL OFL를 따릅니다. 프로젝트 내에 로고에 대한 PSD 파일이 있습니다.
* 샘플에 사용된 플랫폼 로고에 대한 저작권은 각 회사에 존재합니다. RxSocialLogin 라이브러리는 플랫폼 회사와 연관이 없습니다.

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

