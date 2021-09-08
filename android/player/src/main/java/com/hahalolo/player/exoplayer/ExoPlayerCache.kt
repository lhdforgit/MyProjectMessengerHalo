/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.player.exoplayer

import android.content.Context
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.upstream.cache.Cache
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.hahalolo.player.exoplayer.ExoPlayerCache.downloadCacheSingleton
import com.hahalolo.player.exoplayer.ExoPlayerCache.lruCacheSingleton
import com.hahalolo.player.utils.Capsule
import java.io.File

/**
 * A convenient object to help creating and reusing a [Cache] for the media content. It supports
 * a [lruCacheSingleton] which is a [SimpleCache] that uses the [LeastRecentlyUsedCacheEvictor]
 * internally, and a [downloadCacheSingleton] which is a [SimpleCache] that doesn't evict cache,
 * which is useful to store downloaded content.
 */
object ExoPlayerCache {

    private const val CACHE_CONTENT_DIRECTORY = "kohii_content"
    private const val DOWNLOAD_CONTENT_DIRECTORY = "kohii_content_download"
    private const val CACHE_SIZE = 24 * 1024 * 1024L // 24 Megabytes

    private val lruCacheCreator: (Context) -> Cache = { context ->
        SimpleCache(
            File(
                context.getExternalFilesDir(null) ?: context.filesDir,
                CACHE_CONTENT_DIRECTORY
            ),
            LeastRecentlyUsedCacheEvictor(CACHE_SIZE),
            ExoDatabaseProvider(context)
        )
    }

    private val downloadCacheCreator: (Context) -> Cache = { context ->
        SimpleCache(
            File(
                context.getExternalFilesDir(null) ?: context.filesDir,
                DOWNLOAD_CONTENT_DIRECTORY
            ),
            NoOpCacheEvictor(),
            ExoDatabaseProvider(context)
        )
    }

    /**
     * A reusable [Cache] that uses the [LeastRecentlyUsedCacheEvictor] internally.
     *
     * Usage:
     *
     * ```kotlin
     * val cache = ExoPlayerCache.lruCacheSingleton.get(context)
     * ```
     */
    val lruCacheSingleton = Capsule(lruCacheCreator)

    /**
     * A reusable [Cache] that uses the [NoOpCacheEvictor] internally.
     *
     * Usage:
     *
     * ```kotlin
     * val cache = ExoPlayerCache.downloadCacheSingleton.get(context)
     * ```
     */
    val downloadCacheSingleton = Capsule(downloadCacheCreator)
}