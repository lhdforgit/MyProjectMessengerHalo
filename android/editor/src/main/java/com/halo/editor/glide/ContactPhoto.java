/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.editor.glide;


import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.Key;

import java.io.IOException;
import java.io.InputStream;

public interface ContactPhoto extends Key {

  InputStream openInputStream(Context context) throws IOException;

  @Nullable
  Uri getUri(@NonNull Context context);

  boolean isProfilePhoto();

}
