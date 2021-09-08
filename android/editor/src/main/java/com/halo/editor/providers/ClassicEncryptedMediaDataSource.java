/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.editor.providers;

import android.media.MediaDataSource;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.halo.editor.attachments.AttachmentSecret;
import com.halo.editor.util.Util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@RequiresApi(23)
final class ClassicEncryptedMediaDataSource extends MediaDataSource {

  private final AttachmentSecret attachmentSecret;
  private final File mediaFile;
  private final long             length;

  ClassicEncryptedMediaDataSource(@NonNull AttachmentSecret attachmentSecret, @NonNull File mediaFile, long length) {
    this.attachmentSecret = attachmentSecret;
    this.mediaFile        = mediaFile;
    this.length           = length;
  }

  @Override
  public int readAt(long position, byte[] bytes, int offset, int length) throws IOException {
    try (InputStream inputStream = ClassicDecryptingPartInputStream.createFor(attachmentSecret, mediaFile)) {
      byte[] buffer          = new byte[4096];
      long   headerRemaining = position;

      while (headerRemaining > 0) {
        int read = inputStream.read(buffer, 0, Util.toIntExact(Math.min((long)buffer.length, headerRemaining)));

        if (read == -1) return -1;

        headerRemaining -= read;
      }

      return inputStream.read(bytes, offset, length);
    }
  }

  @Override
  public long getSize() {
    return length;
  }

  @Override
  public void close() {}
}
