package com.github.windsekirun.rxsociallogin.test

import android.annotation.SuppressLint
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

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
