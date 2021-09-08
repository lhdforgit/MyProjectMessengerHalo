/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.presentation.utils.media;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.halo.R;
import com.halo.common.glide.transformations.face.FaceCenterCrop;
import com.halo.common.utils.ThumbImageUtils;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.halo.binding.BindingAdapters.Transform.CENTER_CROP;
import static com.halo.binding.BindingAdapters.Transform.CENTER_INSIDE;
import static com.halo.binding.BindingAdapters.Transform.CIRCLE_CROP;
import static com.halo.binding.BindingAdapters.Transform.FACE_CENTER;
import static com.halo.binding.BindingAdapters.Transform.FIT_CENTER;

public class MediaLoaderUtils {
    public static void loadMedia(ImageView imageView,
                                 String uri,
                                 RequestManager requestManager) {
        loadMedia(imageView, uri, requestManager, null);
    }

    public static void loadMediaFace(ImageView imageView,
                                     String uri,
                                     RequestManager requestManager) {
        loadFaceCenterCropImage(uri, requestManager, imageView, null, null);
    }

    public static void loadMedia(ImageView imageView,
                                 String uri,
                                 RequestManager requestManager,
                                 MediaLoaderListener mediaLoaderListener) {


//        if (imageView != null && requestManager != null && !TextUtils.isEmpty(uri)) {
//
            RequestListener<Drawable> requestListener = new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    if (mediaLoaderListener != null) mediaLoaderListener.onLoadFailed();
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    if (mediaLoaderListener != null) mediaLoaderListener.onResourceReady();
                    return false;
                }
            };
        loadMedia(imageView, uri, requestManager , requestListener , mediaLoaderListener);
//            switch (mediaLoaderListener != null ? mediaLoaderListener.transform() : FIT_CENTER) {
//                case CENTER_CROP:
//                    loadCenterCropImage(uri, requestManager, imageView, mediaLoaderListener, requestListener);
//                    break;
//                case CIRCLE_CROP:
//                    loadCircleCropImage(uri, requestManager, imageView, mediaLoaderListener, requestListener);
//                    break;
//                case CENTER_INSIDE:
//                    loadCenterInsideImage(uri, requestManager, imageView, mediaLoaderListener, requestListener);
//                    break;
//                case FIT_CENTER:
//                    loadFitCenterImage(uri, requestManager, imageView, mediaLoaderListener, requestListener);
//                    break;
//                case FACE_CENTER:
//                    loadFaceCenterCropImage(uri, requestManager, imageView, mediaLoaderListener, requestListener);
//                    break;
//                default:
//                    loadCenterCropImage(uri, requestManager, imageView, mediaLoaderListener, requestListener);
//                    break;
//            }
//        }
    }

    public static void loadMedia(ImageView imageView,
                                 String uri,
                                 RequestManager requestManager,
                                 RequestListener<Drawable> requestListener,
                                 MediaLoaderListener mediaLoaderListener) {
        if (imageView != null && requestManager != null && !TextUtils.isEmpty(uri)) {

            switch (mediaLoaderListener != null ? mediaLoaderListener.transform() : FIT_CENTER) {
                case CENTER_CROP:
                    loadCenterCropImage(uri, requestManager, imageView, mediaLoaderListener, requestListener);
                    break;
                case CIRCLE_CROP:
                    loadCircleCropImage(uri, requestManager, imageView, mediaLoaderListener, requestListener);
                    break;
                case CENTER_INSIDE:
                    loadCenterInsideImage(uri, requestManager, imageView, mediaLoaderListener, requestListener);
                    break;
                case FIT_CENTER:
                    loadFitCenterImage(uri, requestManager, imageView, mediaLoaderListener, requestListener);
                    break;
                case FACE_CENTER:
                    loadFaceCenterCropImage(uri, requestManager, imageView, mediaLoaderListener, requestListener);
                    break;
                default:
                    loadCenterCropImage(uri, requestManager, imageView, mediaLoaderListener, requestListener);
                    break;
            }
        }
    }

    private static void loadCenterCropImage(@NonNull String url,
                                            @NonNull RequestManager requestManager,
                                            @NonNull ImageView imageView,
                                            MediaLoaderListener mediaLoaderListener,
                                            @NonNull RequestListener<Drawable> requestListener) {

        RequestBuilder<Drawable> centerRequest = requestManager
                .asDrawable()
                .listener(requestListener)
                .placeholder(mediaLoaderListener != null ? mediaLoaderListener.placeholder() : R.color.img_holder)
                .error(mediaLoaderListener != null ? mediaLoaderListener.error() : R.color.img_holder)
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

    private static void loadCircleCropImage(@NonNull String url,
                                            @NonNull RequestManager requestManager,
                                            @NonNull ImageView imageView,
                                            MediaLoaderListener mediaLoaderListener,
                                            @NonNull RequestListener<Drawable> requestListener) {
        RequestBuilder<Drawable> centerRequest = requestManager
                .asDrawable()
                .listener(requestListener)
                .placeholder(mediaLoaderListener != null ? mediaLoaderListener.placeholder() : R.color.img_holder)
                .error(mediaLoaderListener != null ? mediaLoaderListener.error() : R.color.img_holder)
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

    private static void loadCenterInsideImage(@NonNull String url,
                                              @NonNull RequestManager requestManager,
                                              @NonNull ImageView imageView,
                                              MediaLoaderListener mediaLoaderListener,
                                              @NonNull RequestListener<Drawable> requestListener) {

        RequestBuilder<Drawable> centerRequest = requestManager
                .asDrawable()
                .listener(requestListener)
                .placeholder(mediaLoaderListener != null ? mediaLoaderListener.placeholder() : R.color.img_holder)
                .error(mediaLoaderListener != null ? mediaLoaderListener.error() : R.color.img_holder)
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

    private static void loadFitCenterImage(@NonNull String url,
                                           @NonNull RequestManager requestManager,
                                           @NonNull ImageView imageView,
                                           MediaLoaderListener mediaLoaderListener,
                                           @NonNull RequestListener<Drawable> requestListener) {
        RequestBuilder<Drawable> centerRequest = requestManager
                .asDrawable()
                .listener(requestListener)
                .placeholder(mediaLoaderListener != null ? mediaLoaderListener.placeholder() : R.color.img_holder)
                .error(mediaLoaderListener != null ? mediaLoaderListener.error() : R.color.img_holder)
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

    private static void loadFaceCenterCropImage(@NonNull String url,
                                                @NonNull RequestManager requestManager,
                                                @NonNull ImageView imageView,
                                                MediaLoaderListener mediaLoaderListener,
                                                RequestListener<Drawable> requestListener) {
        RequestBuilder<Drawable> centerRequest = requestManager
                .asDrawable()
                .listener(requestListener)
                .placeholder(mediaLoaderListener != null ? mediaLoaderListener.placeholder() : R.color.img_holder)
                .error(mediaLoaderListener != null ? mediaLoaderListener.error() : R.color.img_holder)
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

    public interface MediaLoaderListener {

        default int transform() {
            return 3;
        }

        default void onLoadFailed() {
        }

        default void onResourceReady() {
        }

        default int placeholder() {
            return R.color.img_holder;
        }

        default int error() {
            return R.color.img_holder;
        }
    }
}
