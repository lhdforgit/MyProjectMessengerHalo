/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.player.media

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @author ndn (2018/10/19).
 */
@Parcelize
open class MediaItem(
  override val uri: Uri,
  override val type: String? = null,
  override val mediaDrm: MediaDrm? = null
) : Media, Parcelable {

  constructor(
    url: String,
    type: String? = null,
    mediaDrm: MediaDrm? = null
  ) : this(Uri.parse(url), type, mediaDrm)

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as MediaItem

    if (uri != other.uri) return false
    if (type != other.type) return false
    if (mediaDrm != other.mediaDrm) return false
    return true
  }

  override fun hashCode(): Int {
    var result = uri.hashCode()
    result = 31 * result + (type?.hashCode() ?: 0)
    result = 31 * result + (mediaDrm?.hashCode() ?: 0)
    return result
  }

  override fun toString(): String {
    return "K::Media(uri=$uri, type=$type, mediaDrm=$mediaDrm)"
  }
}
