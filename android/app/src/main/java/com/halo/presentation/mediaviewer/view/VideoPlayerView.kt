/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.presentation.mediaviewer.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.hahalolo.player.core.Playback
import com.hahalolo.player.core.Rebinder
import com.hahalolo.player.exoplayer.HaloPlayer
import com.halo.databinding.VideoPlayerViewBinding
import com.halo.widget.R

/**
 * @author admin
 * Created by admin
 * Created on 6/4/20.
 * Package com.halo.presentation.mediaviewer.view
 */
class VideoPlayerView : FrameLayout, Playback.StateListener {

    var ratio: Float = 1F
        set(value) {
            field = value
            invalidate()
            requestLayout()
        }

    var binding: VideoPlayerViewBinding
    private var playback: Playback? = null

    private var videoTag: String = ""

    // Trick here: we do not rely on the actual binding to have the Rebinder. This instance will
    // be useful in some verifications.
    val rebinder: Rebinder?
        get() = this.videoTag?.let { Rebinder(it) }

    val inflater: LayoutInflater by lazy { LayoutInflater.from(context) }

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    @SuppressLint("CustomViewStyleable")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        attrs?.let {
            val a = context.obtainStyledAttributes(
                it,
                R.styleable.RatioGroup
            )
            with(a) {
                ratio = getFloat(
                    R.styleable.RatioGroup_ratio_group,
                    1F
                )
                recycle()
            }
        }

        binding = VideoPlayerViewBinding.inflate(inflater, this, true)
    }

    fun bindVideo(
        url: String?,
        player: HaloPlayer
    ) {
        videoTag = "MediaViewer:$url"
        player.setUp(url?:"") {
            tag = videoTag
            controller = object : Playback.Controller {
                override fun playerCanStart(): Boolean = true
                override fun playerCanPause(): Boolean = true

                override fun setupRenderer(playback: Playback, renderer: Any?) {
                    if (renderer is PlayerView) {
                        renderer.useController = true
                        renderer.setControlDispatcher(player.createControlDispatcher(playback))
                    }
                }
            }
        }.bind(binding.playerView) { pk ->
            pk.addStateListener(this@VideoPlayerView)
            playback = pk
        }
    }

    fun unbind() {
        playback = null
    }
}