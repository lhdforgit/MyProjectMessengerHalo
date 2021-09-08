/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.common.glide.transformations;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.security.MessageDigest;

/**
 * @author ndn
 * Created by ndn
 * Created on 9/13/18
 * A {@link com.bumptech.glide.load.Transformation} for transforming {@link Bitmap}'s
 * size not to exceed the OpenGl texture size limit.
 */
public final class GlMaxTextureSizeBitmapTransformation extends BitmapTransformation {

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        final int resWidth = toTransform.getWidth();
        final int resHeight = toTransform.getHeight();
        float maxTextureSize = GlMaxTextureCalculator.Companion.getInstance().getGlMaxTextureSize();

        float sizeMultiplier = Math.min(maxTextureSize / resWidth, maxTextureSize / resHeight);
        if (sizeMultiplier < 1) {
            return TransformationUtil.sizeMultiplier(pool, toTransform, sizeMultiplier);
        } else {
            return toTransform;
        }
    }

    public String getId() {
        return "TransformationUtil.GlMaxTextureSizeBitmapTransformation";
    }

    @Override
    public void updateDiskCacheKey(MessageDigest messageDigest) {
        messageDigest.update(getId().getBytes());
    }
}
