/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.imageviewer.loader;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.request.RequestListener;

/**
 * Interface definition for a callback to be invoked when image should be loaded
 */
//N.B.! This class is written in Java for convenient use of lambdas due to languages compatibility issues.
public interface ImageLoader<T> {

    /**
     * Fires every time when image object should be displayed in a provided {@link ImageView}
     *
     * @param imageView an {@link ImageView} object where the image should be loaded
     * @param image     image data from which image should be loaded
     */
    void loadImage(ImageView imageView, RequestListener<Drawable> listener , T image);
}
