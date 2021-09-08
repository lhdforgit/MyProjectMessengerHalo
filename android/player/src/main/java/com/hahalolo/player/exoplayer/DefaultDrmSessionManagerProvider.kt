/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.player.exoplayer

import android.content.Context
import android.text.TextUtils
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import com.google.android.exoplayer2.drm.*
import com.google.android.exoplayer2.drm.UnsupportedDrmException.REASON_UNSUPPORTED_SCHEME
import com.google.android.exoplayer2.upstream.HttpDataSource
import com.google.android.exoplayer2.util.Util.getDrmUuid
import com.hahalolo.player.R
import com.hahalolo.player.media.Media
import java.util.*

/**
 * @author ndn (2018/10/27).
 */
class DefaultDrmSessionManagerProvider(
  private val context: Context,
  private val httpDataSourceFactory: HttpDataSource.Factory
) : DrmSessionManagerProvider {

  override fun provideDrmSessionManager(media: Media): DrmSessionManager<ExoMediaCrypto>? {
    val mediaDrm = media.mediaDrm ?: return null
    var drmSessionManager: DrmSessionManager<ExoMediaCrypto>? = null
    var errorStringId = R.string.error_drm_unknown
    var subString: String? = null

    val drmSchemeUuid = getDrmUuid(mediaDrm.type)
    if (drmSchemeUuid == null) {
      errorStringId = R.string.error_drm_unsupported_scheme
    } else {
      try {
        drmSessionManager = buildDrmSessionManagerV18(
            drmSchemeUuid, mediaDrm.licenseUrl,
            mediaDrm.keyRequestPropertiesArray, mediaDrm.multiSession, httpDataSourceFactory
        )
      } catch (e: UnsupportedDrmException) {
        e.printStackTrace()
        errorStringId =
          if (e.reason == REASON_UNSUPPORTED_SCHEME)
            R.string.error_drm_unsupported_scheme
          else
            R.string.error_drm_unknown
        if (e.reason == REASON_UNSUPPORTED_SCHEME) {
          subString = mediaDrm.type
        }
      }
    }

    if (drmSessionManager == null) {
      val error =
        if (TextUtils.isEmpty(subString)) context.getString(errorStringId)
        else "${context.getString(errorStringId)}: $subString"
      Toast.makeText(context, error, LENGTH_SHORT)
          .show()
    }

    return drmSessionManager
  }

  @Throws(UnsupportedDrmException::class)
  private fun buildDrmSessionManagerV18(
    uuid: UUID,
    licenseUrl: String,
    keyRequestProperties: Array<String>?,
    multiSession: Boolean,
    httpDataSourceFactory: HttpDataSource.Factory
  ): DrmSessionManager<ExoMediaCrypto> {
    val drmCallback = HttpMediaDrmCallback(licenseUrl, httpDataSourceFactory)
    if (keyRequestProperties != null) {
      var i = 0
      while (i < keyRequestProperties.size - 1) {
        drmCallback.setKeyRequestProperty(keyRequestProperties[i], keyRequestProperties[i + 1])
        i += 2
      }
    }
    return DefaultDrmSessionManager.Builder()
        .setUuidAndExoMediaDrmProvider(uuid, FrameworkMediaDrm.DEFAULT_PROVIDER)
        .setMultiSession(multiSession)
        .build(drmCallback)
  }
}
