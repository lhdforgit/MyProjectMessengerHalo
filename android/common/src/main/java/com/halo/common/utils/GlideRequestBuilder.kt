/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.common.utils

import android.graphics.drawable.Drawable
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestOptions
import com.halo.common.glide.downsamplestrategy.FitOutWidthDownSampleStrategy
import com.halo.common.glide.downsamplestrategy.GlMaxTextureSizeDownSampleStrategy
import com.halo.common.glide.downsamplestrategy.MultiDownSampleStrategy
import com.halo.common.glide.transformations.face.FaceCenterCrop

/**
 * @author ndn
 * Created by ndn
 * Created on 2019-10-15
 * com.halo.common.glide
 */

class GlideRequestBuilder {

    /**
     * Performance Tips
     * Animations in Android can be expensive, particularly if a large number are started at once.
     * Cross fades and other animations involving changes in alpha can be especially expensive.
     * In addition, animations often take substantially longer to run than images take to decode.
     * Gratuitous use of animations in lists and grids can make image loading feel slow and janky.
     * To maximize performance, consider avoiding animations when using Glide to load images into ListViews,
     * GridViews, or RecyclerViews, especially when you expect images to be cached or fast to load most of the time.
     * Instead consider pre-loading so that images are in memory when users scroll to them.
     */
    companion object Builder {
        /**
         * https://github.com/bumptech/glide/issues/1456
         * https://github.com/bumptech/glide/issues/363
         *
         * @return [RequestBuilder]
         */
        fun getThumbRequest(requestManager: RequestManager): RequestBuilder<Drawable> {
            return requestManager
                .asDrawable()
                .dontAnimate() // Disable Cross Fade animation
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .centerCrop()
        }

        fun getFaceCenterCrop(requestManager: RequestManager): RequestBuilder<Drawable> {
            return requestManager
                .asDrawable()
                .dontAnimate() // Disable Cross Fade animation
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .apply(RequestOptions.bitmapTransform(FaceCenterCrop()))
        }

        fun getDownSampleStrategyCenterCropRequest(requestManager: RequestManager): RequestBuilder<Drawable> {
            return requestManager
                .asDrawable()
                .apply(
                    RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .downsample(
                            MultiDownSampleStrategy(
                                GlMaxTextureSizeDownSampleStrategy(),
                                FitOutWidthDownSampleStrategy()
                            )
                        )
                )
                .centerCrop()
        }

        fun getCenterCropRequest(requestManager: RequestManager): RequestBuilder<Drawable> {
            return requestManager
                .asDrawable()
                .dontAnimate() // Disable Cross Fade animation
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .centerCrop()
        }

        fun getCenterCropRequestGif(requestManager: RequestManager): RequestBuilder<GifDrawable> {
            return requestManager
                .asGif()
                .dontAnimate() // Disable Cross Fade animation
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .centerCrop()
        }

        fun getCenterInsideRequest(requestManager: RequestManager): RequestBuilder<Drawable> {
            return requestManager
                .asDrawable()
                .dontAnimate() // Disable Cross Fade animation
                .apply(
                    RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .downsample(
                            MultiDownSampleStrategy(
                                GlMaxTextureSizeDownSampleStrategy(),
                                FitOutWidthDownSampleStrategy()
                            )
                        )
                )
                .centerInside()
        }

        fun getCircleCropRequest(requestManager: RequestManager): RequestBuilder<Drawable> {
            return requestManager
                .asDrawable()
                .dontAnimate() // Disable Cross Fade animation
                .apply(
                    RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .downsample(
                            MultiDownSampleStrategy(
                                GlMaxTextureSizeDownSampleStrategy(),
                                FitOutWidthDownSampleStrategy()
                            )
                        )
                )
                .circleCrop()
        }
    }
}