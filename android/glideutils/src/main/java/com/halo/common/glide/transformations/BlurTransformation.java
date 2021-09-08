/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.common.glide.transformations;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.renderscript.RSRuntimeException;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;
import com.halo.common.glide.transformations.internal.FastBlur;
import com.halo.common.glide.transformations.internal.RSBlur;

import java.security.MessageDigest;

/**
 * @author ndn
 * Created by ndn
 * Created on 9/13/18
 */
public class BlurTransformation implements Transformation<Bitmap> {

    private static int MAX_RADIUS = 25;
    private static int DEFAULT_DOWN_SAMPLING = 1;

    private Context mContext;
    private BitmapPool mBitmapPool;

    private int mRadius;
    private int mSampling;
    private int mTargetSize;

    public BlurTransformation(Context context) {
        this(context, Glide.get(context).getBitmapPool(), MAX_RADIUS, DEFAULT_DOWN_SAMPLING);
    }

    public BlurTransformation(Context context, BitmapPool pool) {
        this(context, pool, MAX_RADIUS, DEFAULT_DOWN_SAMPLING);
    }

    public BlurTransformation(Context context, BitmapPool pool, int radius) {
        this(context, pool, radius, DEFAULT_DOWN_SAMPLING);
    }

    public BlurTransformation(Context context, int radius) {
        this(context, Glide.get(context).getBitmapPool(), radius, DEFAULT_DOWN_SAMPLING);
    }

    public BlurTransformation(Context context, int radius, int sampling) {
        this(context, Glide.get(context).getBitmapPool(), radius, sampling);
    }

    public BlurTransformation(Context context, BitmapPool pool, int radius, int sampling) {
        mContext = context.getApplicationContext();
        mBitmapPool = pool;
        mRadius = radius;
        mSampling = sampling;
    }

    public int getTargetSize() {
        return mTargetSize;
    }

    public void setTargetSize(int targetSize) {
        this.mTargetSize = targetSize;
    }

    @NonNull
    @Override
    public Resource<Bitmap> transform(@NonNull Context context, @NonNull Resource<Bitmap> resource, int outWidth, int outHeight) {
        Bitmap source = resource.get();

        int width = source.getWidth();
        int height = source.getHeight();

        float sampling = mSampling;
        int radius = mRadius;
        if (mTargetSize > 0) {
            sampling = Math.min((float) width / mTargetSize, (float) height / mTargetSize);
            if (sampling < 1) {
                //targetSize bigger than resource width or height, decrease radius, not sampling
                radius = (int) (mRadius * sampling);
                sampling = 1;
            }
        }

        int scaledWidth = (int) (width / sampling);
        int scaledHeight = (int) (height / sampling);

        Bitmap bitmap = mBitmapPool.get(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        canvas.scale(1 / sampling, 1 / sampling);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(source, 0, 0, paint);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            try {
                bitmap = RSBlur.blur(mContext, bitmap, radius);
            } catch (RSRuntimeException e) {
                e.printStackTrace();
                bitmap = FastBlur.blur(bitmap, radius, true);
            }
        } else {
            bitmap = FastBlur.blur(bitmap, radius, true);
        }

        return BitmapResource.obtain(bitmap, mBitmapPool);
    }

    @NonNull
    public String getId() {
        return "BlurTransformation(radius=" + mRadius + ", sampling=" + mSampling + ")";
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update(getId().getBytes());
    }
}
