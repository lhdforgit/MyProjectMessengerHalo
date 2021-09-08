/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.player.exoplayer

import com.google.android.exoplayer2.source.MediaSource
import com.hahalolo.player.media.Media

class HybridMediaItem(
    val media: Media,
    val mediaSource: MediaSource
) : Media by media, MediaSource by mediaSource
