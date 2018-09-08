package com.github.windsekirun.rxsociallogin

import android.support.annotation.CheckResult
import com.github.windsekirun.rxsociallogin.facebook.FacebookLogin
import com.github.windsekirun.rxsociallogin.github.GithubLogin
import com.github.windsekirun.rxsociallogin.google.GoogleLogin
import com.github.windsekirun.rxsociallogin.intenal.rx.BaseSocialObservable
import com.github.windsekirun.rxsociallogin.intenal.utils.Preconditions
import com.github.windsekirun.rxsociallogin.kakao.KakaoLogin
import com.github.windsekirun.rxsociallogin.line.LineLogin
import com.github.windsekirun.rxsociallogin.linkedin.LinkedinLogin
import com.github.windsekirun.rxsociallogin.model.LoginResultItem
import com.github.windsekirun.rxsociallogin.naver.NaverLogin
import com.github.windsekirun.rxsociallogin.twitter.TwitterLogin
import com.github.windsekirun.rxsociallogin.vk.VKLogin
import com.github.windsekirun.rxsociallogin.wordpress.WordpressLogin
import com.github.windsekirun.rxsociallogin.yahoo.YahooLogin
import io.reactivex.Observable

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

    @CheckResult
    @JvmStatic
    fun twitter(login: TwitterLogin): Observable<LoginResultItem> {
        Preconditions.checkNotNull(login, "TwitterLogin")
        return RxTwitterLogin(login)
    }

    @CheckResult
    @JvmStatic
    fun google(login: GoogleLogin): Observable<LoginResultItem> {
        Preconditions.checkNotNull(login, "GoogleLogin")
        return RxGoogleLogin(login)
    }

    @CheckResult
    @JvmStatic
    fun github(login: GithubLogin): Observable<LoginResultItem> {
        Preconditions.checkNotNull(login, "GithubLogin")
        return RxGithubLogin(login)
    }

    @CheckResult
    @JvmStatic
    fun linkedin(login: LinkedinLogin): Observable<LoginResultItem> {
        Preconditions.checkNotNull(login, "LinkedinLogin")
        return RxLinkedinLogin(login)
    }

    @CheckResult
    @JvmStatic
    fun wordpress(login: WordpressLogin): Observable<LoginResultItem> {
        Preconditions.checkNotNull(login, "WordpressLogin")
        return RxWordpressLogin(login)
    }

    @CheckResult
    @JvmStatic
    fun yahoo(login: YahooLogin): Observable<LoginResultItem> {
        Preconditions.checkNotNull(login, "YahooLogin")
        return RxYahooLogin(login)
    }

    @CheckResult
    @JvmStatic
    fun vk(login: VKLogin): Observable<LoginResultItem> {
        Preconditions.checkNotNull(login, "VKLogin")
        return RxVKLogin(login)
    }

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
}
