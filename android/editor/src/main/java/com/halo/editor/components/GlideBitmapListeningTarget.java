/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.editor.components;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.halo.common.utils.SettableFuture;

public class GlideBitmapListeningTarget extends BitmapImageViewTarget {

    private final SettableFuture<Boolean> loaded;

    public GlideBitmapListeningTarget(@NonNull ImageView view, @NonNull SettableFuture<Boolean> loaded) {
        super(view);
        this.loaded = loaded;
    }

    @Override
    protected void setResource(@Nullable Bitmap resource) {
        super.setResource(resource);
        loaded.set(true);
    }

    @Override
    public void onLoadFailed(@Nullable Drawable errorDrawable) {
        super.onLoadFailed(errorDrawable);
        loaded.set(true);
    }
}
