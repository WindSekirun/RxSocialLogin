package com.github.windsekirun.rxsociallogin

import android.support.annotation.CheckResult

import com.github.windsekirun.rxsociallogin.facebook.FacebookLogin
import com.github.windsekirun.rxsociallogin.facebook.RxFacebookLogin
import com.github.windsekirun.rxsociallogin.intenal.Preconditions
import com.github.windsekirun.rxsociallogin.kakao.KakaoLogin
import com.github.windsekirun.rxsociallogin.kakao.RxKakaoLogin
import com.github.windsekirun.rxsociallogin.line.LineLogin
import com.github.windsekirun.rxsociallogin.line.RxLineLogin
import com.github.windsekirun.rxsociallogin.model.LoginResultItem
import com.github.windsekirun.rxsociallogin.naver.NaverLogin
import com.github.windsekirun.rxsociallogin.naver.RxNaverLogin
import io.reactivex.Observable

/**
 * RxSocialLogin
 * Class: RxSocialLogin
 * Created by pyxis on 18. 7. 11.
 *
 *
 * Description:
 */
object RxSocialLogin {

    @CheckResult
    @JvmStatic
    fun facebook(login: FacebookLogin): Observable<LoginResultItem> {
        Preconditions.checkNotNull(login, "FacebookLogin")
        return RxFacebookLogin(login)
    }

    @CheckResult
    @JvmStatic
    fun kakao(login: KakaoLogin): Observable<LoginResultItem> {
        Preconditions.checkNotNull(login, "KakaoLogin")
        return RxKakaoLogin(login)
    }

    @CheckResult
    @JvmStatic
    fun naver(login: NaverLogin): Observable<LoginResultItem> {
        Preconditions.checkNotNull(login, "NaverLogin")
        return RxNaverLogin(login)
    }

    @CheckResult
    @JvmStatic
    fun line(login: LineLogin): Observable<LoginResultItem> {
        Preconditions.checkNotNull(login, "LineLogin")
        return RxLineLogin(login)
    }
}
