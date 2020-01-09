package com.github.windsekirun.rxsociallogin.apple

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.AttributeSet
import android.widget.ImageButton
import androidx.annotation.IntRange
import com.github.kittinunf.fuel.core.*
import com.github.kittinunf.fuel.core.deserializers.ByteArrayDeserializer
import com.github.kittinunf.fuel.httpDownload
import com.github.kittinunf.fuel.httpGet
import com.github.windsekirun.rxsociallogin.R
import com.github.windsekirun.rxsociallogin.RxSocialLogin
import com.github.windsekirun.rxsociallogin.intenal.model.PlatformType
import okhttp3.HttpUrl
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.lang.ref.WeakReference
import java.security.MessageDigest
import java.util.*

class AppleLoginButton : ImageButton {
    @IntRange(from = QUERY_WIDTH_MIN.toLong(), to = QUERY_WIDTH_MAX.toLong())
    var imageWidth: Int = QUERY_WIDTH_DEFAULT

    @IntRange(from = QUERY_HEIGHT_MIN.toLong(), to = QUERY_HEIGHT_MAX.toLong())
    var imageHeight: Int = QUERY_HEIGHT_DEFAULT

    var imageBackgroundColor: BackgroundColor = QUERY_COLOR_DEFAULT

    @IntRange(from = QUERY_BORDER_RADIUS_MIN.toLong(), to = QUERY_BORDER_RADIUS_MAX.toLong())
    var imageBorderRadius: Int = QUERY_BORDER_RADIUS_DEFAULT

    @IntRange(from = QUERY_SCALE_MIN.toLong(), to = QUERY_SCALE_MAX.toLong())
    var imageScale: Int = QUERY_SCALE_DEFAULT

    var buttonType: ButtonType = QUERY_TYPE_DEFAULT

    var hasBorder: Boolean = QUERY_BORDER_DETAULT

    var locale: ButtonLocale = provideDefaultLocale()

    constructor(context: Context?) :
            this(context, null, 0)

    constructor(context: Context?, attrs: AttributeSet?) :
            this(context, attrs, 0)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {
        if (context == null) return
        context.theme.obtainStyledAttributes(attrs, R.styleable.AppleLoginButton, defStyleAttr, 0)
            .also {
                this.imageWidth =
                    it.getInt(R.styleable.AppleLoginButton_imageWidth, QUERY_WIDTH_DEFAULT)
                this.imageHeight =
                    it.getInt(R.styleable.AppleLoginButton_imageHeight, QUERY_HEIGHT_DEFAULT)
                this.imageBackgroundColor =
                    it.getInt(R.styleable.AppleLoginButton_imageBackgroundColor, 0).run {
                        BackgroundColor.values()[this]
                    }
                this.imageBorderRadius = it.getInt(
                    R.styleable.AppleLoginButton_imageBorderRadius,
                    QUERY_BORDER_RADIUS_DEFAULT
                )
                this.imageScale =
                    it.getInt(R.styleable.AppleLoginButton_imageScale, QUERY_SCALE_DEFAULT)
                this.hasBorder =
                    it.getBoolean(R.styleable.AppleLoginButton_hasBorder, QUERY_BORDER_DETAULT)
                this.buttonType = it.getInt(R.styleable.AppleLoginButton_buttonType, 0).run {
                    ButtonType.values()[this]
                }
            }.recycle()
    }


