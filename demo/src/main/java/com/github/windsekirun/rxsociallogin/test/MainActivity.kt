package com.github.windsekirun.rxsociallogin.test

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import com.github.windsekirun.rxsociallogin.RxSocialLogin
import com.github.windsekirun.rxsociallogin.intenal.model.PlatformType
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.activity_main.*
import pyxis.uzuki.live.richutilskt.utils.getKeyHash


class MainActivity : AppCompatActivity() {
    private val compositeDisposable = CompositeDisposable()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(MainActivity::class.java.simpleName, "KeyHash: ${getKeyHash()}")

        btnDisqus.setOnClickListener {
            RxSocialLogin.login(PlatformType.DISQUS)
        }

        btnFacebook.setOnClickListener {
            RxSocialLogin.login(PlatformType.FACEBOOK)
        }

        btnFoursquare.setOnClickListener {
            RxSocialLogin.login(PlatformType.FOURSQUARE)
        }

        btnGithub.setOnClickListener {
            RxSocialLogin.login(PlatformType.GITHUB)
        }

        btnGoogle.setOnClickListener {
            RxSocialLogin.login(PlatformType.GOOGLE)
        }

        btnKakao.setOnClickListener {
            RxSocialLogin.login(PlatformType.KAKAO)
        }

        btnLine.setOnClickListener {
            RxSocialLogin.login(PlatformType.LINE)
        }

        btnLinkedin.setOnClickListener {
            RxSocialLogin.login(PlatformType.LINKEDIN)
        }

        btnNaver.setOnClickListener {
            RxSocialLogin.login(PlatformType.NAVER)
        }

        btnTwitch.setOnClickListener {
            RxSocialLogin.login(PlatformType.TWITCH)
        }

        btnTwitter.setOnClickListener {
            RxSocialLogin.login(PlatformType.TWITTER)
        }

        btnVK.setOnClickListener {
            RxSocialLogin.login(PlatformType.VK)
        }

        btnWordpress.setOnClickListener {
            RxSocialLogin.login(PlatformType.WORDPRESS)
        }

        btnWindows.setOnClickListener {
            RxSocialLogin.login(PlatformType.WINDOWS)
        }

        btnYahoo.setOnClickListener {
            RxSocialLogin.login(PlatformType.YAHOO)
        }

        RxSocialLogin.initialize(this)

        RxSocialLogin.result(this)
                .subscribe({
                    txtResult.text = it.toString()
                    txtResult.setTextColor(Color.BLACK)
                    txtPlatform.text = it.platform.name
                }, {
                    Log.d(MainActivity::class.java.simpleName, "onError: ${it.message}")
                    txtResult.text = "Login Failed - ${it.message}"
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
}
