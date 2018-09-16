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
import com.github.windsekirun.rxsociallogin.twitter.TwitterLogin
import com.github.windsekirun.rxsociallogin.vk.VKLogin
import com.github.windsekirun.rxsociallogin.windows.WindowsLogin
import com.github.windsekirun.rxsociallogin.wordpress.WordpressLogin
import com.github.windsekirun.rxsociallogin.yahoo.YahooLogin
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

        // warning, do this in MainThread.
        RxSocialLogin.kakao(kakaoLogin)
                .subscribe(consumer, error)
                .addTo(compositeDisposable)

        RxSocialLogin.facebook(facebookLogin)
                .subscribe(consumer, error)
                .addTo(compositeDisposable)

        RxSocialLogin.naver(naverLogin)
                .subscribe(consumer, error)
                .addTo(compositeDisposable)

        RxSocialLogin.line(lineLogin)
                .subscribe(consumer, error)
                .addTo(compositeDisposable)

        RxSocialLogin.twitter(twitterLogin)
                .subscribe(consumer, error)
                .addTo(compositeDisposable)

        RxSocialLogin.google(googleLogin)
                .subscribe(consumer, error)
                .addTo(compositeDisposable)

        RxSocialLogin.github(githubLogin)
                .subscribe(consumer, error)
                .addTo(compositeDisposable)

        RxSocialLogin.linkedin(linkedinLogin)
                .subscribe(consumer, error)
                .addTo(compositeDisposable)

        RxSocialLogin.wordpress(wordpressLogin)
                .subscribe(consumer, error)
                .addTo(compositeDisposable)

        RxSocialLogin.yahoo(yahooLogin)
                .subscribe(consumer, error)
                .addTo(compositeDisposable)

        RxSocialLogin.vk(vkLogin)
                .subscribe(consumer, error)
                .addTo(compositeDisposable)

        RxSocialLogin.windows(windowsLogin)
                .subscribe(consumer, error)
                .addTo(compositeDisposable)

        RxSocialLogin.disqus(disqusLogin)
                .subscribe(consumer, error)
                .addTo(compositeDisposable)

        RxSocialLogin.foursquare(foursquareLogin)
                .subscribe(consumer, error)
                .addTo(compositeDisposable)

        btnKakao.setOnClickListener {
            kakaoLogin.onLogin()
        }

        btnFacebook.setOnClickListener {
            facebookLogin.onLogin()
        }

        btnNaver.setOnClickListener {
            naverLogin.onLogin()
        }

        btnLine.setOnClickListener {
            lineLogin.onLogin()
        }

        btnTwitter.setOnClickListener {
            twitterLogin.onLogin()
        }

        btnGoogle.setOnClickListener {
            googleLogin.onLogin()
        }

        btnGithub.setOnClickListener {
            githubLogin.onLogin()
        }

        btnLinkedin.setOnClickListener {
            linkedinLogin.onLogin()
        }

        btnWordpress.setOnClickListener {
            wordpressLogin.onLogin()
        }

        btnYahoo.setOnClickListener {
            yahooLogin.onLogin()
        }

        btnVK.setOnClickListener {
            vkLogin.onLogin()
        }

        btnWindows.setOnClickListener {
            windowsLogin.onLogin()
        }

        btnFoursquare.setOnClickListener {
            foursquareLogin.onLogin()
        }

        btnDisqus.setOnClickListener {
            disqusLogin.onLogin()
        }

        btnTwitch.setOnClickListener {

        }
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
    }
}
