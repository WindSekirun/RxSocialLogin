# RxSocialLogin [![](https://jitpack.io/v/WindSekirun/RxSocialLogin.svg)](https://jitpack.io/#WindSekirun/RxSocialLogin)

 [![](https://img.shields.io/badge/Android%20Arsenal-RxSocialLogin-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/7028) [![Awesome Kotlin Badge](https://kotlin.link/awesome-kotlin.svg)](https://github.com/KotlinBy/awesome-kotlin)

![](graphics/logo.png)

*The license information for logo is located at the bottom of the document.*

These instructions are available in their respective languages.

* [English](README.md) - Latest update: 2018-11-18, [@WindSekirun](https://github.com/windsekirun)
* [한국어](README-ko.md) - Latest update: 2018-10-25, [@WindSekirun](https://github.com/windsekirun)
* [日本語](README-jp.md) - Latest update: 2018-10-25, [@WindSekirun](https://github.com/windsekirun)

## Introduction

An Android Library that provides social login for 15 platforms within by [RxJava2](https://github.com/ReactiveX/RxJava), [Kotlin](http://kotlinlang.org/) and [Firebase Authentication](https://firebase.google.com/docs/auth/).

This library is an improved version of [@WindSekirun](https://github.com/windsekirun) 's [SocialLogin](https://github.com/WindSekirun/SocialLogin) library. It has the following differences.

* The result delivery method has been changed to be passed through RxJava instead of the Listener.
* Compared to the original written in Java, the improved version is written in Kotlin only.
* Compared to the original supported 6 platforms, the improved version is support 15 platforms.
* Provide *Type-Safe builder* with Kotlin DSL
* All methods and code have been rewritten.
* All code that are written in Kotlin but considered to be Java compatible.

## Supported Platforms

| Platform                                                       | Data                                                       | Version  |
| ------------------------------------------------------------ | ------------------------------------------------------------ | ----- |
| [Disqus](https://github.com/WindSekirun/RxSocialLogin/wiki/Guide-to-Disqus) | id, name, email, nickname, profilePicture, accessToken                    | 1.0.0 |
| [Facebook](https://github.com/WindSekirun/RxSocialLogin/wiki/Guide-to-Facebook) | id, name, email, profilePicture, gender, firstName, accessToken           | 0.5.0 |
| [Foursquare](https://github.com/WindSekirun/RxSocialLogin/wiki/Guide-to-Foursquare) | id, name, email, firstName, gender, birthDay, profilePicture, accessToken | 1.0.0 |
| [Github](https://github.com/WindSekirun/RxSocialLogin/wiki/Guide-to-Github) | id, name, email, profilePicture, emailVerified, accessToken               | 1.0.0 |
| [Google](https://github.com/WindSekirun/RxSocialLogin/wiki/Guide-to-Google) | id, name, email, profilePicture, emailVerified               | 0.5.0 |
| [Kakao](https://github.com/WindSekirun/RxSocialLogin/wiki/Guide-to-Kakao) | id, name, email, profilePicture, thumbnailImage, ageRange, birthDay, gender, emailVerified | 0.5.0 |
| [Line](https://github.com/WindSekirun/RxSocialLogin/wiki/Guide-to-Line) | id, name, accessToken                                        | 0.5.0 |
| [LinkedIn](https://github.com/WindSekirun/RxSocialLogin/wiki/Guide-to-LinkedIn) | id, name, email, profilePicture, firstName, accessToken                   | 1.0.0 |
| [Naver](https://github.com/WindSekirun/RxSocialLogin/wiki/Guide-to-Naver) | id, name, email, nickname, gender, profilePicture, age, birthDay, accessToken | 0.5.0 |
| [Twitch](https://github.com/WindSekirun/RxSocialLogin/wiki/Guide-to-Twitch) | id, name, email,  profilePicture, accessToken                             | 1.0.0 |
| [Twitter](https://github.com/WindSekirun/RxSocialLogin/wiki/Guide-to-Twitter) | id, name, nickname, email, profilePicture                    | 0.5.0 |
| [VK](https://github.com/WindSekirun/RxSocialLogin/wiki/Guide-to-VK) | id, name, email, profilePicture, nickname, firstName, birthDay | 1.0.0 |
| [Windows](https://github.com/WindSekirun/RxSocialLogin/wiki/Guide-to-Windows) | id, name, email                                              | 1.0.0 |
| [Wordpress](https://github.com/WindSekirun/RxSocialLogin/wiki/Guide-to-Wordpress) | id, name, email, profilePicture, emailVerified, accessToken               | 1.0.0 |
| [Yahoo](https://github.com/WindSekirun/RxSocialLogin/wiki/Guide-to-Yahoo) | id, name                                                     | 1.0.0 |

Click on the name of each platform to move to how to apply the platform.

## Import

Add the following code to `build.gradle` in the root folder.

```groovy
allprojects {
	repositories {
		maven { url 'http://devrepo.kakao.com:8088/nexus/content/groups/public/' }
		maven { url 'https://jitpack.io' }
	}
}
```

Add the following dependencies to the `build.gradle` of the module you want to use.

```groovy
dependencies {
	implementation 'com.github.WindSekirun:RxSocialLogin:1.2.1'
	// androidx
        implementation 'com.github.WindSekirun:RxSocialLogin:1.2.1-androidx'
    
	// RxJava
	implementation 'io.reactivex.rxjava2:rxandroid:lastest-version'
	implementation 'io.reactivex.rxjava2:rxjava:lastest-version'
}
```

RxJava is an active library, and you should always keep the latest version for new enhancements to take effect. Therefore, we recommend that you add RxJava to the bottom of the dependency.

* RxAndroid: <a href='http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22io.reactivex.rxjava2%22%20a%3A%22rxandroid%22'><img src='http://img.shields.io/maven-central/v/io.reactivex.rxjava2/rxandroid.svg'></a>
* RxJava: <a href='http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22io.reactivex.rxjava2%22%20a%3A%22rxjava%22'><img src='http://img.shields.io/maven-central/v/io.reactivex.rxjava2/rxjava.svg'></a>

#### Migrate from 1.0.0

1.1.0 has **MASSIVE** breaking changes you should know about that. 

The following are major changes.

* Migrate to Java Builder to DSL Builder
* Initialize in RxSocialLogin as once
* Call onActivityResult as once
* Migrate receive result with RxSocialLogin.result()

[Release Notes are here](https://github.com/WindSekirun/RxSocialLogin/pull/26)

## Very easy 5-step usage

First, Initialize the module using `ConfigDSLBuilder` in `Application` class. `ConfigDSLBuilder` allows you to configure settings for each platform. 

```kotlin
class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        initSocialLogin {
            facebook(getString(R.string.facebook_api_key)) {
                behaviorOnCancel = true
                requireWritePermissions = false
                imageEnum = FacebookConfig.FacebookImageEnum.Large
            }
        }
    }
}

```

Inside `initSocialLogin` block, you can **use methods which have platform name** such as facebook and google. All parameters except `setup` will necessary information to use SocialLogin feature.

`setup` parameter is function that **provide generate platform config object**(ex, FacebookConfig) and apply additional options such as `behaviorOnCancel`, `imageEnum`. It can be optional, but not nullable parameters.

Although `ConfigDSLBuilder` is *Kotlin Type-Safe builders*, but **it has compatitable with Java language**. we provide `ConfigFunction` with same feature with original `setup` higher-order function.

You can see full examples of `ConfigDSLBuilder` both in [Kotlin](https://github.com/WindSekirun/RxSocialLogin/blob/1.1-dev/demo/src/main/java/com/github/windsekirun/rxsociallogin/test/MainApplication.kt) and [Java](https://github.com/WindSekirun/RxSocialLogin/blob/1.1-dev/demo/src/main/java/com/github/windsekirun/rxsociallogin/test/JavaApplication.java)

Next, Call `RxSocialLogin.initialize(this)` in `onCreate` methods in `Activity` class before execute `RxSocialLogin.result` methods. 

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    ...
    RxSocialLogin.initialize(this)
}
```

From 1.1.0, `RxSocialLogin` class will manage instance of Login object, so you don't need to care about initialization. 

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

## Instructions for use

#### Apply to Proguard

Please refer to [Proguard rule of sample app](https://github.com/WindSekirun/RxSocialLogin/blob/master/demo/proguard-rules.pro).

#### Constraints - all actions should keep the main thread

Everything should work within the main thread. If library use a network inside the library, it will be handled correctly internally using [Fuel](https://github.com/kittinunf/Fuel), so the `Observable` returned by `RxSocialLogin` should keep the main thread. If it is not the main thread, login fails immediately.

In other words, the following cases are not processed and are treated as `LoginFailedException` immediately.

```kotlin
RxSocialLogin.result()
		.subscribeOn(Schedulers.io())
		.observeOn(AndroidSchedulers.mainThread())
		...
```

Due to this constraints, it is not allowed to **start social login right after the network processing, such as `flatMap`**. If you need to handle this case, it is better to call `RxSocialLogin` separately in subscribe after network processing.

#### Occurred OnErrorNotImplementedException

A common error is [OnErrorNotImplementedException](http://reactivex.io/RxJava/javadoc/io/reactivex/exceptions/OnErrorNotImplementedException.html), which is not handled for `onError` at the time of `subscribe`

#### Occurred UndeliverableException

Based on 0.5.0 [UndeliverableException](http://reactivex.io/RxJava/javadoc/io/reactivex/exceptions/UndeliverableException.html) occurs when Exception is not passed to `onError`. You can use `RxJavaPlugins.setErrorHandler { e -> }` to solve the problem, but this will change the overall behavior of RxJavaPlugins.

In 1.0.0 and later, `LoginFailedException` has been changed to inherit` IllegalStateException` to prevent this problem. Therefore, it is not intended to occur in later versions.

See [Error handling](https://github.com/ReactiveX/RxJava/wiki/What's-different-in-2.0#error-handling) for more details.

#### Targeting below of API 21

Currently(1.1.0), **we support API 16 as minSdkVersion**, but `com.microsoft.identify.client:msal` library support API 21 as minSdkVersion.

According [issue #263 of AzureAD/microsoft-authentication-library-for-android](https://github.com/AzureAD/microsoft-authentication-library-for-android/issues/263), You can override this library to avoid conflicts of minSdkVersion.

Place this statement in AndroidManifest.xml to solve this conflicts. we hope microsoft solve this problem asap.

```xml
<uses-sdk tools:overrideLibrary="com.microsoft.identity.msal"/>
```

## Author & Contributor

* Author: [@WindSekirun](https://github.com/windsekirun), E-mail [pyxis@uzuki.live](mailto:pyxis@uzuki.live)
* Contribuor: [Contributors](https://github.com/WindSekirun/RxSocialLogin/graphs/contributors)

[Issue Tracker](https://github.com/WindSekirun/RxSocialLogin/issues) receives a variety of issues including bug findings, improvements, and new platform additions. [Pull Requests](https://github.com/WindSekirun/RxSocialLogin/pulls) is always welcome.

## License

* The ReactiveX logo was taken from [Seeklogo](https://seeklogo.com/vector-logo/284342/reactivex).
* The font used for the logo is Hanken Design Co. [Hanken round](https://www.behance.net/gallery/18871499/Hanken-Round-Free-Typeface) and this font follows SIL OFL. There is a PSD file for the logo in the project.
* Copyright for the platform logo used in the sample exists in each company. The RxSocialLogin library is not associated with the platform company.

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
