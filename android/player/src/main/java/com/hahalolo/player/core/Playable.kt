/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.player.core

import com.hahalolo.player.internal.DynamicFragmentRendererPlayback
import com.hahalolo.player.internal.DynamicViewRendererPlayback
import com.hahalolo.player.internal.StaticViewRendererPlayback
import com.hahalolo.player.media.Media
import com.hahalolo.player.media.PlaybackInfo
import com.hahalolo.player.media.VolumeInfo

/**
 * A [Playable] contains necessary information about a [Media] item, and the config provided by
 * client for it to start the media with expected result. Instance of [Playable] is provided by
 * [PlayableCreator].
 *
 * An implementation of [Playable] must take into account about the following points:
 *
 * - A [Playable] is managed at application scope. So it must not retain any reference to narrower
 * scope like [androidx.fragment.app.Fragment] or [android.app.Activity].
 * - A [Playable] must acknowledge about configuration changes. It must return a correct value
 * from [onConfigChange] so that the system can provide proper resource management. For example if
 * any of its resource needs to be reset at configuration change, its [onConfigChange] method must
 * return `false`.
 *
 * [AbstractPlayable] is a base implementation that leverage actual playback logic to a [Bridge].
 *
 * @see [AbstractPlayable]
 * @see [AbstractBridge]
 * @see [PlayableCreator]
 * @see [Engine]
 */
abstract class Playable(
  val media: Media,
  internal val config: Config
) {

  data class Config(
    internal val tag: Any = Master.NO_TAG,
    internal val rendererType: Class<*>
  )

  abstract val tag: Any

  abstract var renderer: Any?

  internal abstract var playback: Playback?

  internal abstract var manager: PlayableManager?

  internal abstract val playerState: Int

  internal abstract var playbackInfo: PlaybackInfo

  abstract fun onPrepare(loadSource: Boolean)

  // Ensure the preparation for the playback
  abstract fun onReady()

  abstract fun onPlay()

  abstract fun onPause()

  abstract fun onReset()

  abstract fun onRelease()

  abstract fun isPlaying(): Boolean

  abstract fun onUnbind(playback: Playback)

  /**
   * Return `true` to indicate that this Playable would survive configuration changes and no
   * playback reloading would be required. In special cases like YouTube playback, it is recommended
   * to return `false` so halo will handle the resource recycling correctly.
   */
  abstract fun onConfigChange(): Boolean

  /**
   * Once the Playback finds it is good time for the Playable to request/release the Renderer, it
   * will trigger these calls to send that signal. The 'good time' can varies due to the actual
   * use case. In halo, there are 2 following cases:
   * - The Playback's Container is also the renderer. In this case, the Container/Renderer will
   * always be there. We suggest that the Playable should request for the Renderer as soon as
   * possible, and release it as late as possible. The proper place to do that are when the
   * Playback becomes active (onActive()) and inactive (onInActive()).
   *
   * @see [StaticViewRendererPlayback]
   *
   * - The Playback's Container is not the Renderer. In this case, the Renderer is expected
   * to be created on demand and release as early as possible, so that halo can reuse it for
   * other Playback as soon as possible. We suggest that the Playable should request for
   * the Renderer just right before the Playback starts (onPlay()), and release the Renderer
   * just right after the Playback pauses (onPause()).
   *
   * Flow:  Playable#setupRenderer(playback)
   *          ???
   *        If Bridge<RENDERER> needs a renderer
   *          ???
   *        Manager#requestRenderer(playback, playable)
   *          ???
   *        Playback#attachRenderer(renderer)
   *          ???
   *        Playback#onAttachRenderer(renderer)
   *          ???
   *        If valid renderer returns, do the update for Bridge<RENDERER>
   *
   * @see [DynamicViewRendererPlayback]
   * @see [DynamicFragmentRendererPlayback]
   */
  abstract fun setupRenderer(playback: Playback)

  /**
   * Once the Playback finds it is good time for the Playable to request/release the Renderer, it
   * will trigger these calls to send that signal. The 'good time' can varies due to the actual
   * use case. In halo, there are 2 following cases:
   * - The Playback's Container is also the renderer. In this case, the Container/Renderer will
   * always be there. We suggest that the Playable should request for the Renderer as soon as
   * possible, and release it as late as possible. The proper place to do that are when the
   * Playback becomes active (onActive()) and inactive (onInActive()).
   *
   * @see [StaticViewRendererPlayback]
   *
   * - The Playback's Container is not the Renderer. In this case, the Renderer is expected
   * to be created on demand and release as early as possible, so that halo can reuse it for
   * other Playback as soon as possible. We suggest that the Playable should request for
   * the Renderer just right before the Playback starts (onPlay()), and release the Renderer
   * just right after the Playback pauses (onPause()).
   *
   * Flow:  Playable#teardownRenderer(playback)
   *          ???
   *        If Bridge<RENDERER> has a renderer to release
   *          ???
   *        Manager#releaseRenderer(playback, playable)
   *          ???
   *        Playback#detachRenderer(renderer)
   *          ???
   *        Playback#onDetachRenderer(renderer)
   *          ???
   *        If the renderer is managed by pool, it will now be released back to the pool for reuse.
   *          ???
   *        Clear the renderer in Bridge<RENDERER>
   *
   * @see [DynamicViewRendererPlayback]
   * @see [DynamicFragmentRendererPlayback]
   */
  abstract fun teardownRenderer(playback: Playback)

  internal abstract fun onPlaybackPriorityChanged(
    playback: Playback,
    oldPriority: Int,
    newPriority: Int
  )

  internal abstract fun onVolumeInfoChanged(
    playback: Playback,
    from: VolumeInfo,
    to: VolumeInfo
  )

  internal abstract fun onNetworkTypeChanged(
    from: NetworkType,
    to: NetworkType
  )
}
