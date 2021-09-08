/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.player.core

import com.hahalolo.player.core.Playable.Config
import com.hahalolo.player.media.Media

abstract class PlayableCreator<RENDERER : Any>(val rendererType: Class<RENDERER>) {

  abstract fun createPlayable(
    config: Config,
    media: Media
  ): Playable

  abstract fun cleanUp()
}
