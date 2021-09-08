/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.player.exoplayer

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.drm.DrmSessionManager
import com.google.android.exoplayer2.ext.rtmp.RtmpDataSourceFactory
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.MediaSourceFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.upstream.cache.Cache
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.hahalolo.player.core.Common
import com.hahalolo.player.exoplayer.ExoPlayerCache.lruCacheSingleton
import com.hahalolo.player.media.Media

/**
 * @author ndn (2018/10/27).
 */
class DefaultMediaSourceFactoryProvider @JvmOverloads constructor(
  dataSourceFactory: DataSource.Factory,
  private val drmSessionManagerProvider: DrmSessionManagerProvider,
  mediaCache: Cache? = null
) : MediaSourceFactoryProvider {

  constructor(context: Context, dataSourceFactory: HttpDataSource.Factory) : this(
    dataSourceFactory = DefaultDataSourceFactory(context, dataSourceFactory),
    drmSessionManagerProvider = DefaultDrmSessionManagerProvider(context, dataSourceFactory),
    mediaCache = lruCacheSingleton.get(context)
  )

  constructor(context: Context) : this(
    context,
    DefaultHttpDataSourceFactory(Common.getUserAgent(context, "Hahalolo"))
  )

  private val dataSourceFactory: DataSource.Factory = if (mediaCache != null) {
    CacheDataSourceFactory(
      mediaCache,
      dataSourceFactory,
      FileDataSource.Factory(), null,
      CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR, null
    )
  } else {
    dataSourceFactory
  }

  override fun provideMediaSourceFactory(media: Media): MediaSourceFactory {
    @C.ContentType val type = Util.inferContentType(media.uri, media.type)

    if (media is HybridMediaItem) {
      return object : MediaSourceFactory {
        override fun getSupportedTypes(): IntArray {
          return intArrayOf(type)
        }

        override fun createMediaSource(uri: Uri?): MediaSource {
          return media.mediaSource
        }

        override fun setDrmSessionManager(drmSessionManager: DrmSessionManager<*>?): MediaSourceFactory {
          return this
        }
      }
    }

    val factory = when (type) {
      C.TYPE_DASH -> DashMediaSource.Factory(dataSourceFactory)
      C.TYPE_SS -> SsMediaSource.Factory(dataSourceFactory)
      C.TYPE_HLS -> HlsMediaSource.Factory(dataSourceFactory)
      C.TYPE_OTHER -> {
        if (inferContentType(media.uri.path ?: "", media.type) == TYPE_RTMP) {
          ProgressiveMediaSource.Factory(RtmpDataSourceFactory())
        } else {
          ProgressiveMediaSource.Factory(dataSourceFactory)
        }
      }
      else -> {
        throw IllegalStateException("Unsupported type: $type")
      }
    }
    val drmSessionManager = drmSessionManagerProvider.provideDrmSessionManager(media)
    if (drmSessionManager != null) factory.setDrmSessionManager(drmSessionManager)
    return factory
  }

  @SuppressLint("WrongConstant")
  fun inferContentType(
    fileName: String,
    overrideExtension: String?
  ): Int {
    val name = Util.toLowerInvariant(fileName)
    return if (name.startsWith("rtmp:")) {
      TYPE_RTMP
    } else {
      inferContentType(Uri.parse(fileName), overrideExtension)
    }
  }

  @C.ContentType
  fun inferContentType(
    uri: Uri,
    overrideExtension: String?
  ): Int {
    return Util.inferContentType(uri, overrideExtension)
  }

  companion object {
    const val TYPE_RTMP = 4
  }
}
