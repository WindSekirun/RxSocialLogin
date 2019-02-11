package com.github.windsekirun.rxsociallogin

import android.app.Application
import com.github.windsekirun.rxsociallogin.amazon.AmazonConfig
import com.github.windsekirun.rxsociallogin.bitbucket.BitbucketConfig
import com.github.windsekirun.rxsociallogin.discord.DiscordConfig
import com.github.windsekirun.rxsociallogin.discord.DiscordLogin
import com.github.windsekirun.rxsociallogin.disqus.DisqusConfig
import com.github.windsekirun.rxsociallogin.facebook.FacebookConfig
import com.github.windsekirun.rxsociallogin.foursquare.FoursquareConfig
import com.github.windsekirun.rxsociallogin.github.GithubConfig
import com.github.windsekirun.rxsociallogin.gitlab.GitlabConfig
import com.github.windsekirun.rxsociallogin.google.GoogleConfig
import com.github.windsekirun.rxsociallogin.intenal.impl.ConfigFunction
import com.github.windsekirun.rxsociallogin.intenal.impl.invoke
import com.github.windsekirun.rxsociallogin.intenal.model.PlatformType
import com.github.windsekirun.rxsociallogin.intenal.model.SocialConfig
import com.github.windsekirun.rxsociallogin.kakao.KakaoConfig
import com.github.windsekirun.rxsociallogin.line.LineConfig
import com.github.windsekirun.rxsociallogin.linkedin.LinkedinConfig
import com.github.windsekirun.rxsociallogin.naver.NaverConfig
import com.github.windsekirun.rxsociallogin.twitch.TwitchConfig
import com.github.windsekirun.rxsociallogin.twitter.TwitterConfig
import com.github.windsekirun.rxsociallogin.vk.VKConfig
import com.github.windsekirun.rxsociallogin.windows.WindowsConfig
import com.github.windsekirun.rxsociallogin.wordpress.WordpressConfig
import com.github.windsekirun.rxsociallogin.yahoo.YahooConfig

fun Application.initSocialLogin(setup: ConfigDSLBuilder.() -> Unit) {
    val builder = ConfigDSLBuilder(this)
    builder.setup()
    builder.build()
}

fun Application.initSocialLoginJava(setup: ConfigBuilder.() -> Unit) {
    val builder = ConfigBuilder(this)
    builder.setup()
    builder.build()
}

interface BuilderFunction {
    fun invoke(builder: ConfigBuilder)
}

open class BaseConfigDSLBuilder(val application: Application) {
    internal val typeMap: MutableMap<PlatformType, SocialConfig> = mutableMapOf()

    fun foursquare(clientId: String, clientSecret: String) {
        typeMap[PlatformType.FOURSQUARE] = FoursquareConfig.apply(clientId, clientSecret)
    }

    fun line(channelId: String) {
        typeMap[PlatformType.LINE] = LineConfig.apply(channelId)
    }

    fun naver(authClientId: String, authClientSecret: String, clientName: String) {
        typeMap[PlatformType.NAVER] = NaverConfig.apply(authClientId, authClientSecret, clientName)
    }

    fun twitter(consumerKey: String, consumerSecret: String) {
        typeMap[PlatformType.TWITTER] = TwitterConfig.apply(consumerKey, consumerSecret)
    }

    fun windows(clientId: String) {
        typeMap[PlatformType.WINDOWS] = WindowsConfig.apply(clientId)
    }

    fun amazon() {
        typeMap[PlatformType.AMAZON] = AmazonConfig.apply()
    }

    internal fun build() {
        RxSocialLogin.initializeInternal(application, typeMap)
    }
}

class ConfigDSLBuilder(application: Application) : BaseConfigDSLBuilder(application) {

    fun disqus(clientId: String, clientSecret: String, redirectUri: String,
               setup: DisqusConfig.() -> Unit = {}) {
        typeMap[PlatformType.DISQUS] = DisqusConfig.apply(clientId, clientSecret, redirectUri, invoke(setup))
    }

    fun facebook(applicationId: String, setup: FacebookConfig.() -> Unit = {}) {
        typeMap[PlatformType.FACEBOOK] = FacebookConfig.apply(applicationId, invoke(setup))
    }

    fun github(clientId: String, clientSecret: String, setup: GithubConfig.() -> Unit = {}) {
        typeMap[PlatformType.GITHUB] = GithubConfig.apply(clientId, clientSecret, invoke(setup))
    }

    fun google(clientTokenId: String, setup: GoogleConfig.() -> Unit = {}) {
        typeMap[PlatformType.GOOGLE] = GoogleConfig.apply(clientTokenId, invoke(setup))
    }

    fun kakao(setup: KakaoConfig.() -> Unit = {}) {
        typeMap[PlatformType.KAKAO] = KakaoConfig.apply(invoke(setup))
    }

    fun linkedin(clientId: String, clientSecret: String, redirectUri: String,
                 setup: LinkedinConfig.() -> Unit = {}) {
        typeMap[PlatformType.LINKEDIN] = LinkedinConfig.apply(clientId, clientSecret, redirectUri, invoke(setup))
    }

    fun twitch(clientId: String, clientSecret: String, redirectUri: String,
               setup: TwitchConfig.() -> Unit = {}) {
        typeMap[PlatformType.TWITCH] = TwitchConfig.apply(clientId, clientSecret, redirectUri, invoke(setup))
    }

