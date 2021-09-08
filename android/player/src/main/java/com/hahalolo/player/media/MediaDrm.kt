/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.player.media

import android.os.Parcelable
import androidx.core.util.ObjectsCompat
import java.util.*

/**
 * Note: implementation of this interface must comparable using all 4 values, no more, no less.
 *
 * @author ndn (2018/06/25).
 */
interface MediaDrm : Comparable<MediaDrm>, Parcelable {

  // DRM Scheme
  val type: String

  val licenseUrl: String

  val keyRequestPropertiesArray: Array<String>?

  val multiSession: Boolean

  override fun compareTo(other: MediaDrm): Int {
    var result = type.compareTo(other.type)
    if (result == 0) {
      result = this.multiSession.compareTo(other.multiSession)
    }

    if (result == 0) {
      result = (if (ObjectsCompat.equals(this.licenseUrl, other.licenseUrl)) 0 else -1)
    }

    if (result == 0) {
      result = if (Arrays.deepEquals(keyRequestPropertiesArray, other.keyRequestPropertiesArray))
        0
      else
        -1
    }

    return result
  }
}
