# RxSocialLogin [![CircleCI](https://circleci.com/gh/WindSekirun/RxSocialLogin.svg?style=svg)](https://circleci.com/gh/WindSekirun/RxSocialLogin) [![](https://jitpack.io/v/WindSekirun/RxSocialLogin.svg)](https://jitpack.io/#WindSekirun/RxSocialLogin)

 [![](https://img.shields.io/badge/Android%20Arsenal-RxSocialLogin-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/7028)  [![Awesome Kotlin Badge](https://kotlin.link/awesome-kotlin.svg)](https://github.com/KotlinBy/awesome-kotlin)


![](graphics/logo.png)

*The license information for logo is located at the bottom of the document.*

These instructions are available in their respective languages.

* [English](README.md) - Latest update: 2018-10-10, [@WindSekirun](https://github.com/windsekirun)
* [한국어](README-ko.md) - Latest update: 2018-10-10, [@WindSekirun](https://github.com/windsekirun)
* [日本語](README-jp.md) - Latest update: 2018-10-10, [@WindSekirun](https://github.com/windsekirun)

## Introduction
このAndroidライブラリは、[RxJava2](https://github.com/ReactiveX/RxJava)、[Kotlin](http://kotlinlang.org/)、[Firebase 認証](https://firebase.google.com/docs/auth/)を搭載した15プラットフォームのソーシャルログインを提供するライブラリです。

このライブラリは、[@WindSekirun](https://github.com/windsekirun)の[SocialLogin](https://github.com/WindSekirun/SocialLogin)ライブラリの改良版です。 以下は、ライブラリの変更点です。

* 結果の配信方法が'Listener'の代わりに'RxJava'を経由するように変更されました。
* Javaで書かれた元と比較して、改良されたバージョンはKotlinでのみ書かれています。
* サポートされている元の6プラットフォームと比較して、改良されたバージョンは15プラットフォームをサポートしています。
* Kotlinに作成された*Type-Safe builder*を提供しています。
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
	implementation 'com.github.WindSekirun:RxSocialLogin:1.1.0'
    
	// RxJava
	implementation 'io.reactivex.rxjava2:rxandroid:lastest-version'
	implementation 'io.reactivex.rxjava2:rxjava:lastest-version'
}
```

RxJavaはアクティブなライブラリです。新しい拡張機能を有効にするには、常に最新のバージョンを保持する必要があります。 したがって、RxJavaを依存関係の末尾に追加することをお勧めします。

* RxAndroid: <a href='http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22io.reactivex.rxjava2%22%20a%3A%22rxandroid%22'><img src='http://img.shields.io/maven-central/v/io.reactivex.rxjava2/rxandroid.svg'></a>

* RxJava: <a href='http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22io.reactivex.rxjava2%22%20a%3A%22rxjava%22'><img src='http://img.shields.io/maven-central/v/io.reactivex.rxjava2/rxjava.svg'></a>

#### 1.0.0での移行

1.1.0は1.0.0に比べて理解すべき大きな変化があります。以下は、その変化点です。

- JavaのビルダーからDSLビルダーへの移行
- RxSocialLoginのインスタンスの管理
- onActivityResultイベント共通化
- 結果を購読方法を変更する

[Release Notesは,ここで見ることができます](https://github.com/WindSekirun/RxSocialLogin/pull/26)

## 簡単な5ステップの使用方法

まず、 `Application`クラスの` ConfigDSLBuilder`を使ってモジュールを初期化します。`ConfigDSLBuilder`は、プラットフォームに合わせて設定を構成することができるように提供しています。

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

`initSocialLogin`ブロック内でfacebook、googleのような**プラットフォーム名を持つメソッドを使用することができます。**` setup`パラメータを除いた残りのパラメータは、ソーシャルログイン機能を使用するために必要な情報です。

`setup`パラメータは、生成されたConfigオブジェクト（例えば、FacebookConfig）を提供し、` behaviorOnCancel`、`imageEnum`などの追加オプションを提供する関数です。メソッドに提供しなくても、nullに提供することはできません。

たとえ`ConfigDSLBuilder`が*Kotlin Type-Safe builders*で構成されても、**はまだJava言語との互換性になります。**オリジナルの` setup`高階関数が提供する機能を`ConfigFunction`というインターフェースで提供します。

[Kotlin](https://github.com/WindSekirun/RxSocialLogin/blob/1.1-dev/demo/src/main/java/com/github/windsekirun/rxsociallogin/test/MainApplication.kt) と [Java](https://github.com/WindSekirun/RxSocialLogin/blob/1.1-dev/demo/src/main/java/com/github/windsekirun/rxsociallogin/test/JavaApplication.java) にされた`ConfigDSLBuilder`の完全な例を見ることができています。

次に、 `Activity`クラスの `onStart` メソッドで `RxSocialLogin.initialize(this)`を呼び出します。

```kotlin
override fun onStart() {
    super.onStart()
    RxSocialLogin.initialize(this)
}
```

1.1.0から`RxSocialLogin`クラスがログインオブジェクトのインスタンスを管理するため、ログインオブジェクトの初期化を気にする必要はありません。

次に、`onActivityResult`メソッドで`RxSocialLogin.activityResult（requestCode、resultCode、data）` を呼び出します。

```kotlin
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent ? ) {
    super.onActivityResult(requestCode, resultCode, data)
    RxSocialLogin.activityResult(requestCode, resultCode, data)
}
```

次に、結果を得ようとするところえ `RxSocialLogin.result`を呼び出します。アクティビティのほかのクラスにも使用が可能です。

```kotlin
RxSocialLogin.result()
    .subscribe({ item -> 

    }, { throwable ->

    }).addTo(compositeDisposable)
```

最後に、`RxSocialLogin.login（PlatformType.FACEBOOK）`を呼び出して、ソーシャルログイン機能を開始します。

### 使用にについての説明

#### Apply to Proguard
[サンプルアプリケーションのProguardルール](https://github.com/WindSekirun/RxSocialLogin/blob/master/demo/proguard-rules.pro) を参照してください。

#### 制約 - すべてのアクションはメインスレッドを保持する必要があります
すべてのアクションはメインスレッドを保持する必要があります。 ライブラリが内でネットワークを使用している場合、[Fuel](https://github.com/kittinunf/Fuel)を使用して内部的に正しく処理されるため、 `RxSocialLogin`によって返される` Observable`はメインスレッドを保持します。 メインスレッドでない場合は、すぐにログインに失敗します。

言い換えれば、以下のケースは処理されず、即座に `LoginFailedException`として扱われます。

```kotlin
RxSocialLogin.result()
		.subscribeOn(Schedulers.io())
		.observeOn(AndroidSchedulers.mainThread())
		...
```

この制約のため、**flatmapなのでネットワーク処理の直後にソーシャルログインを開始することはできません**。このケースを処理する必要がある場合は、ネットワーク処理後にサブスクライブする際に `RxSocialLogin`を別々に呼び出す方が良いと思います。

#### OnErrorNotImplementedExceptionが発生しました
一般的なエラーは[OnErrorNotImplementedException](http://reactivex.io/RxJava/javadoc/io/reactivex/exceptions/OnErrorNotImplementedException.html)です。これは `subscribe`時に` onError`を適切に処理していないからです。

#### UndeliverableExceptionが発生しました
0.5.0に基づいて、例外が `onError`に渡されないとき、[UndeliverableException](http://reactivex.io/RxJava/javadoc/io/reactivex/exceptions/UndeliverableException.html)が発生されます。`RxJavaPlugins.setErrorHandler { e -> }`によってこの問題を解決する事が出来るですが、ごのメソッドによってRxJavaPlugins全ての行動が変わるので、ご注意ください。

1.0.0以降では、この問題を解決するために `LoginFailedException`が` IllegalStateException`を継承するように変更されました。 したがって、それ以降のバージョンでは発生しません。

詳細については、[Error handling](https://github.com/ReactiveX/RxJava/wiki/What's-different-in-2.0#error-handling)を参照してください。

#### Targeting below of API 21

現在（1.1.0）、**minSdkVersionにAPI16をサポートしているが**、`` com.microsoft.identify.client：msal``ライブラリがminSdkVersionにAPI26をサポートしています。

android](https://github.com/AzureAD/microsoft-authentication-library-for-android/issues/263) によると、ライブラリをオーバーライドすることでminSdkVersionの衝突を回避することができます。

競合を解決するために、次のコードをAndroidManifest.xmlに挿入します。

```xml
<uses-sdk tools:overrideLibrary="com.microsoft.identity.msal"/>
```

## 著者&貢献者
* 著者: [@WindSekirun](https://github.com/windsekirun), E-mail [pyxis@uzuki.live](mailto:pyxis@uzuki.live)
* 貢献者: [Contributors](https://github.com/WindSekirun/RxSocialLogin/graphs/contributors)

[Issue Tracker](https://github.com/WindSekirun/RxSocialLogin/issues)には、バグの発見、改善、新しいプラットフォームの追加など、さまざまな問題を受け取ります。[Pull Request](https://github.com/WindSekirun/RxSocialLogin/pulls)はいつでも歓迎します。

## ライセンス
* ReactiveXロゴは[Seeklogo](https://seeklogo.com/vector-logo/284342/reactivex)から取得しました。
* ロゴに使用されるフォントは'Hanken Design Co.'さんの[Hanken round](https://www.behance.net/gallery/18871499/Hanken-Round-Free-Typeface)で、このフォントはSIL OFLに従います。 プロジェクトにはロゴ用のPSDファイルがあります。
* サンプルに使用されているプラットフォームロゴの著作権は、各社に存在します。 RxSocialLoginライブラリは、プラットフォーム会社に関連付けられていません。

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
