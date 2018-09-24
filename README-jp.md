# RxSocialLogin [![CircleCI](https://circleci.com/gh/WindSekirun/RxSocialLogin.svg?style=svg)](https://circleci.com/gh/WindSekirun/RxSocialLogin) [![](https://jitpack.io/v/WindSekirun/RxSocialLogin.svg)](https://jitpack.io/#WindSekirun/RxSocialLogin)

 [![](https://img.shields.io/badge/Android%20Arsenal-RxSocialLogin-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/7028)

![](graphics/logo.png)

*The license information for logo is located at the bottom of the document.*

These instructions are available in their respective languages.

* [English](README.md) - Latest update: 2018-08-25, [@WindSekirun](https://github.com/windsekirun)
* [한국어](README-ko.md) - Latest update: 2018-08-25, [@WindSekirun](https://github.com/windsekirun)
* [日本語](README-JP.md) - Latest update: 2018-08-25, [@WindSekirun](https://github.com/windsekirun)

## Introduction

このAndroidライブラリは、[RxJava2](https://github.com/ReactiveX/RxJava)、[Kotlin](http://kotlinlang.org/)、[Firebase 認証](https://firebase.google.com/docs/auth/)を搭載した15プラットフォームのソーシャルログインを提供するライブラリです。

このライブラリは、[@WindSekirun](https://github.com/windsekirun)の[SocialLogin](https://github.com/WindSekirun/SocialLogin)ライブラリの改良版です。 以下は、ライブラリの変更点です。

* 結果の配信方法が'Listener'の代わりに'RxJava'を経由するように変更されました。
* Javaで書かれた元と比較して、改良されたバージョンはKotlinでのみ書かれています。
* サポートされている元の6プラットフォームと比較して、改良されたバージョンは15プラットフォームをサポートしています。
* すべてのメソッドとコードが書き直されました。
* Kotlinで書かれているが、Javaと互換性があるように作成しました。

## サポートされているプラットフォーム

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

プラットフォームの名前をクリックして、プラットフォームの適用方法に移動します。

## インポート

ルートフォルダの `build.gradle`に次のコードを追加します。

```groovy
allprojects {
	repositories {
		maven { url 'http://devrepo.kakao.com:8088/nexus/content/groups/public/' }
		maven { url 'https://jitpack.io' }
	}
}
```

使用するモジュールの `build.gradle`に次の依存関係を追加します。

```groovy
dependencies {
	implementation 'com.github.WindSekirun:RxSocialLogin:1.0.0'
    
	// RxJava
	implementation 'io.reactivex.rxjava2:rxandroid:lastest-version'
	implementation 'io.reactivex.rxjava2:rxjava:lastest-version'
}
```

RxJavaはアクティブなライブラリです。新しい拡張機能を有効にするには、常に最新のバージョンを保持する必要があります。 したがって、RxJavaを依存関係の末尾に追加することをお勧めします。

* RxAndroid: <a href='http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22io.reactivex.rxjava2%22%20a%3A%22rxandroid%22'><img src='http://img.shields.io/maven-central/v/io.reactivex.rxjava2/rxandroid.svg'></a>

* RxJava: <a href='http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22io.reactivex.rxjava2%22%20a%3A%22rxjava%22'><img src='http://img.shields.io/maven-central/v/io.reactivex.rxjava2/rxjava.svg'></a>

## 簡単な5ステップの使用方法

まず、`Application`クラスの`RxSocialLogin.init（this）`でモジュールを初期化し、プラットフォームごとにConfigオブジェクトを宣言します。Configオブジェクトは、プラットフォームを使用するために必要な情報です。プラットフォームの設定情報については、上記の「サポートされているプラットフォーム」セクションのプラットフォームをクリックして、wikiを参照してください。

`RxSocialLogin.init（this）`は一度だけ呼び出される必要があることに注意してください。

```kotlin
RxSocialLogin.init(this)

val facebookConfig = FacebookConfig.Builder()
	.setApplicationId(getString(R.string.facebook_api_key))
	.setRequireEmail()
	.setBehaviorOnCancel()
	.build()

RxSocialLogin.addType(PlatformType.FACEBOOK, facebookConfig)
```

次に、グローバル変数として使用するコードで使用する'Platform + Login'という名前のクラスのインスタンスを作成してください。ここでは**ソーシャルモジュール変数**と仮定します。

```kotlin
private val facebookLogin: FacebookLogin by lazy { FacebookLogin() }
```

次に、ActivityのonActivityResultで、対応するソーシャルモジュール変数の `onActivityResult`メソッドを呼び出してください。

```kotlin
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
	super.onActivityResult(requestCode, resultCode, data)
	facebookLogin.onActivityResult(requestCode, resultCode, data)
}
```

次に、`RxSocialLogin`クラス対応するソーシャルモジュール変数を渡すことによって、`Observable`を得ることができます。

```kotlin
RxSocialLogin.facebook(facebookLogin)
	.subscribe(data -> {
		// TODO: do job with LoginResultItem
	}, error -> {
		// TODO: Error on login()
	});
```

最後に、ソーシャル・ログインを開始する時、ソーシャル・モジュール変数の `login`メソッドを呼び出してソーシャル・ログインを開始します。

```kotlin
facebookLogin.login()
```

#### [JakeWharton/RxBinding](https://github.com/JakeWharton/RxBinding) で使う
自然な使用のために [JakeWharton/RxBinding](https://github.com/JakeWharton/RxBinding) と共に使用することができます。 しかし、我々はこのアプローチが主要な方法ではないことを知っています。

```kotlin
btnFacebook.clicks()
	.doOnNext { facebookLogin.login() }
	.flatMap { facebookLogin.toObservable() }
	.subscribe(consumer, error)
	.addTo(compositeDisposable)
```

### 使用にについての説明

#### ソーシャルモジュール変数の問題。

現在、2つのタイプのコンストラクタがあります。

* FacebookLogin() - 主なコンストラクタ
* FacebookLogin(activity: FragmentActivity) - セカンダリコンストラクタ

セカンダリコンストラクタを使用する場合は、セカンダリコンストラクタで提供される `FragmentActivity`オブジェクトを使用します。 それ以外の場合は、 `FragmentActivity`オブジェクトを使用して内部的にキャッシュします。

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

* Author: @WindSekirun, E-mail [pyxis@uzuki.live](mailto:pyxis@uzuki.live)
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