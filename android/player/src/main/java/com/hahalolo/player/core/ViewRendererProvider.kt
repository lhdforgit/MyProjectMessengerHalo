/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.player.core

import android.view.View
import com.hahalolo.player.media.Media

abstract class ViewRendererProvider @JvmOverloads constructor(
    poolSize: Int = 2
) : RecycledRendererProvider(poolSize) {

    override fun releaseRenderer(
        playback: Playback,
        media: Media,
        renderer: Any?
    ): Boolean {
        if (renderer != null) {
            // View must be removed from its parent before this call.
            require(renderer is View && (renderer.parent == null && !renderer.isAttachedToWindow))
        }
        return super.releaseRenderer(playback, media, renderer)
    }
}
