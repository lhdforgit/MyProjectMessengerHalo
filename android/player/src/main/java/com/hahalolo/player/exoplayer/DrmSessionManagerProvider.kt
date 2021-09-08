/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.player.exoplayer

import com.google.android.exoplayer2.drm.DrmSessionManager
import com.google.android.exoplayer2.drm.ExoMediaCrypto
import com.hahalolo.player.media.Media

/**
 * @author ndn (2018/10/27).
 */
interface DrmSessionManagerProvider {

  fun provideDrmSessionManager(media: Media): DrmSessionManager<ExoMediaCrypto>?
}
