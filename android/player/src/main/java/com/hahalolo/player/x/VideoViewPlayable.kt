/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.player.x

import androidx.core.view.isVisible
import androidx.media2.widget.VideoView
import com.hahalolo.player.core.AbstractPlayable
import com.hahalolo.player.core.Bridge
import com.hahalolo.player.core.Master
import com.hahalolo.player.core.Playback
import com.hahalolo.player.media.Media

internal class VideoViewPlayable(
    master: Master,
    media: Media,
    config: Config,
    bridge: Bridge<VideoView>
) : AbstractPlayable<VideoView>(master, media, config, bridge) {

    override var renderer: Any?
        get() = bridge.renderer
        set(value) {
            require(value is VideoView?)
            bridge.renderer = value
        }

    override fun onRendererAttached(playback: Playback, renderer: Any?) {
        if (renderer is VideoView) {
            renderer.mediaControlView?.isVisible = playback.config.controller != null
        }
        super.onRendererAttached(playback, renderer)
    }
}