    fun vk(setup: VKConfig.() -> Unit = {}) {
        typeMap[PlatformType.VK] = VKConfig.apply(invoke(setup))
    }

    fun wordpress(clientId: String, clientSecret: String, redirectUri: String,
                  setup: WordpressConfig.() -> Unit = {}) {
        typeMap[PlatformType.WORDPRESS] = WordpressConfig.apply(clientId, clientSecret, redirectUri, invoke(setup))
    }

    fun yahoo(clientId: String, clientSecret: String, redirectUri: String,
              setup: YahooConfig.() -> Unit = {}) {
        typeMap[PlatformType.YAHOO] = YahooConfig.apply(clientId, clientSecret, redirectUri, invoke(setup))
    }

    fun discord(clientId: String, clientSecret: String, redirectUri: String, setup: DiscordConfig.() -> Unit =  {}) {
        typeMap[PlatformType.DISCORD] = DiscordConfig.apply(clientId, clientSecret, redirectUri, invoke(setup))
    }

    fun bitbucket(clientId: String, clientSecret: String, redirectUri: String, setup: BitbucketConfig.() -> Unit =  {}) {
        typeMap[PlatformType.BITBUCKET] = BitbucketConfig.apply(clientId, clientSecret, redirectUri, invoke(setup))
    }

    fun gitlab(clientId: String, clientSecret: String, redirectUri: String, setup: GitlabConfig.() -> Unit =  {}) {
        typeMap[PlatformType.GITLAB] = GitlabConfig.apply(clientId, clientSecret, redirectUri, invoke(setup))
    }
}

class ConfigBuilder(application: Application) : BaseConfigDSLBuilder(application) {

    @JvmOverloads
    fun disqus(clientId: String, clientSecret: String, redirectUri: String,
               setup: ConfigFunction<DisqusConfig> = EmptyFunction()) {
        typeMap[PlatformType.DISQUS] = DisqusConfig.apply(clientId, clientSecret, redirectUri, setup)
    }

    @JvmOverloads
    fun facebook(applicationId: String, setup: ConfigFunction<FacebookConfig> = EmptyFunction()) {
        typeMap[PlatformType.FACEBOOK] = FacebookConfig.apply(applicationId, setup)
    }

    @JvmOverloads
    fun github(clientId: String, clientSecret: String, setup: ConfigFunction<GithubConfig> = EmptyFunction()) {
        typeMap[PlatformType.GITHUB] = GithubConfig.apply(clientId, clientSecret, setup)
    }

    @JvmOverloads
    fun google(clientTokenId: String, setup: ConfigFunction<GoogleConfig> = EmptyFunction()) {
        typeMap[PlatformType.GOOGLE] = GoogleConfig.apply(clientTokenId, setup)
    }

    @JvmOverloads
    fun kakao(setup: ConfigFunction<KakaoConfig> = EmptyFunction()) {
        typeMap[PlatformType.KAKAO] = KakaoConfig.apply(setup)
    }

    @JvmOverloads
    fun linkedin(clientId: String, clientSecret: String, redirectUri: String,
                 setup: ConfigFunction<LinkedinConfig> = EmptyFunction()) {
        typeMap[PlatformType.LINKEDIN] = LinkedinConfig.apply(clientId, clientSecret, redirectUri, setup)
    }

    @JvmOverloads
    fun twitch(clientId: String, clientSecret: String, redirectUri: String,
               setup: ConfigFunction<TwitchConfig> = EmptyFunction()) {
        typeMap[PlatformType.TWITCH] = TwitchConfig.apply(clientId, clientSecret, redirectUri, setup)
    }

    @JvmOverloads
    fun vk(setup: ConfigFunction<VKConfig> = EmptyFunction()) {
        typeMap[PlatformType.VK] = VKConfig.apply(setup)
    }

    @JvmOverloads
    fun wordpress(clientId: String, clientSecret: String, redirectUri: String,
                  setup: ConfigFunction<WordpressConfig> = EmptyFunction()) {
        typeMap[PlatformType.WORDPRESS] = WordpressConfig.apply(clientId, clientSecret, redirectUri, setup)
    }

    @JvmOverloads
    fun yahoo(clientId: String, clientSecret: String, redirectUri: String,
              setup: ConfigFunction<YahooConfig> = EmptyFunction()) {
        typeMap[PlatformType.YAHOO] = YahooConfig.apply(clientId, clientSecret, redirectUri, setup)
    }

    @JvmOverloads
    fun discord(clientId: String, clientSecret: String, redirectUri: String,
              setup: ConfigFunction<DiscordConfig> = EmptyFunction()) {
        typeMap[PlatformType.DISCORD] = DiscordConfig.apply(clientId, clientSecret, redirectUri, setup)
    }

    @JvmOverloads
    fun bitbucket(clientId: String, clientSecret: String, redirectUri: String,
                setup: ConfigFunction<BitbucketConfig> = EmptyFunction()) {
        typeMap[PlatformType.BITBUCKET] = BitbucketConfig.apply(clientId, clientSecret, redirectUri, setup)
    }

    @JvmOverloads
    fun gitlab(clientId: String, clientSecret: String, redirectUri: String,
                  setup: ConfigFunction<GitlabConfig> = EmptyFunction()) {
        typeMap[PlatformType.GITLAB] = GitlabConfig.apply(clientId, clientSecret, redirectUri, setup)
    }

    internal class EmptyFunction<T> : ConfigFunction<T> {
        override fun invoke(config: T) {}
    }
}