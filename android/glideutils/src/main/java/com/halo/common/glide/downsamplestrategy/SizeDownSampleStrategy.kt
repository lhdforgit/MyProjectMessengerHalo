/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.common.glide.downsamplestrategy

import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy

/**
 * @author ndn
 * Created by ndn
 * Created on 9/13/18
 * A DownsampleStrategy for Downsample Bitmap]'s size with target size.Scale type use CENTER_INSIDE
 * Gif is invalid.
 */
class SizeDownSampleStrategy(val size: Int) : DownsampleStrategy() {
    override fun getScaleFactor(sourceWidth: Int, sourceHeight: Int, requestedWidth: Int, requestedHeight: Int): Float {
        return (size.toFloat() / sourceWidth).coerceAtMost(size.toFloat() / sourceHeight)
    }

    override fun getSampleSizeRounding(sourceWidth: Int, sourceHeight: Int, requestedWidth: Int, requestedHeight: Int): SampleSizeRounding {
        return SampleSizeRounding.QUALITY
    }
}