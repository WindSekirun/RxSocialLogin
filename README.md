# RxSocialLogin [![CircleCI](https://circleci.com/gh/WindSekirun/RxSocialLogin.svg?style=svg)](https://circleci.com/gh/WindSekirun/RxSocialLogin) [![](https://jitpack.io/v/WindSekirun/RxSocialLogin.svg)](https://jitpack.io/#WindSekirun/RxSocialLogin)

 [![](https://img.shields.io/badge/Android%20Arsenal-RxSocialLogin-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/7028) [![Awesome Kotlin Badge](https://kotlin.link/awesome-kotlin.svg)](https://github.com/KotlinBy/awesome-kotlin)

![](graphics/logo.png)

*The license information for logo is located at the bottom of the document.*

These instructions are available in their respective languages.

* [English](README.md) - Latest update: 2018-09-30, [@WindSekirun](https://github.com/windsekirun)
* [한국어](README-ko.md) - Latest update: 2018-09-30, [@WindSekirun](https://github.com/windsekirun)
* [日本語](README-jp.md) - Latest update: 2018-09-30, [@WindSekirun](https://github.com/windsekirun)

## Introduction

This Android library is a library that provides social login for 15 platforms powered by [RxJava2](https://github.com/ReactiveX/RxJava), [Kotlin](http://kotlinlang.org/) and [Firebase Authentication](https://firebase.google.com/docs/auth/).

This library is an improved version of [@WindSekirun](https://github.com/windsekirun) 's [SocialLogin](https://github.com/WindSekirun/SocialLogin) library. It has the following differences.

* The result delivery method has been changed to be passed through RxJava instead of the Listener.
* Compared to the original written in Java, the improved version is written in Kotlin only.
* Compared to the original supported 6 platforms, the improved version is support 15 platforms.
* All methods and code have been rewritten.
* All code that are written in Kotlin but considered to be Java compatible.

## Supported Platforms

| Platform                                                       | Data                                                       | Version  |
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
	implementation 'com.github.WindSekirun:RxSocialLogin:1.0.0'
    
	// RxJava
	implementation 'io.reactivex.rxjava2:rxandroid:lastest-version'
	implementation 'io.reactivex.rxjava2:rxjava:lastest-version'
}
```

RxJava is an active library, and you should always keep the latest version for new enhancements to take effect. Therefore, we recommend that you add RxJava to the bottom of the dependency.

* RxAndroid: <a href='http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22io.reactivex.rxjava2%22%20a%3A%22rxandroid%22'><img src='http://img.shields.io/maven-central/v/io.reactivex.rxjava2/rxandroid.svg'></a>

* RxJava: <a href='http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22io.reactivex.rxjava2%22%20a%3A%22rxjava%22'><img src='http://img.shields.io/maven-central/v/io.reactivex.rxjava2/rxjava.svg'></a>

## Very easy 5-step usage

First, initialize the moduel by `RxSocialLogin.init(this)` in `Application` class and declare Config object for each platform. There Config object is necessary information to using platform, For config information for each platform, please click on each platform in the "Supported Platforms" section above to see the wiki. 

Note that `RxSocialLogin.init (this)` only needs to be called once.

```kotlin
RxSocialLogin.init(this)

val facebookConfig = FacebookConfig.Builder()
	.setApplicationId(getString(R.string.facebook_api_key))
	.setRequireEmail()
	.setBehaviorOnCancel()
	.build()

RxSocialLogin.addType(PlatformType.FACEBOOK, facebookConfig)
```

Then create an instance of the class named Platform + Login to use in the code you want to use as a global variable (defined here as **social module variable**).

```kotlin
private val facebookLogin: FacebookLogin by lazy { FacebookLogin() }
```

Then, in the onActivityResult of the activity, call the `onActivityResult` method of the corresponding social module variable.

```kotlin
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
	super.onActivityResult(requestCode, resultCode, data)
	facebookLogin.onActivityResult(requestCode, resultCode, data)
}
```

Next, we pass `Observable` to each platform method of the` RxSocialLogin` class by passing the variable of the corresponding social module.

```kotlin
RxSocialLogin.facebook(facebookLogin)
	.subscribe(data -> {
		// TODO: do job with LoginResultItem
	}, error -> {
		// TODO: Error on login()
	});
```

Finally, you start a social login by calling the `login` method of the social module variable where you want to start the social login (when the user requests a social login).

```kotlin
facebookLogin.login()
```

#### Using with [JakeWharton/RxBinding](https://github.com/JakeWharton/RxBinding)

You can use it with [JakeWharton/RxBinding](https://github.com/JakeWharton/RxBinding) for natural use. However, we know this approach is not the primary method.

```kotlin
btnFacebook.clicks()
	.doOnNext { facebookLogin.login() }
	.flatMap { facebookLogin.toObservable() }
	.subscribe(consumer, error)
	.addTo(compositeDisposable)
```

### Instructions for use

#### Social module variable matters.

There are currently two types of constructors.

* FacebookLogin() - Primary constructors.
* FacebookLogin(activity: FragmentActivity) - Secondary constructors.

If you use Secondary constructors, use the `FragmentActivity` object provided in the Seconday constructor. Otherwise, use the `FragmentActivity` object to cache internally.

However, there may be a module that throw an error when it is created as the default constructor, so it is better to pass a `FragmentActivity` object through the Secondary constructor whenever possible.

#### Apply to Proguard

Please refer to [Proguard rule of sample app](https://github.com/WindSekirun/RxSocialLogin/blob/master/demo/proguard-rules.pro).

#### Constraints - all actions should keep the main thread

Everything should work within the main thread. If library use a network inside the library, it will be handled correctly internally using [Fuel](https://github.com/kittinunf/Fuel), so the `Observable` returned by `RxSocialLogin` should keep the main thread. If it is not the main thread, login fails immediately.

In other words, the following cases are not processed and are treated as `LoginFailedException` immediately.

```kotlin
RxSocialLogin.facebook(facebookLogin)
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
