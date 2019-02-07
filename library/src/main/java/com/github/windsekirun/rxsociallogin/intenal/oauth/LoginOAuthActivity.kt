package com.github.windsekirun.rxsociallogin.intenal.oauth

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.webkit.WebView
import com.github.kittinunf.fuel.httpPost
import com.github.windsekirun.rxsociallogin.R
import com.github.windsekirun.rxsociallogin.intenal.model.PlatformType
import com.github.windsekirun.rxsociallogin.intenal.utils.getCode
import com.github.windsekirun.rxsociallogin.intenal.utils.toResultObservable
import com.github.windsekirun.rxsociallogin.intenal.webview.EnhanceWebView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class LoginOAuthActivity : AppCompatActivity() {
    private lateinit var disposable: Disposable
    private lateinit var authUrl: String
    private lateinit var title: String
    private lateinit var oauthUrl: String
    private lateinit var parameters: HashMap<String, String>
    private lateinit var platformType: PlatformType
    private lateinit var authorizationValue: String
    private lateinit var webView: EnhanceWebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_oauth)
        webView = findViewById(R.id.webView)

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
                setResult(2)
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (webView.onBackPressed()) {
            super.onBackPressed()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun init() {
        if (intent == null) {
            setResult(Activity.RESULT_CANCELED)
            finish()
            return
        }

        if (intent.extras == null) {
            setResult(Activity.RESULT_CANCELED)
            finish()
            return
        }

        authUrl = intent.extras.getString(EXTRA_AUTH_URL) ?: ""
        oauthUrl = intent.extras.getString(EXTRA_OAUTH_URL) ?: ""
        platformType = intent.extras.getSerializable(EXTRA_PLATFORM) as PlatformType
        parameters = intent.extras.getSerializable(EXTRA_OAUTH_PARAMETERS) as java.util.HashMap<String, String>
        title = intent.extras.getString(EXTRA_TITLE)
        authorizationValue = intent.extras.getString(EXTRA_AUTHORIZATION_VALUE)

        webView.setWebViewHandler(object : EnhanceWebView.EnhanceWebViewHandler {
            override fun onPageFinished(view: WebView, url: String) {}

            override fun shouldOverrideUrlLoading(view: WebView, url: String, uri: Uri, scheme: String, host: String, parameters: Map<String, String>?): Boolean {
                try {
                    if (!url.contains("?code=")) return false
                    requestOAuthToken(url.getCode())
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                return false
            }
        })

        webView.enableGoBack = true
        webView.enableFormUpload(this)
        webView.url = authUrl

        supportActionBar?.let {
            it.title = this.title
            it.setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun requestOAuthToken(code: String) {
        val header: Pair<String, String>
        when (platformType) {
            PlatformType.GITHUB -> {
                parameters["code"] = code
                header = "Accept" to "application/json"
            }
            PlatformType.YAHOO -> {
                parameters["code"] = code
                header = "Authorization" to "Basic $authorizationValue"
            }
            PlatformType.DISCORD -> {
                header = "Content-Type" to "application/x-www-form-urlencoded"
            }
            else -> {
                parameters["code"] = code
                header = "Content-Type" to "application/json"
            }
        }

        val pairs = parameters.entries
                .map { it.key to it.value }

        disposable = oauthUrl.httpPost(pairs)
                .header(header)
                .toResultObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { result, error ->
                    if (error == null && result.component1() != null) {
                        val intent = Intent()
                        intent.putExtra(RESPONSE_JSON, result.component1())
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    } else {
                        setResult(2)
                        finish()
                    }
                }
    }

    companion object {
        const val RESPONSE_JSON = "5007b400-fe6c-4e65-834b-42ec5b91cd6e"

        private const val EXTRA_AUTH_URL = "2a909ae5-1d53-43d1-8cb5-91e9d2fc04f2"
        private const val EXTRA_TITLE = "7ef9679f-4832-4368-8255-094077aba67f"
        private const val EXTRA_OAUTH_URL = "15db1867-c8b8-4853-9540-c6806e1cd87f"
        private const val EXTRA_OAUTH_PARAMETERS = "ae2eb911-af09-4bcf-8c30-cd15c8583e3a"
        private const val EXTRA_PLATFORM = "bfa7d893-1a67-448d-b8d6-2e84fa0bc167"
        private const val EXTRA_AUTHORIZATION_VALUE = "5ab24c5e-fe4d-47b4-a70d-38a3442908cc"

        internal fun startOAuthActivity(activity: FragmentActivity?, requestCode: Int, platform: PlatformType,
                                        authUrl: String, title: String, oauthUrl: String,
                                        parameters: java.util.HashMap<String, String>, authorization: String = "") {
            if (activity == null) return

            val intent = Intent(activity, LoginOAuthActivity::class.java)
            intent.putExtra(EXTRA_AUTH_URL, authUrl)
            intent.putExtra(EXTRA_TITLE, title)
            intent.putExtra(EXTRA_OAUTH_URL, oauthUrl)
            intent.putExtra(EXTRA_OAUTH_PARAMETERS, parameters)
            intent.putExtra(EXTRA_PLATFORM, platform)
            intent.putExtra(EXTRA_AUTHORIZATION_VALUE, authorization)

            activity.startActivityForResult(intent, requestCode)
        }
    }
}