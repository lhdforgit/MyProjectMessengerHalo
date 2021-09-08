/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.binding;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.halo.common.glide.transformations.face.FaceCenterCrop;
import com.halo.common.utils.ThumbImageUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * @author ndn
 * Created by ndn
 * Created on 6/1/18.
 * Data Binding adapters specific to the app.
 */
public class BindingAdapters {

    @IntDef({Transform.CENTER_CROP,
            Transform.CIRCLE_CROP,
            Transform.CENTER_INSIDE,
            Transform.FIT_CENTER,
            Transform.FACE_CENTER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Transform {
        int CENTER_CROP = 0;
        int CIRCLE_CROP = 1;
        int CENTER_INSIDE = 2;
        int FIT_CENTER = 3;
        int FACE_CENTER = 4;
    }

    @BindingAdapter(value = {"imageUrl",
            "glideTransform",
            "requestManager",
            "typeSize",
            "size"}, requireAll = false)
    public static void bindImage(
            ImageView imageView,
            String url,
            @Transform int transform,
            RequestManager requestManager,
            @ThumbImageUtils.TypeSize int typeSize,
            @ThumbImageUtils.Size int size) {
        if (requestManager == null) {
            requestManager = Glide.with(imageView.getContext());
        }
        String path = !TextUtils.isEmpty(ThumbImageUtils.thumb(size, url, typeSize))
                ? ThumbImageUtils.thumb(size, url, typeSize) : url;

        switch (transform) {
            case Transform.CENTER_CROP:
                loadCenterCropImage(path, requestManager, imageView);
                break;
            case Transform.CIRCLE_CROP:
                loadCircleCropImage(path, requestManager, imageView);
                break;
            case Transform.CENTER_INSIDE:
                loadCenterInsideImage(path, requestManager, imageView);
                break;
            case Transform.FIT_CENTER:
                loadFitCenterImage(path, requestManager, imageView);
                break;
            case Transform.FACE_CENTER:
                loadFaceCenterCropImage(path, requestManager, imageView);
                break;
        }
    }


    private static void loadCenterCropImage(String url, @NonNull RequestManager requestManager, @NonNull ImageView imageView) {
        RequestBuilder<Drawable> centerRequest = requestManager
                .asDrawable()
                .centerCrop();

        RequestBuilder<Drawable> thumbRequest = requestManager
                .asDrawable()
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .override(ThumbImageUtils.Size.MEDIA_THUMB_LOADING)
                .centerCrop()
                .transition(withCrossFade());

        centerRequest.load(url)
                .thumbnail(thumbRequest.load(url))
                .into(imageView);
    }

    private static void loadCenterCropImage(Uri uri, @NonNull RequestManager requestManager, @NonNull ImageView imageView) {
        requestManager.load(uri)
                .apply(RequestOptions.centerCropTransform())
                .into(imageView);
    }

    private static void loadCircleCropImage(String url, @NonNull RequestManager requestManager, @NonNull ImageView imageView) {
        RequestBuilder<Drawable> centerRequest = requestManager
                .asDrawable()
                .placeholder(com.halo.widget.R.drawable.circle_holder)
                .error(com.halo.widget.R.drawable.circle_holder)
                .circleCrop();

        RequestBuilder<Drawable> thumbRequest = requestManager
                .asDrawable()
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .override(ThumbImageUtils.Size.MEDIA_THUMB_LOADING)
                .circleCrop()
                .transition(withCrossFade());

        centerRequest.load(url)
                .thumbnail(thumbRequest.load(url))
                .into(imageView);
    }

    private static void loadCircleCropImage(Uri uri, @NonNull RequestManager requestManager, @NonNull ImageView imageView) {
        requestManager.load(uri)
                .apply(RequestOptions.circleCropTransform())
                .placeholder(com.halo.widget.R.drawable.circle_holder)
                .error(com.halo.widget.R.drawable.circle_holder)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView);
    }

    private static void loadCenterInsideImage(String url, @NonNull RequestManager requestManager, @NonNull ImageView imageView) {
        RequestBuilder<Drawable> centerRequest = requestManager
                .asDrawable()
                .centerInside();

        RequestBuilder<Drawable> thumbRequest = requestManager
                .asDrawable()
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .override(ThumbImageUtils.Size.MEDIA_THUMB_LOADING)
                .centerCrop()
                .transition(withCrossFade());

        centerRequest.load(url)
                .thumbnail(thumbRequest.load(url))
                .into(imageView);
    }

    private static void loadCenterInsideImage(Uri uri, @NonNull RequestManager requestManager, @NonNull ImageView imageView) {
        requestManager.load(uri)
                .apply(RequestOptions.centerInsideTransform())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView);
    }

    private static void loadFitCenterImage(String url, @NonNull RequestManager requestManager, @NonNull ImageView imageView) {
        RequestBuilder<Drawable> centerRequest = requestManager
                .asDrawable()
                .fitCenter();

        RequestBuilder<Drawable> thumbRequest = requestManager
                .asDrawable()
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .override(ThumbImageUtils.Size.MEDIA_THUMB_LOADING)
                .centerCrop()
                .transition(withCrossFade());

        centerRequest.load(url)
                .thumbnail(thumbRequest.load(url))
                .into(imageView);
    }

    private static void loadFitCenterImage(Uri uri, @NonNull RequestManager requestManager, @NonNull ImageView imageView) {
        requestManager.load(uri)
                .apply(RequestOptions.fitCenterTransform())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView);
    }

    private static void loadFaceCenterCropImage(String url, @NonNull RequestManager requestManager, @NonNull ImageView imageView) {
        RequestBuilder<Drawable> centerRequest = requestManager
                .asDrawable()
                .apply(RequestOptions.bitmapTransform(new FaceCenterCrop()));

        RequestBuilder<Drawable> thumbRequest = requestManager
                .asDrawable()
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .override(ThumbImageUtils.Size.MEDIA_THUMB_LOADING)
                .centerCrop()
                .transition(withCrossFade());

        centerRequest.load(url)
                .thumbnail(thumbRequest.load(url))
                .into(imageView);
    }

    private static void loadFaceCenterCropImage(Uri uri, @NonNull RequestManager requestManager, @NonNull ImageView imageView) {
        requestManager.load(uri)
                .apply(RequestOptions.bitmapTransform(new FaceCenterCrop()))
                .into(imageView);
    }

    @BindingAdapter("visibility")
    public static void visibility(View view, boolean visibility) {
        view.setVisibility(visibility ? VISIBLE : GONE);
    }
}