/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.player.core

import android.view.ViewGroup
import com.hahalolo.player.core.Master.Companion.NO_TAG
import com.hahalolo.player.core.Playable.Config
import com.hahalolo.player.core.Playback.*
import com.hahalolo.player.media.Media
import com.hahalolo.player.media.PlaybackInfo

class Binder(
  private val engine: Engine<*>,
  internal val media: Media
) {

  /**
   * @property initialPlaybackInfo expected initial [PlaybackInfo] for a [Playable] to start when
   * a new [Playback] is bound to it. If null, it will follow the default behavior: an existing
   * [Playable] will keep its current state, a newly created [Playable] receives a new
   * [PlaybackInfo]. This property is not available in the [Rebinder].
   */
  class Options {
    var tag: Any = NO_TAG
    var threshold: Float = 0.65F
    var delay: Int = 0
    var preload: Boolean = false
    var repeatMode: Int = Common.REPEAT_MODE_OFF
    var controller: Controller? = null
    var initialPlaybackInfo: PlaybackInfo? = null
    var artworkHintListener: ArtworkHintListener? = null
    var tokenUpdateListener: TokenUpdateListener? = null
    var networkTypeChangeListener: NetworkTypeChangeListener? = null
    val callbacks = mutableSetOf<Callback>()
  }

  @JvmSynthetic
  @PublishedApi
  internal val options = Options()

  @JvmOverloads
  fun bind(
    container: ViewGroup,
    callback: ((Playback) -> Unit)? = null
  ): Rebinder? {
    val tag = options.tag
    val playable = providePlayable(
      media, tag,
      Config(tag = tag, rendererType = engine.playableCreator.rendererType)
    )
    engine.master.bind(playable, tag, container, options, callback)
    return if (tag != NO_TAG) Rebinder(tag) else null
  }

  private fun providePlayable(
    media: Media,
    tag: Any,
    config: Config
  ): Playable {
    var cache = engine.master.playables.asSequence()
      .filterNot { it.value == NO_TAG } // only care about tagged Playables
      .filter { it.value == tag /* equals */ }
      .firstOrNull()
      ?.key

    if (cache != null) {
      require(cache.media == media) // Playable of same tag must have the same Media data.
      if (cache.config != config /* equals */) {
        // Scenario: client bind a Video of same tag/media but different Renderer type or Config.
        cache.playback = null // will also set Manager to null
        engine.master.tearDown(cache, true)
        cache = null
      }
    }

    return cache ?: engine.playableCreator.createPlayable(config, media)
  }
}
