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
 * A DownsampleStrategy for Downsample Bitmap]'s size not to exceed view width size limit.
 */
class FitOutWidthDownSampleStrategy : DownsampleStrategy() {

    override fun getScaleFactor(sourceWidth: Int, sourceHeight: Int, requestedWidth: Int, requestedHeight: Int): Float {
        val fitOutSizeMultiplier: Float = requestedWidth.toFloat() / sourceWidth.toFloat()
        return if (fitOutSizeMultiplier < 1) fitOutSizeMultiplier else 1.0F
    }

    override fun getSampleSizeRounding(sourceWidth: Int, sourceHeight: Int, requestedWidth: Int, requestedHeight: Int): SampleSizeRounding {
        return SampleSizeRounding.QUALITY
    }

}