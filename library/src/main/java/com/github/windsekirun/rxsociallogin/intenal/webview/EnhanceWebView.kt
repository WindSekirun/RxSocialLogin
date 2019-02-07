package com.github.windsekirun.rxsociallogin.intenal.webview

import android.annotation.TargetApi
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.os.Message
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.SslErrorHandler
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import com.github.windsekirun.rxsociallogin.intenal.utils.weak

import com.google.common.base.Splitter
import pyxis.uzuki.live.richutilskt.utils.runOnUiThread

import java.lang.ref.WeakReference
import java.util.Arrays
import java.util.HashMap

class EnhanceWebView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {
    init {
        init(attrs)
    }

    var url: String = ""
        set(value) {
            field = value
            loadWebView()
        }
    var assertUrl: String = ""
        set(value) {
            field = value
            loadWebView()
        }

    var enableGoBack: Boolean = false

    private var internalWebView: WebView? = null
    private var webViewHandler: EnhanceWebViewHandler? = null
    private var uploadMessage: ValueCallback<Uri>? = null
    private var uploadMessages: ValueCallback<Array<Uri>>? = null
    private var activityWeakReference: Activity? by weak(null)
    private var shouldInterceptRequestListener: OnShouldInterceptRequestListener? = null
    private var receivedErrorListener: OnReceivedErrorListener? = null
    private var backButtonListener: OnBackButtonListener? = null
    private var contentMimeType = "*/*"
    private val FORM_REQUEST_CODE = 72

    /**
     * get [WebView] object which handling now
     *
     * @return [WebView]
     */
    val webView: WebView
        get() = getChildAt(0) as WebView

    /**
     * listener when shouldInterceptRequest in WebViewClient
     *
     * @param onShouldInterceptRequestListener
     */
    fun setOnShouldInterceptRequestListener(onShouldInterceptRequestListener: OnShouldInterceptRequestListener) {
        shouldInterceptRequestListener = onShouldInterceptRequestListener
    }

    /**
     * listener when invoke EnhanceWebView.onBackPressed()
     *
     * @param onBackButtonListener
     */
    fun setOnBackButtonListener(onBackButtonListener: OnBackButtonListener) {
        backButtonListener = onBackButtonListener
    }

    /**
     * listener when onReceivedError or onReceivedHttpError called in WebViewClient
     *
     * @param onReceivedErrorListener
     */
    fun setOnReceivedErrorListener(onReceivedErrorListener: OnReceivedErrorListener) {
        receivedErrorListener = onReceivedErrorListener
    }

    /**
     * set [EnhanceWebViewHandler] to handle event of WebView
     * implemented in ViewModel is recommend.
     *
     * @param webViewHandler [EnhanceWebViewHandler] object
     */
    fun setWebViewHandler(webViewHandler: EnhanceWebViewHandler) {
        this.webViewHandler = webViewHandler
    }

    /**
     * loading webview
     *
     * @param webView [WebView]
     */
    @JvmOverloads
    fun loadWebView(webView: WebView? = internalWebView) {
        if (TextUtils.isEmpty(url) && !TextUtils.isEmpty(assertUrl)) {
            url = String.format("file:///android_asset/%s", assertUrl)
        }

        loadWebView(webView, url)
    }

    /**
     * enable Form upload feature
     *
     * @param activity        `Activity` object, it will store by [WeakReference]
     * @param contentMimeType mimeType when request ACTION_GET_CONTENT
     */
    @JvmOverloads
    fun enableFormUpload(activity: Activity, contentMimeType: String = "*/*") {
        activityWeakReference = activity
        this.contentMimeType = contentMimeType
    }

    /**
     * loading webview with Url
     *
     * @param webView [WebView]
     * @param url     String
     */
    fun loadWebView(webView: WebView?, url: String) {
        val webSettings = webView?.settings ?: return
        webSettings.databaseEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.javaScriptEnabled = true
        webSettings.setSupportZoom(false)
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        webSettings.setSupportMultipleWindows(true)
        webView.webViewClient = WebClient()
        webView.webChromeClient = ChromeClient()

        webView.loadUrl(url)
    }

