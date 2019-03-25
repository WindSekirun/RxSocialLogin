package com.github.windsekirun.rxsociallogin.test.binding;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import androidx.databinding.BindingAdapter;

/**
 * RxSocialLogin
 * Class: BindAdapter
 * Created by Pyxis on 2019-02-07.
 * <p>
 * Description:
 */
public class BindAdapter {

    @BindingAdapter("circleImage")
    public static void bindCircleImage(ImageView imageView, String url) {
        Glide.with(imageView.getContext()).load(url).apply(RequestOptions.circleCropTransform()).into(imageView);
    }
}
