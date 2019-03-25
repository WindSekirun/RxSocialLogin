package com.github.windsekirun.rxsociallogin.test.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableInt
import com.github.windsekirun.rxsociallogin.RxSocialLogin
import com.github.windsekirun.rxsociallogin.intenal.model.PlatformType
import com.github.windsekirun.rxsociallogin.test.R
import com.github.windsekirun.rxsociallogin.test.databinding.MainActivityBinding


class MainActivity : AppCompatActivity() {
    val platformCount = ObservableInt(PlatformType.values().size)
    private lateinit var binding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.main_activity)
        binding.activity = this

        val fragment = MainFragment()
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment, "main-fragment").commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        RxSocialLogin.activityResult(requestCode, resultCode, data)
    }
}
