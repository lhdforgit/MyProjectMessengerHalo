/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.player.core

import android.content.Context
import com.hahalolo.player.media.Media

interface BridgeCreator<RENDERER : Any> {

  fun createBridge(
    context: Context,
    media: Media
  ): Bridge<RENDERER>

  fun cleanUp()
}
