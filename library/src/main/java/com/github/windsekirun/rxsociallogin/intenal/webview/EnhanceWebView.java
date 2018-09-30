package com.github.windsekirun.rxsociallogin.intenal.webview;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.google.common.base.Splitter;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pyxis.uzuki.live.richutilskt.utils.RichUtils;

/**
 * PyxisBaseApp
 * Class: EnhanceWebView
 * Created by Pyxis on 2018-02-08.
 * <p>
 * Description:
 */

public class EnhanceWebView extends FrameLayout {
    public String mUrl;
    public String mAssertUrl;
    private WebView mWebView;
    private EnhanceWebViewHandler mWebViewHandler;
    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mUploadMessages = null;
    private WeakReference<Activity> mActivityWeakReference = null;
    private OnShouldInterceptRequestListener mOnShouldInterceptRequestListener;
    private OnReceivedErrorListener mOnReceivedErrorListener;
    private OnBackButtonListener mOnBackButtonListener;
    private int FORM_REQUEST_CODE = 72;
    private boolean mEnableGoBack;
    private String mContentMimeType = "*/*";

    public EnhanceWebView(@NonNull Context context) {
        super(context);
        init(null);
    }

    public EnhanceWebView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    /**
     * get {@link WebView} object which handling now
     *
     * @return {@link WebView}
     */
    public WebView getWebView() {
        return (WebView) getChildAt(0);
    }

    /**
     * listener when shouldInterceptRequest in WebViewClient
     *
     * @param onShouldInterceptRequestListener
     */
    public void setOnShouldInterceptRequestListener(OnShouldInterceptRequestListener onShouldInterceptRequestListener) {
        mOnShouldInterceptRequestListener = onShouldInterceptRequestListener;
    }

    /**
     * listener when invoke EnhanceWebView.onBackPressed()
     *
     * @param onBackButtonListener
     */
    public void setOnBackButtonListener(OnBackButtonListener onBackButtonListener) {
        mOnBackButtonListener = onBackButtonListener;
    }

    /**
     * listener when onReceivedError or onReceivedHttpError called in WebViewClient
     *
     * @param onReceivedErrorListener
     */
    public void setOnReceivedErrorListener(OnReceivedErrorListener onReceivedErrorListener) {
        mOnReceivedErrorListener = onReceivedErrorListener;
    }

    /**
     * set {@link EnhanceWebViewHandler} to handle event of WebView
     * <p>
     * implemented in ViewModel is recommend.
     *
     * @param webViewHandler {@link EnhanceWebViewHandler} object
     */
    public void setWebViewHandler(EnhanceWebViewHandler webViewHandler) {
        mWebViewHandler = webViewHandler;
    }

    /**
     * set flag of enable webView.goBack()
     *
     * @param enableGoBack
     */
    public void setEnableGoBack(boolean enableGoBack) {
        mEnableGoBack = enableGoBack;
    }

    /**
     * set URL to load
     *
     * @param url String
     */
    public void setUrl(String url) {
        mUrl = url;
        loadWebView();
    }

    /**
     * set Assert Url to load
     *
     * @param assetUrl String
     */
    public void setAssertUrl(String assetUrl) {
        mAssertUrl = assetUrl;
        loadWebView();
    }

    /**
     * loading webview
     */
    public void loadWebView() {
        loadWebView(mWebView);
    }

    /**
     * loading webview
     *
     * @param webView {@link WebView}
     */
    public void loadWebView(WebView webView) {
        if (TextUtils.isEmpty(mUrl) && !TextUtils.isEmpty(mAssertUrl)) {
            mUrl = String.format("file:///android_asset/%s", mAssertUrl);
        }

        loadWebView(webView, mUrl);
    }

    /**
     * enable Form upload feature
     *
     * @param activity {@code Activity} object, it will store by {@link WeakReference}
     */
    public void enableFormUpload(Activity activity) {
        enableFormUpload(activity, "*/*");
    }

    /**
     * enable Form upload feature
     *
     * @param activity        {@code Activity} object, it will store by {@link WeakReference}
     * @param contentMimeType mimeType when request ACTION_GET_CONTENT
     */
    public void enableFormUpload(Activity activity, String contentMimeType) {
        mActivityWeakReference = new WeakReference<>(activity);
        mContentMimeType = contentMimeType;
    }

    /**
     * loading webview with Url
     *
     * @param webView {@link WebView}
     * @param url     String
     */
    public void loadWebView(WebView webView, String url) {
        WebSettings webSettings = webView.getSettings();
        webSettings.setDatabaseEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setSupportMultipleWindows(true);
        webView.setWebViewClient(new WebClient());
        webView.setWebChromeClient(new ChromeClient());

        webView.loadUrl(url);
    }

    /**
     * Calling Javascript methods
     *
     * @param methodName
     * @param args
     */
    public void callScriptMethod(String methodName, Object... args) {
        RichUtils.runOnUiThread(() -> {
            String format = methodName;
            if (!format.contains("javascript:")) {
                format = String.format("javascript:%s", format);
            }

            List<Object> objects = Arrays.asList(args);
            if (!objects.isEmpty()) {
                format += "(";
                for (int i = 0, icnt = objects.size(); i < icnt; i++) {
                    format += "\'%s\'";
                    if (i != icnt - 1) {
                        format += ", ";
                    }
                }

                format += ")";
            } else {
                format += "()";
            }

            String message = String.format(format, args);
            mWebView.loadUrl(message);
        });
    }

