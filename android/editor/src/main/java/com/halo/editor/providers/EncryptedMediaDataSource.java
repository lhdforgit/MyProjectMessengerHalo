/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.editor.providers;

import android.media.MediaDataSource;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.halo.editor.attachments.AttachmentSecret;

import java.io.File;

@RequiresApi(23)
public final class EncryptedMediaDataSource {

  public static MediaDataSource createFor(@NonNull AttachmentSecret attachmentSecret, @NonNull File mediaFile, @Nullable byte[] random, long length) {
    if (random == null) {
      return new ClassicEncryptedMediaDataSource(attachmentSecret, mediaFile, length);
    } else {
      return new ModernEncryptedMediaDataSource(attachmentSecret, mediaFile, random, length);
    }
  }

  public static MediaDataSource createForDiskBlob(@NonNull AttachmentSecret attachmentSecret, @NonNull File mediaFile) {
    return new ModernEncryptedMediaDataSource(attachmentSecret, mediaFile, null, mediaFile.length() - 32);
  }
}
