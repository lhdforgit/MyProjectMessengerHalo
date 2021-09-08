/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.common.glide.downsamplestrategy

import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import com.halo.common.glide.transformations.GlMaxTextureCalculator


/**
 * @author ndn
 * Created by ndn
 * Created on 9/13/18
 * A DownsampleStrategy for Downsample Bitmap]'s size not to exceed the OpenGl texture size limit.
 */
class GlMaxTextureSizeDownSampleStrategy : DownsampleStrategy() {
    override fun getScaleFactor(sourceWidth: Int, sourceHeight: Int, requestedWidth: Int, requestedHeight: Int): Float {
        val maxTextureSize: Float = GlMaxTextureCalculator.instance.glMaxTextureSize.toFloat()
        val sizeMultiplier: Float =
            (maxTextureSize / sourceWidth).coerceAtMost(maxTextureSize / sourceHeight)
        return if (sizeMultiplier < 1) sizeMultiplier else 1.0F
    }

    override fun getSampleSizeRounding(sourceWidth: Int, sourceHeight: Int, requestedWidth: Int, requestedHeight: Int): SampleSizeRounding {
        return SampleSizeRounding.QUALITY
    }

}