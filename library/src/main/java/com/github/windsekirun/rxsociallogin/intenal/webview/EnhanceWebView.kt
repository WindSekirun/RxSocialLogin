package com.github.windsekirun.rxsociallogin.intenal.webview

import android.annotation.TargetApi
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.os.Message
import android.os.Parcelable
import android.provider.MediaStore
import android.text.TextUtils
import android.util.AttributeSet
import android.webkit.*
import android.widget.FrameLayout
import com.github.windsekirun.rxsociallogin.intenal.utils.weak
import com.google.common.base.Splitter
import pyxis.uzuki.live.richutilskt.utils.createUri
import pyxis.uzuki.live.richutilskt.utils.runOnUiThread
import java.lang.ref.WeakReference
import java.util.*


/**
 * PyxisBaseApp
 * Class: EnhanceWebView
 * Created by Pyxis on 2018-02-08.
 *
 *
 * Description:
 */

class EnhanceWebView : FrameLayout {
    var mUrl: String = ""
    var mAssertUrl: String = ""

    var onShouldInterceptRequestListener: OnShouldInterceptRequestListener? = null
    var webViewHandler: EnhanceWebViewHandler? = null
    var onReceivedErrorListener: OnReceivedErrorListener? = null
    var onBackButtonListener: OnBackButtonListener? = null
    var enableGoBack: Boolean = false
    var additionalUserAgent = ""

    private var mWebView: WebView? = null
    private var uploadMessage: ValueCallback<Uri>? = null
    private var uploadMessages: ValueCallback<Array<Uri>>? = null
    private var activity: Activity? by weak(null)
    private var mContentMimeType = "*/*"
    private lateinit var mCapturedImageURI: Uri
    private var enableCamera: Boolean = false

    private val FORM_REQUEST_CODE = 72
    private val TAG = EnhanceWebView::class.java.simpleName

    /**
     * get [WebView] object which handling now
     *
     * @return [WebView]
     */
    val webView: WebView
        get() = getChildAt(0) as WebView

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    /**
     * set URL to load
     *
     * @param url String
     */
    fun setUrl(url: String) {
        mUrl = url
        loadWebView()
    }

    /**
     * set Assert Url to load
     *
     * @param assetUrl String
     */
    fun setAssertUrl(assetUrl: String) {
        mAssertUrl = assetUrl
        loadWebView()
    }

    /**
     * set Base url to load
     */
    @JvmOverloads
    fun setBaseUrl(content: String, webView: WebView? = mWebView) {
        loadWebViewByBaseUrl(webView, content)
    }

    /**
     * loading webview
     *
     * @param webView [WebView]
     */
    @JvmOverloads
    fun loadWebView(webView: WebView? = mWebView) {
        if (TextUtils.isEmpty(mUrl) && !TextUtils.isEmpty(mAssertUrl)) {
            mUrl = String.format("file:///android_asset/%s", mAssertUrl)
        }

        loadWebView(webView, mUrl)
    }

    /**
     * enable Form upload feature
     *
     * @param activity        `Activity` object, it will store by [WeakReference]
     * @param contentMimeType mimeType when request ACTION_GET_CONTENT
     */
    @JvmOverloads
    fun enableFormUpload(activity: Activity, contentMimeType: String = "*/*", enableCamera: Boolean = false) {
        this.activity = activity
        mContentMimeType = contentMimeType
        this.enableCamera = enableCamera
    }

    /**
     * loading webview with Url
     *
     * @param webView [WebView]
     * @param url     String
     */
    fun loadWebView(webView: WebView?, url: String) {
        setDefaultWebSettings(webView)
        webView?.loadUrl(url)
    }

