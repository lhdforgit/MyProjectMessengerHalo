/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.editor.camera;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.io.FileDescriptor;

public interface CameraFragment {

  @SuppressLint("RestrictedApi")
  static Fragment newInstance() {
    if (CameraXUtil.isSupported()) {
      return CameraXFragment.newInstance();
    } else {
      return Camera1Fragment.newInstance();
    }
  }

  interface Controller {
    void onCameraError();
    void onImageCaptured(@NonNull byte[] data, int width, int height);
    void onVideoCaptured(@NonNull FileDescriptor fd);
    void onVideoCaptureError();
    void onGalleryClicked();
    int getDisplayRotation();
    void onCameraCountButtonClicked();
  }
}
