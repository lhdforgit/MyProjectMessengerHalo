/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.player.core

@Suppress("ClassName")
sealed class Strategy : Selector {

  object NO_PLAYER : Strategy() {
    override fun invoke(playbacks: Collection<Playback>): Collection<Playback> {
      return emptyList()
    }
  }

  object SINGLE_PLAYER : Strategy() {
    override fun invoke(playbacks: Collection<Playback>): Collection<Playback> {
      return listOfNotNull(playbacks.firstOrNull())
    }
  }

  object MULTI_PLAYER : Strategy() {
    override fun invoke(playbacks: Collection<Playback>): Collection<Playback> {
      return playbacks
    }
  }
}
