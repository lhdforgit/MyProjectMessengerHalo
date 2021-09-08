/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.presentation.gallery.engine;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;

/**
 * @author ndn
 * Created by ndn
 * Created on 7/11/18
 * {@link ImageEngine} implementation using Glide.
 */

public class GlideEngine implements ImageEngine {

    @Override
    public void loadThumbnail(Context context, ImageView imageView, Uri uri) {
        Glide.with(context)
                .asBitmap()  // some .jpeg files are actually gif
                .load(uri)
                .apply(RequestOptions.centerCropTransform())
                .into(imageView);
    }

    @Override
    public void loadThumbnail(Context context, int resize, Drawable placeholder, ImageView imageView, Uri uri) {
        Glide.with(context)
                .asBitmap()  // some .jpeg files are actually gif
                .load(uri)
                .apply(RequestOptions.placeholderOf(placeholder))
                .apply(RequestOptions.overrideOf(resize, resize))
                .apply(RequestOptions.centerCropTransform())
                .into(imageView);
    }

    @Override
    public void loadGifThumbnail(Context context, ImageView imageView, Uri uri) {
        Glide.with(context)
                .asGif()  // some .jpeg files are actually gif
                .load(uri)
                .apply(RequestOptions.centerCropTransform())
                .into(imageView);
    }

    @Override
    public void loadGifThumbnail(Context context, int resize, Drawable placeholder, ImageView imageView, Uri uri) {
        Glide.with(context)
                .asGif()  // some .jpeg files are actually gif
                .load(uri)
                .apply(RequestOptions.placeholderOf(placeholder))
                .apply(RequestOptions.overrideOf(resize, resize))
                .apply(RequestOptions.centerCropTransform())
                .into(imageView);
    }

    @Override
    public void loadImage(Context context, int resizeX, int resizeY, ImageView imageView, Uri uri) {
        Glide.with(context)
                .load(uri)
                .apply(RequestOptions.overrideOf(resizeX, resizeY))
                .apply(RequestOptions.priorityOf(Priority.HIGH))
                .apply(RequestOptions.fitCenterTransform())
                .into(imageView);
    }

    @Override
    public void loadGifImage(Context context, int resizeX, int resizeY, ImageView imageView, Uri uri) {
        Glide.with(context)
                .asGif()
                .load(uri)
                .apply(RequestOptions.overrideOf(resizeX, resizeY))
                .apply(RequestOptions.priorityOf(Priority.HIGH))
                .into(imageView);
    }

    @Override
    public boolean supportAnimatedGif() {
        return true;
    }
}
