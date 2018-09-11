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
import kotlinx.android.synthetic.main.activity_main.*
import pyxis.uzuki.live.richutilskt.utils.getKeyHash


class MainActivity : AppCompatActivity() {
    private lateinit var kakaoLogin: KakaoLogin
    private lateinit var facebookLogin: FacebookLogin
    private lateinit var naverLogin: NaverLogin
    private lateinit var lineLogin: LineLogin
    private lateinit var twitterLogin: TwitterLogin
    private lateinit var googleLogin: GoogleLogin
    private lateinit var githubLogin: GithubLogin
    private lateinit var linkedinLogin: LinkedinLogin
    private lateinit var wordpressLogin: WordpressLogin
    private lateinit var yahooLogin: YahooLogin
    private lateinit var vkLogin: VKLogin
    private lateinit var windowsLogin: WindowsLogin
    private lateinit var disqusLogin: DisqusLogin
    private lateinit var foursquareLogin: FoursquareLogin
    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(MainActivity::class.java.simpleName, "KeyHash: ${getKeyHash()}")

        kakaoLogin = KakaoLogin(this)
        facebookLogin = FacebookLogin(this)
        naverLogin = NaverLogin(this)
        lineLogin = LineLogin(this)
        twitterLogin = TwitterLogin(this)
        googleLogin = GoogleLogin(this)
        githubLogin = GithubLogin(this)
        linkedinLogin = LinkedinLogin(this)
        wordpressLogin = WordpressLogin(this)
        yahooLogin = YahooLogin(this)
        vkLogin = VKLogin(this)
        windowsLogin = WindowsLogin(this)
        disqusLogin = DisqusLogin(this)
        foursquareLogin = FoursquareLogin(this)

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
        val kakaoDisposable = RxSocialLogin.kakao(kakaoLogin)
                .subscribe(consumer, error)

        val facebookDisposable = RxSocialLogin.facebook(facebookLogin)
                .subscribe(consumer, error)

        val naverDisposable = RxSocialLogin.naver(naverLogin)
                .subscribe(consumer, error)

        val lineDisposable = RxSocialLogin.line(lineLogin)
                .subscribe(consumer, error)

        val twitterDisposable = RxSocialLogin.twitter(twitterLogin)
                .subscribe(consumer, error)

        val googleDisposable = RxSocialLogin.google(googleLogin)
                .subscribe(consumer, error)

        val githubDisposable = RxSocialLogin.github(githubLogin)
                .subscribe(consumer, error)

        val linkedinDisposable = RxSocialLogin.linkedin(linkedinLogin)
                .subscribe(consumer, error)

        val wordpressDisposable = RxSocialLogin.wordpress(wordpressLogin)
                .subscribe(consumer, error)

        val yahooDisposable = RxSocialLogin.yahoo(yahooLogin)
                .subscribe(consumer, error)

        val vkDisposable = RxSocialLogin.vk(vkLogin)
                .subscribe(consumer, error)

        val windowsDisposable = RxSocialLogin.windows(windowsLogin)
                .subscribe(consumer, error)

        val disqusDisposable = RxSocialLogin.disqus(disqusLogin)
                .subscribe(consumer, error)

        val foursquareDisposable = RxSocialLogin.foursquare(foursquareLogin)
                .subscribe(consumer, error)

        compositeDisposable.add(kakaoDisposable)
        compositeDisposable.add(facebookDisposable)
        compositeDisposable.add(naverDisposable)
        compositeDisposable.add(lineDisposable)
        compositeDisposable.add(twitterDisposable)
        compositeDisposable.add(googleDisposable)
        compositeDisposable.add(githubDisposable)
        compositeDisposable.add(linkedinDisposable)
        compositeDisposable.add(wordpressDisposable)
        compositeDisposable.add(yahooDisposable)
        compositeDisposable.add(vkDisposable)
        compositeDisposable.add(windowsDisposable)
        compositeDisposable.add(disqusDisposable)
        compositeDisposable.add(foursquareDisposable)

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
