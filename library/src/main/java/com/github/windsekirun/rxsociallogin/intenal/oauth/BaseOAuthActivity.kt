package com.github.windsekirun.rxsociallogin.intenal.oauth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.github.windsekirun.rxsociallogin.R
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.security.SecureRandom

/**
 * RxSocialLogin
 * Class: BaseOAuthActivity
 * Created by pyxis on 18. 8. 1.
 *
 *
 * Description:
 */
abstract class BaseOAuthActivity : AppCompatActivity() {
    lateinit var disposable: Disposable

    private val groupAlphanumeric = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
    private var secureRandom = SecureRandom()

    abstract fun init()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_oauth)

        init()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::disposable.isInitialized && !disposable.isDisposed) {
            disposable.dispose()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finishActivity()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun Single<String>.requestAccessToken(): Disposable {
        return this.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    finishActivity(it)
                }, {
                    finishActivity()
                })
    }

    fun setToolbar(title: String) {
        supportActionBar?.let {
            it.title = title
            it.setDisplayHomeAsUpEnabled(true)
        }
    }

    fun randomString(len: Int): String {
        val sb = StringBuilder(len)
        for (i in 0 until len) {
            sb.append(groupAlphanumeric[secureRandom.nextInt(groupAlphanumeric.length)])
        }
        return sb.toString()
    }

    private fun finishActivity() {
        setResult(2)
        finish()
    }

    private fun finishActivity(jsonStr: String) {
        val intent = Intent()
        intent.putExtra(RESPONSE_JSON, jsonStr)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    companion object {
        const val RESPONSE_JSON = "5007b400-fe6c-4e65-834b-42ec5b91cd6e"
    }
}