    /**
     * Calling Javascript methods
     *
     * @param methodName
     * @param args
     */
    fun callScriptMethod(methodName: String, vararg args: Any) {
        runOnUiThread {
            var format = methodName
            if (!format.contains("javascript:")) {
                format = String.format("javascript:%s", format)
            }

            val objects = Arrays.asList(*args)
            if (!objects.isEmpty()) {
                format += "("
                var i = 0
                val icnt = objects.size
                while (i < icnt) {
                    format += "\'%s\'"
                    if (i != icnt - 1) {
                        format += ", "
                    }
                    i++
                }

                format += ")"
            } else {
                format += "()"
            }

            val message = String.format(format, *args)
            internalWebView?.loadUrl(message)
        }
    }

    /**
     * handle onBackPressed() event
     * when this methods return true, call super.onBackPressed()
     *
     * @return flag which activity can be closed
     */
    fun onBackPressed(): Boolean {
        if (childCount <= 1) {
            val webView = getChildAt(0) as WebView

            if (webView.canGoBack() && enableGoBack) {
                webView.goBack()
                return false
            }

            return backButtonListener == null || backButtonListener?.onBackPressed(webView.url) ?: true
        } else {
            val webView = getChildAt(childCount - 1) as WebView
            if (webView.canGoBack()) {
                webView.goBack()
            } else {
                removeView(webView)
            }
            return false
        }
    }

