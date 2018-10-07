package com.github.windsekirun.rxsociallogin.test

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.github.windsekirun.rxsociallogin.RxSocialLogin
import com.github.windsekirun.rxsociallogin.intenal.model.PlatformType
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.activity_main.*
import pyxis.uzuki.live.richutilskt.utils.getKeyHash


class MainActivity : AppCompatActivity() {
    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(MainActivity::class.java.simpleName, "KeyHash: ${getKeyHash()}")

        btnDisqus.setOnClickListener {
            observeEvent()
            RxSocialLogin.login(PlatformType.DISQUS)
        }

        btnFacebook.setOnClickListener {
            observeEvent()
            RxSocialLogin.login(PlatformType.FACEBOOK)
        }

        btnFoursquare.setOnClickListener {
            observeEvent()
            RxSocialLogin.login(PlatformType.FOURSQUARE)
        }

        btnGithub.setOnClickListener {
            observeEvent()
            RxSocialLogin.login(PlatformType.GITHUB)
        }

        btnGoogle.setOnClickListener {
            observeEvent()
            RxSocialLogin.login(PlatformType.GOOGLE)
        }

        btnKakao.setOnClickListener {
            observeEvent()
            RxSocialLogin.login(PlatformType.KAKAO)
        }

        btnLine.setOnClickListener {
            observeEvent()
            RxSocialLogin.login(PlatformType.LINE)
        }

        btnLinkedin.setOnClickListener {
            observeEvent()
            RxSocialLogin.login(PlatformType.LINKEDIN)
        }

        btnNaver.setOnClickListener {
            observeEvent()
            RxSocialLogin.login(PlatformType.NAVER)
        }

        btnTwitch.setOnClickListener {
            observeEvent()
            RxSocialLogin.login(PlatformType.TWITCH)
        }

        btnTwitter.setOnClickListener {
            observeEvent()
            RxSocialLogin.login(PlatformType.TWITTER)
        }

        btnVK.setOnClickListener {
            observeEvent()
            RxSocialLogin.login(PlatformType.VK)
        }

        btnWordpress.setOnClickListener {
            observeEvent()
            RxSocialLogin.login(PlatformType.WORDPRESS)
        }

        btnWindows.setOnClickListener {
            observeEvent()
            RxSocialLogin.login(PlatformType.WINDOWS)
        }

        btnYahoo.setOnClickListener {
            observeEvent()
            RxSocialLogin.login(PlatformType.YAHOO)
        }

        RxSocialLogin.initialize(this)

        RxSocialLogin.result()
                .subscribe({
                    txtResult.text = it.toString()
                    txtResult.setTextColor(Color.BLACK)
                    txtPlatform.text = it.platform.name
                }, {
                    Log.d(MainActivity::class.java.simpleName, "onError: ${it.message}")
                    txtResult.text = "Login Failed"
                    txtResult.setTextColor(Color.RED)
                }).addTo(compositeDisposable)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        RxSocialLogin.activityResult(requestCode, resultCode, data)
    }

    private fun observeEvent() {

    }
}
