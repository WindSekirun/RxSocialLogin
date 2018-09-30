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
import com.github.windsekirun.rxsociallogin.intenal.model.LoginResultItem
import com.github.windsekirun.rxsociallogin.kakao.KakaoLogin
import com.github.windsekirun.rxsociallogin.line.LineLogin
import com.github.windsekirun.rxsociallogin.linkedin.LinkedinLogin
import com.github.windsekirun.rxsociallogin.naver.NaverLogin
import com.github.windsekirun.rxsociallogin.twitch.TwitchLogin
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
    private val kakaoLogin: KakaoLogin by lazy { KakaoLogin() }
    private val facebookLogin: FacebookLogin by lazy { FacebookLogin() }
    private val naverLogin: NaverLogin by lazy { NaverLogin(this) }
    private val lineLogin: LineLogin by lazy { LineLogin() }
    private val twitterLogin: TwitterLogin by lazy { TwitterLogin() }
    private val googleLogin: GoogleLogin by lazy { GoogleLogin() }
    private val githubLogin: GithubLogin by lazy { GithubLogin() }
    private val linkedinLogin: LinkedinLogin by lazy { LinkedinLogin() }
    private val wordpressLogin: WordpressLogin by lazy { WordpressLogin() }
    private val yahooLogin: YahooLogin by lazy { YahooLogin() }
    private val vkLogin: VKLogin by lazy { VKLogin() }
    private val windowsLogin: WindowsLogin by lazy { WindowsLogin() }
    private val disqusLogin: DisqusLogin by lazy { DisqusLogin() }
    private val foursquareLogin: FoursquareLogin by lazy { FoursquareLogin() }
    private val twitchLogin: TwitchLogin by lazy { TwitchLogin() }
    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(MainActivity::class.java.simpleName, "KeyHash: ${getKeyHash()}")

        btnDisqus.setOnClickListener {
            disqusLogin.login()
        }

        btnFacebook.setOnClickListener {
            facebookLogin.login()
        }

        btnFoursquare.setOnClickListener {
            foursquareLogin.login()
        }

        btnGithub.setOnClickListener {
            githubLogin.login()
        }

        btnGoogle.setOnClickListener {
            googleLogin.login()
        }

        btnKakao.setOnClickListener {
            kakaoLogin.login()
        }

        btnLine.setOnClickListener {
            lineLogin.login()
        }

        btnLinkedin.setOnClickListener {
            linkedinLogin.login()
        }

        btnNaver.setOnClickListener {
            naverLogin.login()
        }

        btnTwitch.setOnClickListener {
            twitchLogin.login()
        }

        btnTwitter.setOnClickListener {
            twitterLogin.login()
        }

        btnVK.setOnClickListener {
            vkLogin.login()
        }

        btnWindows.setOnClickListener {
            windowsLogin.login()
        }

        btnYahoo.setOnClickListener {
            yahooLogin.login()
        }

        RxSocialLogin.result(disqusLogin, facebookLogin, foursquareLogin, githubLogin, googleLogin,
                kakaoLogin, lineLogin, linkedinLogin, naverLogin, twitchLogin, twitchLogin, vkLogin,
                windowsLogin, yahooLogin)
                .subscribe({
                    txtResult.text = it.toString()
                    txtResult.setTextColor(Color.BLACK)
                    txtPlatform.text = it.platform.name
                }, {
                    Log.d(MainActivity::class.java.simpleName, "onError: ${it.message}")
                    txtResult.text = "Login Failed"
                    txtResult.setTextColor(Color.RED)
                })

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
