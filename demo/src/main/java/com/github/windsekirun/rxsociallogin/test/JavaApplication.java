package com.github.windsekirun.rxsociallogin.test;

import android.app.Application;

import com.github.windsekirun.rxsociallogin.RxSocialLogin;
import com.github.windsekirun.rxsociallogin.facebook.FacebookConfig;

import java.util.ArrayList;


/**
 * RxSocialLogin
 * Class: JavaApplication
 * Created by Pyxis on 10/7/18.
 * <p>
 * Description:
 */
public class JavaApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RxSocialLogin.initSocialLogin(this, builder -> {
            builder.disqus(getString(R.string.disqus_api_key),
                    getString(R.string.disqus_api_secret),
                    getString(R.string.disqus_redirect_uri),
                    config -> config.setActivityTitle("Login to Disqus"));

            builder.facebook(getString(R.string.facebook_api_key), config -> {
                config.setRequireEmail(true);
                config.setRequireFriends(true);
                config.setBehaviorOnCancel(true);
                config.setImageEnum(FacebookConfig.FacebookImageEnum.Large);
            });

            builder.foursquare(getString(R.string.foursquare_api_key), getString(R.string.foursquare_api_secret));

            builder.github(getString(R.string.github_api_key),
                    getString(R.string.github_api_secret), config -> {
                        config.setScopeConfig(new ArrayList<>());
                        config.setActivityTitle("Login to Github");
                    });

            builder.google(getString(R.string.google_api_key), config -> config.setRequireEmail(true));

            builder.kakao(config -> {
                config.setRequireAgeRange(true);
                config.setRequireBirthday(true);
                config.setRequireEmail(true);
                config.setRequireGender(true);
            });

            builder.line(getString(R.string.line_api_channel));

            builder.linkedin(getString(R.string.linkedin_api_key),
                    getString(R.string.linkedin_api_secret),
                    getString(R.string.linkedin_redirect_uri),
                    config -> {
                        config.setRequireEmail(true);
                        config.setActivityTitle("Login to Linkedin");
                    });

            builder.naver(getString(R.string.naver_api_id),
                    getString(R.string.naver_api_secret),
                    getString(R.string.app_name));

            builder.twitch(getString(R.string.twitch_api_key),
                    getString(R.string.twitch_api_secret),
                    getString(R.string.twitch_redirect_uri),
                    config -> {
                        config.setRequireEmail(true);
                        config.setActivityTitle("Login to Twitch");
                    });

            builder.twitter(getString(R.string.twitter_api_id), getString(R.string.twitter_api_secret));

            builder.vk(config -> config.setRequireEmail(true));

            builder.windows(getString(R.string.windows_api_key));

            builder.wordpress(getString(R.string.wordpress_api_key),
                    getString(R.string.wordpress_api_secret),
                    getString(R.string.wordpress_redirect_uri),
                    config -> config.setActivityTitle("Login to Wordpress"));

            builder.yahoo(getString(R.string.yahoo_api_key),
                    getString(R.string.yahoo_api_secret),
                    getString(R.string.yahoo_redirect_uri),
                    config -> config.setActivityTitle("Login to Yahoo"));
        });
    }
}