    /**
     * handle onBackPressed() event
     * <p>
     * when this methods return true, call super.onBackPressed()
     *
     * @return flag which activity can be closed
     */
    public boolean onBackPressed() {
        if (getChildCount() <= 1) {
            WebView webView = (WebView) getChildAt(0);

            if (webView.canGoBack() && mEnableGoBack) {
                webView.goBack();
                return false;
            }

            return mOnBackButtonListener == null || mOnBackButtonListener.onBackPressed(webView.getUrl());
        } else {
            WebView webView = (WebView) getChildAt(getChildCount() - 1);
            if (webView.canGoBack()) {
                webView.goBack();
            } else {
                removeView(webView);
            }
            return false;
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
    public void onFormResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FORM_REQUEST_CODE) {
            Uri result = data == null || resultCode != Activity.RESULT_OK ? null : data.getData();
            if (mUploadMessage != null) {
                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
            }

            if (mUploadMessages != null) {
                if (data != null) {
                    Uri[] results = null;
                    String dataString = data.getDataString();

                    if (dataString != null) {
                        results = new Uri[]{Uri.parse(dataString)};
                    }

                    mUploadMessages.onReceiveValue(results);
                } else {
                    mUploadMessages.onReceiveValue(null);
                }
                mUploadMessages = null;
            }
        }
    }

    private void init(AttributeSet attrs) {
        mWebView = new WebView(getContext());
        addView(mWebView);

        if (!TextUtils.isEmpty(mUrl) || !TextUtils.isEmpty(mAssertUrl)) {
            loadWebView();
        }
    }

    private void showFileChooser() {
        if (mActivityWeakReference.get() == null) return;

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(mContentMimeType);
        mActivityWeakReference.get().startActivityForResult(Intent.createChooser(intent, "Choose File"), FORM_REQUEST_CODE);
    }

    private Map<String, String> getParameters(String uri) {
        try {
            Map<String, String> map = new HashMap<>();
            if (!uri.contains("?")) return map;
            String query = uri.split("\\?")[1];
            map = Splitter.on('&').trimResults().omitEmptyStrings().withKeyValueSeparator("=").split(query);
            return map;
        } catch (Exception e) {
            // sometime we can't catch parameters cause url string is not valid (ex, pay within Samsung Pay)
            return null;
        }
    }

    public interface EnhanceWebViewHandler {
        boolean shouldOverrideUrlLoading(WebView view, String url, Uri uri, String scheme, String host,
                                         @Nullable Map<String, String> parameters);

        void onPageFinished(WebView view, String url);
    }

    public interface OnShouldInterceptRequestListener {
        WebResourceResponse shouldInterceptRequest(WebView view, String url);
    }

    public interface OnBackButtonListener {
        boolean onBackPressed(String url);
    }

    public interface OnReceivedErrorListener {
        void onReceiveError(boolean receiveHttpError, WebResourceRequest resourceRequest, Object object);
    }

    private class WebClient extends WebViewClient {

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            new AlertDialog.Builder(getContext())
                    .setMessage("There is a problem with the security certificate. Do you want to continue?")
                    .setPositiveButton("Proceed", (dialog1, which) -> handler.proceed())
                    .setNegativeButton("Cancel", ((dialog1, which) -> handler.cancel()))
                    .show();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Uri uri = Uri.parse(url);

            if (mWebViewHandler != null && mWebViewHandler.shouldOverrideUrlLoading(view, url, uri, uri.getScheme(),
                    uri.getHost(), getParameters(url))) {
                return true;
            }

            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (mWebViewHandler != null) {
                mWebViewHandler.onPageFinished(view, url);
            }
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            if (mOnShouldInterceptRequestListener != null) {
                return mOnShouldInterceptRequestListener.shouldInterceptRequest(view, url);
            }

            return super.shouldInterceptRequest(view, url);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            if (mOnReceivedErrorListener != null) {
                mOnReceivedErrorListener.onReceiveError(false, request, error);
            }
        }

        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            super.onReceivedHttpError(view, request, errorResponse);
            if (mOnReceivedErrorListener != null) {
                mOnReceivedErrorListener.onReceiveError(true, request, errorResponse);
            }
        }
    }

    private class ChromeClient extends WebChromeClient {

        @Override
        public boolean onConsoleMessage(ConsoleMessage cm) {
            Log.d(EnhanceWebView.class.getSimpleName(), String.format("%s @ %d: %s", cm.message(), cm.lineNumber(), cm.sourceId()));
            return true;
        }

        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            WebView newWebView = new WebView(view.getContext());
            loadWebView(newWebView);
            addView(newWebView);

            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(newWebView);
            resultMsg.sendToTarget();
            return true;
        }

        @Override
        public void onCloseWindow(WebView window) {
            super.onCloseWindow(window);
            removeView(window);
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, final android.webkit.JsResult result) {
            new AlertDialog.Builder(mActivityWeakReference.get())
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> result.confirm())
                    .show();
            return true;
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, final android.webkit.JsResult result) {
            new AlertDialog.Builder(mActivityWeakReference.get())
                    .setMessage(message)
                    .setNegativeButton(android.R.string.no, (dialog, which) -> result.cancel())
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> result.confirm())
                    .show();
            return true;
        }

        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            mUploadMessage = uploadMsg;
            showFileChooser();
        }

        public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
            mUploadMessage = uploadMsg;
            showFileChooser();
        }

        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            mUploadMessage = uploadMsg;
            showFileChooser();

        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            mUploadMessages = filePathCallback;
            showFileChooser();
            return true;
        }
    }
}
