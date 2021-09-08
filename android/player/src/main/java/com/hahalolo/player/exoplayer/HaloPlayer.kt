/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.player.exoplayer

import android.content.Context
import androidx.fragment.app.Fragment
import com.google.android.exoplayer2.ControlDispatcher
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.MediaSourceFactory
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.Cache
import com.hahalolo.player.core.*
import com.hahalolo.player.exoplayer.ExoPlayerCache.lruCacheSingleton
import com.hahalolo.player.media.Media
import com.hahalolo.player.utils.Capsule

class HaloPlayer private constructor(
  master: Master,
  playableCreator: PlayableCreator<PlayerView> = PlayerViewPlayableCreator(master),
  private val rendererProviderFactory: RendererProviderFactory = { PlayerViewProvider() }
) : Engine<PlayerView>(master, playableCreator) {

  private constructor(context: Context) : this(Master[context])

  companion object {

    private val capsule = Capsule(::HaloPlayer)

    @JvmStatic // convenient static call for Java
    operator fun get(context: Context) = capsule.get(context)

    @JvmStatic // convenient static call for Java
    operator fun get(fragment: Fragment) = capsule.get(fragment.requireContext())
  }

  override fun prepare(manager: Manager) {
    manager.registerRendererProvider(PlayerView::class.java, rendererProviderFactory())
  }

  /**
   * Creates a [ControlDispatcher] that can be used to setup the renderer when it is a [PlayerView].
   * This method must be used for the [Playback] that supports manual playback control (the
   * [Playback.Config.controller] is not null).
   */
  fun createControlDispatcher(playback: Playback): ControlDispatcher {
    requireNotNull(playback.config.controller) {
      "Playback needs to be setup with a Controller to use this method."
    }

    return DefaultControlDispatcher(playback)
  }

  class Builder(context: Context) {

    private val master = Master[context.applicationContext]

    private var playableCreator: PlayableCreator<PlayerView> =
      PlayerViewPlayableCreator(master)

    private var rendererProviderFactory: RendererProviderFactory = { PlayerViewProvider() }

    fun setPlayableCreator(playableCreator: PlayableCreator<PlayerView>): Builder = apply {
      this.playableCreator = playableCreator
    }

    fun setRendererProviderFactory(factory: RendererProviderFactory): Builder = apply {
      this.rendererProviderFactory = factory
    }

    fun build(): HaloPlayer = HaloPlayer(
      master = master,
      playableCreator = playableCreator,
      rendererProviderFactory = rendererProviderFactory
    ).also {
      master.registerEngine(it)
    }
  }
}

/**
 * Creates a new [HaloPlayer] instance using an [ExoPlayerConfig]. Note that an application should not
 * hold many instance of [HaloPlayer].
 *
 * @param context the [Context].
 * @param config the [ExoPlayerConfig].
 */
fun createHaloPlayer(context: Context, config: ExoPlayerConfig): HaloPlayer {
  val bridgeCreatorFactory: PlayerViewBridgeCreatorFactory = { appContext ->
    val userAgent = Common.getUserAgent(appContext, "Hahalolo")
    val httpDataSource = DefaultHttpDataSourceFactory(userAgent)

    val playerProvider = DefaultExoPlayerProvider(
      appContext,
      clock = config.clock,
      bandwidthMeterFactory = config.createBandwidthMeterFactory(),
      trackSelectorFactory = config.createTrackSelectorFactory(),
      loadControl = config.createLoadControl(),
      renderersFactory = DefaultRenderersFactory(appContext)
        .setEnableDecoderFallback(config.enableDecoderFallback)
        .setAllowedVideoJoiningTimeMs(config.allowedVideoJoiningTimeMs)
        .setExtensionRendererMode(config.extensionRendererMode)
        .setMediaCodecSelector(config.mediaCodecSelector)
        .setPlayClearSamplesWithoutKeys(config.playClearSamplesWithoutKeys)
    )
    val mediaCache: Cache = config.cache ?: lruCacheSingleton.get(context)
    val drmSessionManagerProvider =
      config.drmSessionManagerProvider ?: DefaultDrmSessionManagerProvider(
        appContext, httpDataSource
      )
    val upstreamFactory = DefaultDataSourceFactory(appContext, httpDataSource)
    val mediaSourceFactoryProvider = DefaultMediaSourceFactoryProvider(
      upstreamFactory, drmSessionManagerProvider, mediaCache
    )
    PlayerViewBridgeCreator(playerProvider, mediaSourceFactoryProvider)
  }

  val playableCreator = PlayerViewPlayableCreator.Builder(context.applicationContext)
    .setBridgeCreatorFactory(bridgeCreatorFactory)
    .build()

  return HaloPlayer.Builder(context).setPlayableCreator(playableCreator).build()
}

/**
 * Creates a new [HaloPlayer] instance using a custom [playerCreator], [mediaSourceFactoryCreator] and
 * [rendererProviderFactory]. Note that an application should not hold many instance of [HaloPlayer].
 *
 * @param context the [Context].
 * @param playerCreator the custom creator for the [Player]. If `null`, it will use the default one.
 * @param mediaSourceFactoryCreator the custom creator for the [MediaSourceFactory]. If `null`, it
 * will use the default one.
 * @param rendererProviderFactory the custom [RendererProviderFactory].
 */
@JvmOverloads
fun createHaloPlayer(
  context: Context,
  playerCreator: ((Context) -> Player)? = null,
  mediaSourceFactoryCreator: ((Media) -> MediaSourceFactory)? = null,
  rendererProviderFactory: RendererProviderFactory = { PlayerViewProvider() }
): HaloPlayer {
  val playerProvider = if (playerCreator == null) {
    DefaultExoPlayerProvider(context)
  } else {
    object : RecycledExoPlayerProvider(context) {
      override fun createExoPlayer(context: Context): Player = playerCreator(context)
    }
  }

  val mediaSourceFactoryProvider =
    if (mediaSourceFactoryCreator == null) {
      DefaultMediaSourceFactoryProvider(context)
    } else {
      object : MediaSourceFactoryProvider {
        override fun provideMediaSourceFactory(media: Media): MediaSourceFactory =
          mediaSourceFactoryCreator(media)
      }
    }

  return HaloPlayer.Builder(context)
    .setPlayableCreator(
      PlayerViewPlayableCreator.Builder(context)
        .setBridgeCreatorFactory {
          PlayerViewBridgeCreator(playerProvider, mediaSourceFactoryProvider)
        }
        .build()
    )
    .setRendererProviderFactory(rendererProviderFactory)
    .build()
}
