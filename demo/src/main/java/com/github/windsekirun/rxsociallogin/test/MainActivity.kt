package com.github.windsekirun.rxsociallogin.test

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.github.windsekirun.rxsociallogin.RxSocialLogin
import com.github.windsekirun.rxsociallogin.facebook.FacebookLogin
import com.github.windsekirun.rxsociallogin.kakao.KakaoLogin
import com.github.windsekirun.rxsociallogin.model.LoginResultItem
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.activity_main.*
import pyxis.uzuki.live.richutilskt.utils.getKeyHash

class MainActivity : AppCompatActivity() {
    private lateinit var kakaoLogin: KakaoLogin
    private lateinit var facebookLogin: FacebookLogin
    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(MainActivity::class.java.simpleName, "KeyHash: ${getKeyHash()}," + " apiKey: ${getString(R.string.kakao_api_key)}")

        kakaoLogin = KakaoLogin(this)
        facebookLogin = FacebookLogin(this)

        val consumer = Consumer<LoginResultItem> {
            val typeStr = it.toString()
            Log.d(MainActivity::class.java.simpleName, "onNext: typeStr: $typeStr")
            txtResult.text = typeStr
        }

        val error = Consumer<Throwable> {
            Log.d(MainActivity::class.java.simpleName, "onError: ${it.message}")
        }

        // warning, do this in MainThread.
        val kakaoDisposable = RxSocialLogin.kakao(kakaoLogin)
                .subscribe(consumer, error)

        val facebookDisposable = RxSocialLogin.facebook(facebookLogin)
                .subscribe(consumer, error)

        compositeDisposable.add(kakaoDisposable)
        compositeDisposable.add(facebookDisposable)

        btnKakao.setOnClickListener {
            if (::kakaoLogin.isInitialized) {
                kakaoLogin.onLogin()
            }
        }

        btnFacebook.setOnClickListener {
            if (::facebookLogin.isInitialized) {
                facebookLogin.onLogin()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (::kakaoLogin.isInitialized) {
            kakaoLogin.onActivityResult(requestCode, resultCode, data)
        }

        if (::facebookLogin.isInitialized) {
            facebookLogin.onActivityResult(requestCode, resultCode, data)
        }
    }
}
