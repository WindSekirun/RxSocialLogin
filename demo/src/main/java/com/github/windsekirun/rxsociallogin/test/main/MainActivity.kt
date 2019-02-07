package com.github.windsekirun.rxsociallogin.test.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.github.windsekirun.rxsociallogin.test.R
import com.github.windsekirun.rxsociallogin.test.databinding.MainActivityBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.main_activity)

        val fragment = MainFragment()
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment, "main-fragment").commit()
    }
}
