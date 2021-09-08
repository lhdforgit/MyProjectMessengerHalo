/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.player.exoplayer

import android.content.Context
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.Cache
import com.hahalolo.player.core.*
import com.hahalolo.player.core.Playable.Config
import com.hahalolo.player.exoplayer.ExoPlayerCache.lruCacheSingleton
import com.hahalolo.player.media.Media
import kotlin.LazyThreadSafetyMode.NONE

typealias PlayerViewBridgeCreatorFactory = (Context) -> BridgeCreator<PlayerView>

class PlayerViewPlayableCreator internal constructor(
  private val master: Master,
  private val bridgeCreatorFactory: PlayerViewBridgeCreatorFactory = defaultBridgeCreatorFactory
) : PlayableCreator<PlayerView>(PlayerView::class.java) {

  constructor(context: Context) : this(Master[context.applicationContext])

  companion object {

    // Only pass Application to this method.
    private val defaultBridgeCreatorFactory: PlayerViewBridgeCreatorFactory = { context ->
      val userAgent = Common.getUserAgent(context, "Hahalolo")
      val httpDataSource = DefaultHttpDataSourceFactory(userAgent)

      // ExoPlayerProvider
      val playerProvider: ExoPlayerProvider = DefaultExoPlayerProvider(
        context,
        bandwidthMeterFactory = DefaultBandwidthMeterFactory()
      )

      // MediaSourceFactoryProvider
      val mediaCache: Cache = lruCacheSingleton.get(context)
      val upstreamFactory = DefaultDataSourceFactory(context, httpDataSource)
      val drmSessionManagerProvider = DefaultDrmSessionManagerProvider(context, httpDataSource)
      val mediaSourceFactoryProvider: MediaSourceFactoryProvider =
        DefaultMediaSourceFactoryProvider(upstreamFactory, drmSessionManagerProvider, mediaCache)
      PlayerViewBridgeCreator(playerProvider, mediaSourceFactoryProvider)
    }
  }

  private val bridgeCreator: Lazy<BridgeCreator<PlayerView>> = lazy(NONE) {
    bridgeCreatorFactory(master.app)
  }

  override fun createPlayable(
    config: Config,
    media: Media
  ): Playable {
    return PlayerViewPlayable(
      master,
      media,
      config,
      bridgeCreator.value.createBridge(master.app, media)
    )
  }

  override fun cleanUp() {
    if (bridgeCreator.isInitialized()) bridgeCreator.value.cleanUp()
  }

  class Builder(context: Context) {

    private val app = context.applicationContext

    private var bridgeCreatorFactory: PlayerViewBridgeCreatorFactory = defaultBridgeCreatorFactory

    fun setBridgeCreatorFactory(factory: PlayerViewBridgeCreatorFactory): Builder = apply {
      this.bridgeCreatorFactory = factory
    }

    fun build(): PlayableCreator<PlayerView> = PlayerViewPlayableCreator(
      Master[app],
      bridgeCreatorFactory
    )
  }
}
