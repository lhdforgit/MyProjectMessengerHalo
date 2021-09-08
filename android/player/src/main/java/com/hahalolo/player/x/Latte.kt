/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.player.x

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.media2.widget.VideoView
import com.hahalolo.player.Experiment
import com.hahalolo.player.core.*
import com.hahalolo.player.utils.Capsule

/**
 * [Latte] is an [Engine] for [VideoView]
 */
@Experiment
class Latte private constructor(
  master: Master,
  playableCreator: PlayableCreator<VideoView> = VideoViewPlayableCreator(master),
  private val rendererProviderFactory: RendererProviderFactory = { VideoViewProvider() }
) : Engine<VideoView>(master, playableCreator) {

  private constructor(context: Context) : this(Master[context])

  companion object {

    private val capsule = Capsule(::Latte)

    @JvmStatic
    operator fun get(context: Context) = capsule.get(context)

    @JvmStatic
    operator fun get(fragment: Fragment) = capsule.get(fragment.requireContext())
  }

  override fun prepare(manager: Manager) {
    manager.registerRendererProvider(VideoView::class.java, rendererProviderFactory())
  }

  class Builder(context: Context) {

    private val master = Master[context.applicationContext]

    private var playableCreator: PlayableCreator<VideoView> = VideoViewPlayableCreator(master)

    private var rendererProviderFactory: RendererProviderFactory = { VideoViewProvider() }

    fun setPlayableCreator(playableCreator: PlayableCreator<VideoView>): Builder = apply {
      this.playableCreator = playableCreator
    }

    fun setRendererProviderFactory(factory: RendererProviderFactory): Builder = apply {
      this.rendererProviderFactory = factory
    }

    fun build(): Latte = Latte(
      master, playableCreator, rendererProviderFactory
    ).also {
      master.registerEngine(it)
    }
  }
}
