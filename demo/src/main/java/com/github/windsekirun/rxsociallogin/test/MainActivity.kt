package com.github.windsekirun.rxsociallogin.test

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.github.windsekirun.rxsociallogin.RxSocialLogin
import com.github.windsekirun.rxsociallogin.disqus.DisqusLogin
import com.github.windsekirun.rxsociallogin.facebook.FacebookLogin
import com.github.windsekirun.rxsociallogin.foursquare.FoursquareLogin
import com.github.windsekirun.rxsociallogin.github.GithubLogin
import com.github.windsekirun.rxsociallogin.google.GoogleLogin
import com.github.windsekirun.rxsociallogin.kakao.KakaoLogin
import com.github.windsekirun.rxsociallogin.line.LineLogin
import com.github.windsekirun.rxsociallogin.linkedin.LinkedinLogin
import com.github.windsekirun.rxsociallogin.model.LoginResultItem
import com.github.windsekirun.rxsociallogin.naver.NaverLogin
import com.github.windsekirun.rxsociallogin.twitch.TwitchLogin
import com.github.windsekirun.rxsociallogin.twitter.TwitterLogin
import com.github.windsekirun.rxsociallogin.vk.VKLogin
import com.github.windsekirun.rxsociallogin.windows.WindowsLogin
import com.github.windsekirun.rxsociallogin.wordpress.WordpressLogin
import com.github.windsekirun.rxsociallogin.yahoo.YahooLogin
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.activity_main.*
import pyxis.uzuki.live.richutilskt.utils.getKeyHash


class MainActivity : AppCompatActivity() {
    private val kakaoLogin: KakaoLogin by lazy { KakaoLogin(this) }
    private val facebookLogin: FacebookLogin by lazy { FacebookLogin(this) }
    private val naverLogin: NaverLogin by lazy { NaverLogin(this) }
    private val lineLogin: LineLogin by lazy { LineLogin(this) }
    private val twitterLogin: TwitterLogin by lazy { TwitterLogin(this) }
    private val googleLogin: GoogleLogin by lazy { GoogleLogin(this) }
    private val githubLogin: GithubLogin by lazy { GithubLogin(this) }
    private val linkedinLogin: LinkedinLogin by lazy { LinkedinLogin(this) }
    private val wordpressLogin: WordpressLogin by lazy { WordpressLogin(this) }
    private val yahooLogin: YahooLogin by lazy { YahooLogin(this) }
    private val vkLogin: VKLogin by lazy { VKLogin(this) }
    private val windowsLogin: WindowsLogin by lazy { WindowsLogin(this) }
    private val disqusLogin: DisqusLogin by lazy { DisqusLogin(this) }
    private val foursquareLogin: FoursquareLogin by lazy { FoursquareLogin(this) }
    private val twitchLogin: TwitchLogin by lazy { TwitchLogin(this) }
    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(MainActivity::class.java.simpleName, "KeyHash: ${getKeyHash()}")

        val consumer = Consumer<LoginResultItem> {
            txtResult.text = it.toString()
            txtResult.setTextColor(Color.BLACK)
            txtPlatform.text = it.platform.name
        }

        val error = Consumer<Throwable> {
            Log.d(MainActivity::class.java.simpleName, "onError: ${it.message}")
            txtResult.text = "Login Failed"
            txtResult.setTextColor(Color.RED)
        }

        btnDisqus.clicks()
                .doOnNext { disqusLogin.onLogin() }
                .flatMap { disqusLogin.toObservable() }
                .subscribe(consumer, error)
                .addTo(compositeDisposable)

        btnFacebook.clicks()
                .doOnNext { facebookLogin.onLogin() }
                .flatMap { facebookLogin.toObservable() }
                .subscribe(consumer, error)
                .addTo(compositeDisposable)

        btnFoursquare.clicks()
                .doOnNext { foursquareLogin.onLogin() }
                .flatMap { foursquareLogin.toObservable() }
                .subscribe(consumer, error)
                .addTo(compositeDisposable)

        btnGithub.clicks()
                .doOnNext { githubLogin.onLogin() }
                .flatMap { githubLogin.toObservable() }
                .subscribe(consumer, error)
                .addTo(compositeDisposable)

        btnGoogle.clicks()
                .doOnNext { googleLogin.onLogin() }
                .flatMap { googleLogin.toObservable() }
                .subscribe(consumer, error)
                .addTo(compositeDisposable)

        btnKakao.clicks()
                .doOnNext { kakaoLogin.onLogin() }
                .flatMap { kakaoLogin.toObservable() }
                .subscribe(consumer, error)
                .addTo(compositeDisposable)

        btnLine.clicks()
                .doOnNext { lineLogin.onLogin() }
                .flatMap { lineLogin.toObservable() }
                .subscribe(consumer, error)
                .addTo(compositeDisposable)

        btnLinkedin.clicks()
                .doOnNext { linkedinLogin.onLogin() }
                .flatMap { linkedinLogin.toObservable() }
                .subscribe(consumer, error)
                .addTo(compositeDisposable)

        btnNaver.clicks()
                .doOnNext { naverLogin.onLogin() }
                .flatMap { naverLogin.toObservable() }
                .subscribe(consumer, error)
                .addTo(compositeDisposable)

        btnTwitch.clicks()
                .doOnNext { twitchLogin.onLogin() }
                .flatMap { twitchLogin.toObservable() }
                .subscribe(consumer, error)
                .addTo(compositeDisposable)

        btnTwitter.clicks()
                .doOnNext { twitterLogin.onLogin() }
                .flatMap { twitterLogin.toObservable() }
                .subscribe(consumer, error)
                .addTo(compositeDisposable)

        btnVK.clicks()
                .doOnNext { vkLogin.onLogin() }
                .flatMap { vkLogin.toObservable() }
                .subscribe(consumer, error)
                .addTo(compositeDisposable)

        btnWindows.clicks()
                .doOnNext { windowsLogin.onLogin() }
                .flatMap { windowsLogin.toObservable() }
                .subscribe(consumer, error)
                .addTo(compositeDisposable)

        btnWordpress.clicks()
                .doOnNext { wordpressLogin.onLogin() }
                .flatMap { wordpressLogin.toObservable() }
                .subscribe(consumer, error)
                .addTo(compositeDisposable)

        btnYahoo.clicks()
                .doOnNext { yahooLogin.onLogin() }
                .flatMap { yahooLogin.toObservable() }
                .subscribe(consumer, error)
                .addTo(compositeDisposable)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        kakaoLogin.onActivityResult(requestCode, resultCode, data)
        facebookLogin.onActivityResult(requestCode, resultCode, data)
        naverLogin.onActivityResult(requestCode, resultCode, data)
        lineLogin.onActivityResult(requestCode, resultCode, data)
        twitterLogin.onActivityResult(requestCode, resultCode, data)
        googleLogin.onActivityResult(requestCode, resultCode, data)
        githubLogin.onActivityResult(requestCode, resultCode, data)
        linkedinLogin.onActivityResult(requestCode, resultCode, data)
        wordpressLogin.onActivityResult(requestCode, resultCode, data)
        yahooLogin.onActivityResult(requestCode, resultCode, data)
        vkLogin.onActivityResult(requestCode, resultCode, data)
        windowsLogin.onActivityResult(requestCode, resultCode, data)
        disqusLogin.onActivityResult(requestCode, resultCode, data)
        foursquareLogin.onActivityResult(requestCode, resultCode, data)
        twitchLogin.onActivityResult(requestCode, resultCode, data)
    }
}