    /**
     * loading webview with BaseUrl
     *
     * @param webView [WebView]
     * @param url     String
     */
    fun loadWebViewByBaseUrl(webView: WebView?, content: String) {
        setDefaultWebSettings(webView)
        webView?.loadDataWithBaseURL("", content,
                "text/html; charset=utf-8", "UTF-8", null)
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
            mWebView!!.loadUrl(message)
        }
    }

    /**
     * handle onBackPressed() event
     *
     *
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

            return onBackButtonListener == null || onBackButtonListener!!.onBackPressed(webView.url)
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
        if (!::mCapturedImageURI.isInitialized) return

        if (requestCode == FORM_REQUEST_CODE) {
            val result = if (data == null || resultCode != Activity.RESULT_OK) null else data.data
            if (uploadMessage != null) {
                uploadMessage!!.onReceiveValue(result)
                uploadMessage = null
            }

            if (uploadMessages != null) {
                if (enableCamera) {
                    var urlParam: Array<Uri>? = null
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        if (resultCode == Activity.RESULT_OK) {
                            urlParam = if (data == null) {
                                arrayOf(mCapturedImageURI)
                            } else {
                                WebChromeClient.FileChooserParams.parseResult(resultCode, data)
                            }
                        }

                        if (urlParam != null) {
                            uploadMessages!!.onReceiveValue(urlParam)
                        }
                    }
                } else {
                    if (data != null) {
                        var results: Array<Uri>? = null
                        val dataString = data.dataString
                        if (dataString != null) {
                            results = arrayOf(Uri.parse(dataString))
                        }
                        uploadMessages!!.onReceiveValue(results)
                    } else {
                        uploadMessages!!.onReceiveValue(null)
                    }
                }

                uploadMessages = null
            }
        }
    }

    /**
     * Enabling Chrome DevTools;
     * see https://developers.google.com/web/tools/chrome-devtools/remote-debugging/webviews?hl=ko
     *
     * @param isEnabled
     */
    fun enableWebContentsDebugging(isEnabled: Boolean) {
        if (Build.VERSION.SDK_INT >= 19) {
            WebView.setWebContentsDebuggingEnabled(isEnabled)
        }
    }

    private fun init(attrs: AttributeSet?) {
        mWebView = WebView(context)
        addView(mWebView)

        if (!TextUtils.isEmpty(mUrl) || !TextUtils.isEmpty(mAssertUrl)) {
            loadWebView()
        }
    }

    private fun showFileChooserKitkat() {
        if (activity == null) return

        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = mContentMimeType
        activity?.startActivityForResult(Intent.createChooser(intent, "Choose File"), FORM_REQUEST_CODE)
    }

    private fun showFileChooser() {
        if (activity == null) return

        if (!enableCamera) {
            showFileChooserKitkat()
            return
        }

        mCapturedImageURI = createUri(context, false, false)

        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI)

        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = mContentMimeType

        val chooserIntent = Intent.createChooser(intent, "Choose File")
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf<Parcelable>(captureIntent))

        activity?.startActivityForResult(chooserIntent, FORM_REQUEST_CODE)
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

    private fun setDefaultWebSettings(webView: WebView?) {
        val webSettings = webView!!.settings
        webSettings.databaseEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.javaScriptEnabled = true
        webSettings.setSupportZoom(false)
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        webSettings.setSupportMultipleWindows(true)

        if (!TextUtils.isEmpty(additionalUserAgent)) {
            webSettings.userAgentString = String.format("%s %s", webSettings.userAgentString, additionalUserAgent)
        }

        webView.webViewClient = WebClient()
        webView.webChromeClient = ChromeClient()
    }

    interface EnhanceWebViewHandler {
        fun shouldOverrideUrlLoading(view: WebView, url: String, uri: Uri, scheme: String?, host: String?,
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
        fun onReceiveError(receiveHttpError: Boolean, resourceRequest: WebResourceRequest?, `object`: Any?)
    }

    private inner class WebClient : WebViewClient() {

        override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
            AlertDialog.Builder(context)
                    .setMessage("There is a problem with the security certificate. Do you want to continue?")
                    .setPositiveButton("Proceed") { dialog1, which -> handler.proceed() }
                    .setNegativeButton("Cancel") { dialog1, which -> handler.cancel() }
                    .show()
        }

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            val uri = Uri.parse(url)

            if (webViewHandler != null && webViewHandler!!.shouldOverrideUrlLoading(view, url, uri, uri.scheme,
                            uri.host, getParameters(url))) {
                return true
            }

            return super.shouldOverrideUrlLoading(view, url)
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            if (webViewHandler != null) {
                webViewHandler!!.onPageFinished(view, url)
            }
        }

        override fun shouldInterceptRequest(view: WebView, url: String): WebResourceResponse? {
            return if (onShouldInterceptRequestListener != null) {
                onShouldInterceptRequestListener!!.shouldInterceptRequest(view, url)
            } else super.shouldInterceptRequest(view, url)
        }

        override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
            super.onReceivedError(view, request, error)
            if (onReceivedErrorListener != null) {
                onReceivedErrorListener!!.onReceiveError(false, request, error)
            }
        }

        override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
            super.onReceivedError(view, errorCode, description, failingUrl)
            if (onReceivedErrorListener != null) {
                onReceivedErrorListener!!.onReceiveError(false, null, null)
            }
        }

        override fun onReceivedHttpError(view: WebView, request: WebResourceRequest, errorResponse: WebResourceResponse) {
            super.onReceivedHttpError(view, request, errorResponse)
            if (onReceivedErrorListener != null) {
                onReceivedErrorListener!!.onReceiveError(true, request, errorResponse)
            }
        }
    }

    private inner class ChromeClient : WebChromeClient() {

        override fun onConsoleMessage(cm: ConsoleMessage): Boolean {
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
            showAlertDialog("", message, DialogInterface.OnClickListener { _, _ -> result.confirm() })
            return true
        }

        override fun onJsConfirm(view: WebView, url: String, message: String, result: android.webkit.JsResult): Boolean {
            showConfirmDialog("", message, DialogInterface.OnClickListener { _, _ -> result.confirm() },
                    DialogInterface.OnClickListener { _, _ -> result.cancel() })
            return true
        }

        fun openFileChooser(uploadMsg: ValueCallback<Uri>) {
            uploadMessage = uploadMsg
            showFileChooserKitkat();
        }

        fun openFileChooser(uploadMsg: ValueCallback<*>, acceptType: String) {
            uploadMessage = uploadMsg as ValueCallback<Uri>?
            showFileChooserKitkat();
        }

        fun openFileChooser(uploadMsg: ValueCallback<Uri>, acceptType: String, capture: String) {
            uploadMessage = uploadMsg
            showFileChooserKitkat();
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        override fun onShowFileChooser(webView: WebView, filePathCallback: ValueCallback<Array<Uri>>, fileChooserParams: WebChromeClient.FileChooserParams): Boolean {
            uploadMessages = filePathCallback
            showFileChooser()
            return true
        }
    }

    fun showAlertDialog(title: String, message: String, positiveListener: DialogInterface.OnClickListener?) {
        val activity = activity ?: return
        if (message.isEmpty()) return
        val builder = AlertDialog.Builder(activity)
        if (title.isNotEmpty()) builder.setTitle(title)
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, positiveListener)
                .show()
    }

    fun showConfirmDialog(title: String, message: String, positiveListener: DialogInterface.OnClickListener?,
                          negativeListener: DialogInterface.OnClickListener?) {
        val activity = activity ?: return
        if (message.isEmpty()) return
        val builder = AlertDialog.Builder(activity)
        if (title.isNotEmpty()) builder.setTitle(title)
        builder.setMessage(message)
        builder.setCancelable(false)
        builder.setPositiveButton(android.R.string.ok, positiveListener)
                .setNegativeButton(android.R.string.no, negativeListener)
                .show()
    }
}