    init {
        setOnClickListener {
            signInWithApple()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        this.loadImage()
    }

    private fun loadImage() {
        ImageDownloadTask(this)
            .execute(provideUrl())
    }

    private class ImageDownloadTask(button: AppleLoginButton) : AsyncTask<String, Unit, Bitmap?>() {
        private val mButton: WeakReference<AppleLoginButton> = WeakReference(button)

        override fun onPreExecute() {
            super.onPreExecute()
            val button = mButton.get() ?: return
            button.adjustViewBounds = true
            button.scaleType = ScaleType.FIT_XY
        }

        override fun doInBackground(vararg p0: String?): Bitmap? {
            val requestUrl = p0.firstOrNull() ?: return null
            return getCache(requestUrl) ?: getNetwork(requestUrl)
        }

        private fun getNetwork(url: String): Bitmap? {
            val (_, response, result) = url.httpGet()
                .response(ByteArrayDeserializer())
            return if (response.isSuccessful) {
                val byteArray = result.get()
                this.updateCache(url, byteArray)
                BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            } else null
        }

        private fun getCache(url: String): Bitmap? {
            val button = mButton.get() ?: return null
            val cacheFile = File(button.context.cacheDir, url.md5)
            return if (cacheFile.exists()) FileInputStream(cacheFile).use {
                it.readBytes()
            }.run {
                BitmapFactory.decodeByteArray(this, 0, this.size)
            }
            else null
        }

        private fun updateCache(url: String, byteArray: ByteArray) {
            val button = mButton.get() ?: return
            val cacheFile = File(button.context.cacheDir, url.md5)
            FileOutputStream(cacheFile).use {
                it.write(byteArray)
            }
        }

        override fun onPostExecute(result: Bitmap?) {
            super.onPostExecute(result)
            val button = mButton.get() ?: return
            val result = result ?: return
            button.setImageBitmap(result)
        }

        val String.md5
            get() =
                this.toByteArray().run {
                    String(
                        MessageDigest.getInstance("MD5")
                            .digest(this)
                    )
                }

        companion object {
            private const val CACHE_PREFIX = "AppleLogin"
        }
    }

    private fun provideDefaultLocale(): ButtonLocale {
        val defaultLocale = Locale.getDefault().toString()
        return try {
            ButtonLocale.valueOf(defaultLocale)
        } catch (e: Exception) {
            e.printStackTrace()
            ButtonLocale.en_US
        }
    }

    private fun provideUrl(): String {
        return HttpUrl.parse(APPLE_CDN)!!
            .newBuilder()
            .addQueryParameter(
                QUERY_HEIGHT,
                imageHeight.clamp(QUERY_HEIGHT_MIN, QUERY_HEIGHT_MAX).toString()
            )
            .addQueryParameter(
                QUERY_WIDTH,
                imageWidth.clamp(QUERY_WIDTH_MIN, QUERY_WIDTH_MAX).toString()
            )
            .addQueryParameter(QUERY_COLOR, imageBackgroundColor.name.toLowerCase(Locale.ENGLISH))
            .addQueryParameter(QUERY_BORDER, hasBorder.toString())
            .addQueryParameter(
                QUERY_TYPE,
                buttonType.name.toLowerCase(Locale.ENGLISH).replace("_", "-")
            )
            .addQueryParameter(
                QUERY_BORDER_RADIUS,
                imageBorderRadius.clamp(QUERY_BORDER_RADIUS_MIN, QUERY_BORDER_RADIUS_MAX).toString()
            )
            .addQueryParameter(
                QUERY_SCALE,
                imageScale.clamp(QUERY_SCALE_MIN, QUERY_SCALE_MAX).toString()
            )
            .addQueryParameter(QUERY_LOCALE, locale.name)
            .build()
            .url()
            .toString()
    }

    fun signInWithApple() {
        RxSocialLogin.login(PlatformType.APPLE)
    }

    enum class BackgroundColor {
        BLACK, WHITE
    }

    enum class ButtonType {
        SIGN_IN, CONTINUE
    }

    enum class ButtonLocale {
        ar_SA, ca_ES, cs_CZ, da_DK, de_DE, el_GR, en_GB, en_US, es_ES,
        es_MX, fi_FI, fr_CA, fr_FR, hr_HR, hu_HU, id_ID, it_IT, iw_IL,
        ja_JP, ko_KR, ms_MY, nl_NL, no_NO, pl_PL, pt_BR, pt_PT, ro_RO,
        ru_RU, sk_SK, sv_SE, th_TH, tr_TR, uk_UA,
        vi_VI, zh_CN, zh_HK, zh_TW
    }

    fun Int.clamp(min: Int, max: Int): Int {
        return Math.min(Math.max(this, min), max)
    }

    companion object {
        private const val APPLE_CDN = "https://appleid.cdn-apple.com/appleid/button"

        /** The height of the button image.
         * The minimum and maximum values are 30 and 64, respectively, and the default value is 30. **/
        private const val QUERY_HEIGHT = "height"
        private const val QUERY_HEIGHT_MIN = 30
        private const val QUERY_HEIGHT_DEFAULT = QUERY_HEIGHT_MIN
        private const val QUERY_HEIGHT_MAX = 64

        /** The width of the button image.
         *  The minimum and maximum values are 130 and 375, respectively, and the default value is 140. **/
        private const val QUERY_WIDTH = "width"
        private const val QUERY_WIDTH_MIN = 130
        private const val QUERY_WIDTH_DEFAULT = 140
        private const val QUERY_WIDTH_MAX = 375

        /** The background color for the button image.
         * The possible values are white and black (the default). **/
        private const val QUERY_COLOR = "color"
        private val QUERY_COLOR_DEFAULT = BackgroundColor.BLACK

        /** A Boolean value that determines whether the button image has a border.
         * The default value is false. **/
        private const val QUERY_BORDER = "border"
        private const val QUERY_BORDER_DETAULT = false

        /** The type of button image returned.
         *  The possible values are sign-in (the default) and continue. **/
        private const val QUERY_TYPE = "type"
        private val QUERY_TYPE_DEFAULT = ButtonType.SIGN_IN

        /** The corner radius for the button image.
         * The minimum and maximum values are 0 and 50, respectively, and the default value is 15. **/
        private const val QUERY_BORDER_RADIUS = "border_radius"
        private const val QUERY_BORDER_RADIUS_MIN = 0
        private const val QUERY_BORDER_RADIUS_DEFAULT = 15
        private const val QUERY_BORDER_RADIUS_MAX = 50

        /** The scale of the button image.
         * The minimum and maximum values are 1 and 6, respectively, and the default value is 1. **/
        private const val QUERY_SCALE = "scale"
        private const val QUERY_SCALE_MIN = 1
        private const val QUERY_SCALE_DEFAULT = QUERY_SCALE_MIN
        private const val QUERY_SCALE_MAX = 6

        /** The language used for text on the button.
         *  The possible values are ar_SA, ca_ES, cs_CZ, da_DK, de_DE, el_GR, en_GB, en_US, es_ES,
         *  es_MX, fi_FI, fr_CA, fr_FR, hr_HR, hu_HU, id_ID, it_IT, iw_IL, ja_JP, ko_KR, ms_MY,
         *  nl_NL, no_NO, pl_PL, pt_BR, pt_PT, ro_RO, ru_RU, sk_SK, sv_SE, th_TH, tr_TR, uk_UA,
         *  vi_VI, zh_CN, zh_HK, and zh_TW. **/
        private const val QUERY_LOCALE = "locale"
    }
}