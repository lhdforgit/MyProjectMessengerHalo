/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.player.exoplayer

import android.content.Context
import androidx.core.util.Pools
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.util.Util
import com.hahalolo.player.media.Media
import com.hahalolo.player.onEachAcquired
import kotlin.math.max

/**
 * Base implementation of the [ExoPlayerProvider] that uses a [Pools.SimplePool] to store the
 * [Player] instance for reuse.
 *
 * @see DefaultExoPlayerProvider
 */
abstract class RecycledExoPlayerProvider(context: Context) : ExoPlayerProvider {

    companion object {
        // Max number of Player instance are cached in the Pool
        // Magic number: Build.VERSION.SDK_INT / 6 --> API 16 ~ 18 will set pool size to 2, etc.
        internal val MAX_POOL_SIZE = max(Util.SDK_INT / 6, Runtime.getRuntime().availableProcessors())
    }

    private val context = context.applicationContext

    // Cache...
    private val plainPlayerPool = Pools.SimplePool<Player>(MAX_POOL_SIZE)

    /**
     * Create a new [Player] instance, given a [Context] of the Application.
     */
    abstract fun createExoPlayer(context: Context): Player

    override fun acquirePlayer(media: Media): Player {
        val result = plainPlayerPool.acquire() ?: createExoPlayer(context)
        result.playWhenReady = false
        if (result is Player.AudioComponent) {
            result.setAudioAttributes(result.audioAttributes, false)
        }
        return result
    }

    override fun releasePlayer(
        media: Media,
        player: Player
    ) {
        // player.stop(true) // client must stop/do proper cleanup by itself.
        if (!plainPlayerPool.release(player)) {
            // No more space in pool --> this Player has no where to go --> release it.
            player.release()
        }
    }

    override fun cleanUp() {
        plainPlayerPool.onEachAcquired { it.release() }
    }
}