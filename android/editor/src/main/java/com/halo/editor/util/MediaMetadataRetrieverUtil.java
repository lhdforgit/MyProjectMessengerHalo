/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.editor.util;

import android.media.MediaDataSource;
import android.media.MediaMetadataRetriever;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.io.IOException;

public final class MediaMetadataRetrieverUtil {

  private MediaMetadataRetrieverUtil() {}

  /**
   * {@link MediaMetadataRetriever#setDataSource(MediaDataSource)} tends to crash in native code on
   * specific devices, so this just a wrapper to convert that into an {@link IOException}.
   */
  @RequiresApi(23)
  public static void setDataSource(@NonNull MediaMetadataRetriever retriever,
                                   @NonNull MediaDataSource dataSource)
      throws IOException
  {
    try {
      retriever.setDataSource(dataSource);
    } catch (Exception e) {
      throw new IOException(e);
    }
  }
}
