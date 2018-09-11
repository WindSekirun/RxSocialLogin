package com.github.windsekirun.rxsociallogin

import android.support.annotation.CheckResult
import com.github.windsekirun.rxsociallogin.facebook.FacebookLogin
import com.github.windsekirun.rxsociallogin.github.GithubLogin
import com.github.windsekirun.rxsociallogin.google.GoogleLogin
import com.github.windsekirun.rxsociallogin.intenal.rx.BaseSocialObservable
import com.github.windsekirun.rxsociallogin.kakao.KakaoLogin
import com.github.windsekirun.rxsociallogin.line.LineLogin
import com.github.windsekirun.rxsociallogin.linkedin.LinkedinLogin
import com.github.windsekirun.rxsociallogin.model.LoginResultItem
import com.github.windsekirun.rxsociallogin.naver.NaverLogin
import com.github.windsekirun.rxsociallogin.twitter.TwitterLogin
import com.github.windsekirun.rxsociallogin.vk.VKLogin
import com.github.windsekirun.rxsociallogin.windows.WindowsLogin
import com.github.windsekirun.rxsociallogin.wordpress.WordpressLogin
import com.github.windsekirun.rxsociallogin.yahoo.YahooLogin
import io.reactivex.Observable

object RxSocialLogin {

    @CheckResult
    @JvmStatic
    fun facebook(login: FacebookLogin): Observable<LoginResultItem> = RxFacebookLogin(login)

    @CheckResult
    @JvmStatic
    fun kakao(login: KakaoLogin): Observable<LoginResultItem> = RxKakaoLogin(login)

    @CheckResult
    @JvmStatic
    fun naver(login: NaverLogin): Observable<LoginResultItem> = RxNaverLogin(login)

    @CheckResult
    @JvmStatic
    fun line(login: LineLogin): Observable<LoginResultItem> = RxLineLogin(login)

    @CheckResult
    @JvmStatic
    fun twitter(login: TwitterLogin): Observable<LoginResultItem> = RxTwitterLogin(login)

    @CheckResult
    @JvmStatic
    fun google(login: GoogleLogin): Observable<LoginResultItem> = RxGoogleLogin(login)

    @CheckResult
    @JvmStatic
    fun github(login: GithubLogin): Observable<LoginResultItem> = RxGithubLogin(login)

    @CheckResult
    @JvmStatic
    fun linkedin(login: LinkedinLogin): Observable<LoginResultItem> = RxLinkedinLogin(login)

    @CheckResult
    @JvmStatic
    fun wordpress(login: WordpressLogin): Observable<LoginResultItem> = RxWordpressLogin(login)

    @CheckResult
    @JvmStatic
    fun yahoo(login: YahooLogin): Observable<LoginResultItem> = RxYahooLogin(login)

    @CheckResult
    @JvmStatic
    fun vk(login: VKLogin): Observable<LoginResultItem> = RxVKLogin(login)

    @CheckResult
    @JvmStatic
    fun windows(login: WindowsLogin): Observable<LoginResultItem> = RxWindowsLogin(login)

    internal class RxFacebookLogin(login: FacebookLogin) : BaseSocialObservable<FacebookLogin>(login)
    internal class RxGithubLogin(login: GithubLogin) : BaseSocialObservable<GithubLogin>(login)
    internal class RxGoogleLogin(login: GoogleLogin) : BaseSocialObservable<GoogleLogin>(login)
    internal class RxKakaoLogin(login: KakaoLogin) : BaseSocialObservable<KakaoLogin>(login)
    internal class RxLineLogin(login: LineLogin) : BaseSocialObservable<LineLogin>(login)
    internal class RxLinkedinLogin(login: LinkedinLogin) : BaseSocialObservable<LinkedinLogin>(login)
    internal class RxNaverLogin(login: NaverLogin) : BaseSocialObservable<NaverLogin>(login)
    internal class RxTwitterLogin(login: TwitterLogin) : BaseSocialObservable<TwitterLogin>(login)
    internal class RxWordpressLogin(login: WordpressLogin) : BaseSocialObservable<WordpressLogin>(login)
    internal class RxYahooLogin(login: YahooLogin) : BaseSocialObservable<YahooLogin>(login)
    internal class RxVKLogin(login: VKLogin) : BaseSocialObservable<VKLogin>(login)
    internal class RxWindowsLogin(login: WindowsLogin) : BaseSocialObservable<WindowsLogin>(login)
}
