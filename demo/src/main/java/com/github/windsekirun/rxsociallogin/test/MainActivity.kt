package com.github.windsekirun.rxsociallogin.test

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.github.windsekirun.rxsociallogin.RxSocialLogin
import com.github.windsekirun.rxsociallogin.facebook.FacebookLogin
import com.github.windsekirun.rxsociallogin.kakao.KakaoLogin
import com.github.windsekirun.rxsociallogin.line.LineLogin
import com.github.windsekirun.rxsociallogin.model.LoginResultItem
import com.github.windsekirun.rxsociallogin.naver.NaverLogin
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.activity_main.*
import pyxis.uzuki.live.richutilskt.utils.getKeyHash

class MainActivity : AppCompatActivity() {
    private lateinit var kakaoLogin: KakaoLogin
    private lateinit var facebookLogin: FacebookLogin
    private lateinit var naverLogin: NaverLogin
    private lateinit var lineLogin: LineLogin
    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(MainActivity::class.java.simpleName, "KeyHash: ${getKeyHash()}")

        kakaoLogin = KakaoLogin(this)
        facebookLogin = FacebookLogin(this)
        naverLogin = NaverLogin(this)
        lineLogin = LineLogin(this)

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

        val naverDisposable = RxSocialLogin.naver(naverLogin)
                .subscribe(consumer, error)

        val lineDisposable = RxSocialLogin.line(lineLogin)
                .subscribe(consumer, error)

        compositeDisposable.add(kakaoDisposable)
        compositeDisposable.add(facebookDisposable)
        compositeDisposable.add(naverDisposable)
        compositeDisposable.add(lineDisposable)

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

        btnTwiitter.setOnClickListener {

        }

        btnGoogle.setOnClickListener {

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
    }
}
