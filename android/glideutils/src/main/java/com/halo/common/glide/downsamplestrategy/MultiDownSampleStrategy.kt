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
 * A DownsampleStrategy for choose min scale.
 */
class MultiDownSampleStrategy(vararg strategy: DownsampleStrategy) : DownsampleStrategy() {

    private val strateies: List<DownsampleStrategy> = strategy.toList()

    override fun getScaleFactor(sourceWidth: Int, sourceHeight: Int, requestedWidth: Int, requestedHeight: Int): Float {
        return strateies.map {
            it.getScaleFactor(
                sourceWidth,
                sourceHeight,
                requestedWidth,
                requestedHeight
            )
        }.min() ?: 0.0f
    }

    override fun getSampleSizeRounding(sourceWidth: Int, sourceHeight: Int, requestedWidth: Int, requestedHeight: Int): SampleSizeRounding {
        return if (strateies.any { it.getSampleSizeRounding(sourceWidth, sourceHeight, requestedWidth, requestedHeight) == SampleSizeRounding.QUALITY })
            SampleSizeRounding.QUALITY
        else SampleSizeRounding.MEMORY
    }

}