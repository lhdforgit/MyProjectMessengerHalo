/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.player.exoplayer

import android.content.Context
import com.google.android.exoplayer2.ui.PlayerView
import com.hahalolo.player.core.Bridge
import com.hahalolo.player.core.BridgeCreator
import com.hahalolo.player.media.Media

class PlayerViewBridgeCreator(
  private val playerProvider: ExoPlayerProvider,
  private val mediaSourceFactoryProvider: MediaSourceFactoryProvider
) : BridgeCreator<PlayerView> {

  override fun createBridge(
    context: Context,
    media: Media
  ): Bridge<PlayerView> {
    return PlayerViewBridge(
        context,
        media,
        playerProvider,
        mediaSourceFactoryProvider
    )
  }

  override fun cleanUp() {
    playerProvider.cleanUp()
  }
}