    /**
     * handle onActivityResult() event when enable Form Upload feature by enableFormUpload(Activity)
     * just pass requestCode, resultCode, data into this methods from onActivityResult()
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    fun onFormResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == FORM_REQUEST_CODE) {
            val result = if (data == null || resultCode != Activity.RESULT_OK) null else data.data
            if (uploadMessage != null) {
                uploadMessage?.onReceiveValue(result)
                uploadMessage = null
            }

            if (uploadMessages != null) {
                if (data != null) {
                    var results: Array<Uri>? = null
                    val dataString = data.dataString

                    if (dataString != null) {
                        results = arrayOf(Uri.parse(dataString))
                    }

                    uploadMessages?.onReceiveValue(results)
                } else {
                    uploadMessages?.onReceiveValue(null)
                }
                uploadMessages = null
            }
        }
    }

    private fun init(attrs: AttributeSet?) {
        internalWebView = WebView(context)
        addView(internalWebView)

        if (!TextUtils.isEmpty(url) || !TextUtils.isEmpty(assertUrl)) {
            loadWebView()
        }
    }

    private fun showFileChooser() {
        if (activityWeakReference == null) return

        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = contentMimeType
        activityWeakReference?.startActivityForResult(Intent.createChooser(intent, "Choose File"), FORM_REQUEST_CODE)
    }

    private fun getParameters(uri: String): Map<String, String>? {
        try {
            var map: Map<String, String> = HashMap()
            if (!uri.contains("?")) return map
            val query = uri.split("\\?".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
            map = Splitter.on('&').trimResults().omitEmptyStrings().withKeyValueSeparator("=").split(query)
            return map
        } catch (e: Exception) {
            // sometime we can't catch parameters cause url string is not valid (ex, pay within Samsung Pay)
            return null
        }
    }

    interface EnhanceWebViewHandler {
        fun shouldOverrideUrlLoading(view: WebView, url: String, uri: Uri, scheme: String, host: String,
                                     parameters: Map<String, String>?): Boolean

        fun onPageFinished(view: WebView, url: String)
    }

    interface OnShouldInterceptRequestListener {
        fun shouldInterceptRequest(view: WebView, url: String): WebResourceResponse
    }

    interface OnBackButtonListener {
        fun onBackPressed(url: String): Boolean
    }

    interface OnReceivedErrorListener {
        fun onReceiveError(receiveHttpError: Boolean, resourceRequest: WebResourceRequest, `object`: Any)
    }

    private inner class WebClient : WebViewClient() {

        override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
            AlertDialog.Builder(context)
                    .setMessage("There is a problem with the security certificate. Do you want to continue?")
                    .setPositiveButton("Proceed") { _, _ -> handler.proceed() }
                    .setNegativeButton("Cancel") { _, _ -> handler.cancel() }
                    .show()
        }

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            val uri = Uri.parse(url)

            return webViewHandler != null && webViewHandler?.shouldOverrideUrlLoading(view, url, uri, uri.scheme,
                    uri.host, getParameters(url)) ?: false
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            if (webViewHandler != null) {
                webViewHandler?.onPageFinished(view, url)
            }
        }

        override fun shouldInterceptRequest(view: WebView, url: String): WebResourceResponse? {
            return if (shouldInterceptRequestListener != null) {
                shouldInterceptRequestListener?.shouldInterceptRequest(view, url)
            } else super.shouldInterceptRequest(view, url)

        }

        override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
            super.onReceivedError(view, request, error)
            if (receivedErrorListener != null) {
                receivedErrorListener?.onReceiveError(false, request, error)
            }
        }

        override fun onReceivedHttpError(view: WebView, request: WebResourceRequest, errorResponse: WebResourceResponse) {
            super.onReceivedHttpError(view, request, errorResponse)
            if (receivedErrorListener != null) {
                receivedErrorListener?.onReceiveError(true, request, errorResponse)
            }
        }
    }

    private inner class ChromeClient : WebChromeClient() {

        override fun onConsoleMessage(cm: ConsoleMessage): Boolean {
            Log.d(EnhanceWebView::class.java.simpleName, String.format("%s @ %d: %s", cm.message(), cm.lineNumber(), cm.sourceId()))
            return true
        }

        override fun onCreateWindow(view: WebView, isDialog: Boolean, isUserGesture: Boolean, resultMsg: Message): Boolean {
            val newWebView = WebView(view.context)
            loadWebView(newWebView)
            addView(newWebView)

            val transport = resultMsg.obj as WebView.WebViewTransport
            transport.webView = newWebView
            resultMsg.sendToTarget()
            return true
        }

        override fun onCloseWindow(window: WebView) {
            super.onCloseWindow(window)
            removeView(window)
        }

        override fun onJsAlert(view: WebView, url: String, message: String, result: android.webkit.JsResult): Boolean {
            if (activityWeakReference == null) return true
            AlertDialog.Builder(activityWeakReference)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok) { _, _ -> result.confirm() }
                    .show()
            return true
        }

        override fun onJsConfirm(view: WebView, url: String, message: String, result: android.webkit.JsResult): Boolean {
            if (activityWeakReference == null) return true

            AlertDialog.Builder(activityWeakReference)
                    .setMessage(message)
                    .setNegativeButton(android.R.string.no) { _, _ -> result.cancel() }
                    .setPositiveButton(android.R.string.ok) { _, _ -> result.confirm() }
                    .show()
            return true
        }

        fun openFileChooser(uploadMsg: ValueCallback<Uri>) {
            uploadMessage = uploadMsg
            showFileChooser()
        }

        fun openFileChooser(uploadMsg: ValueCallback<Uri>, acceptType: String) {
            uploadMessage = uploadMsg
            showFileChooser()
        }

        fun openFileChooser(uploadMsg: ValueCallback<Uri>, acceptType: String, capture: String) {
            uploadMessage = uploadMsg
            showFileChooser()
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        override fun onShowFileChooser(webView: WebView, filePathCallback: ValueCallback<Array<Uri>>, fileChooserParams: WebChromeClient.FileChooserParams): Boolean {
            uploadMessages = filePathCallback
            showFileChooser()
            return true
        }
    }
}