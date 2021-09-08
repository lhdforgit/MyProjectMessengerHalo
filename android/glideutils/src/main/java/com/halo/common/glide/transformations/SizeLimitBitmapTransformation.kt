/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.common.glide.transformations

import android.graphics.Bitmap
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import java.security.MessageDigest

/**
 * @author ndn
 * Created by ndn
 * Created on 9/13/18
 * A [com.bumptech.glide.load.Transformation] for transforming [Bitmap]'s
 * size limit to target size.
 */
class SizeLimitBitmapTransformation(private val mSize: Int) : BitmapTransformation() {

    val id: String
        get() = "TransformationUtil.SizeMultiplierBitmapTransformation"

    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap {
        val resWidth = toTransform.width
        val resHeight = toTransform.height
        if (mSize >= resWidth.coerceAtLeast(resHeight)) {
            return toTransform
        }
        val mSizeMultiplier = (mSize.toFloat() / resWidth).coerceAtMost(mSize.toFloat() / resHeight)
        return TransformationUtil.sizeMultiplier(pool, toTransform, mSizeMultiplier)
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(id.toByteArray())
    }
}
