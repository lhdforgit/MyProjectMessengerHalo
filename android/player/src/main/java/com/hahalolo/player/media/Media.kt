/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.player.media

import android.net.Uri

interface Media {

  val uri: Uri

  val type: String?

  val mediaDrm: MediaDrm?
}
