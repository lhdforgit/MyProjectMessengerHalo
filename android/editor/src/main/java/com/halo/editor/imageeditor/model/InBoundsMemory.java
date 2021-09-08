/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.editor.imageeditor.model;

import android.graphics.Matrix;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

final class InBoundsMemory {

  private final Matrix lastGoodUserCrop  = new Matrix();
  private final Matrix lastGoodMainImage = new Matrix();

  void push(@Nullable EditorElement mainImage, @NonNull EditorElement userCrop) {
    if (mainImage == null) {
      lastGoodMainImage.reset();
    } else {
      lastGoodMainImage.set(mainImage.getLocalMatrix());
      lastGoodMainImage.preConcat(mainImage.getEditorMatrix());
    }

    lastGoodUserCrop.set(userCrop.getLocalMatrix());
    lastGoodUserCrop.preConcat(userCrop.getEditorMatrix());
  }

  void restore(@Nullable EditorElement mainImage, @NonNull EditorElement cropEditorElement, @Nullable Runnable invalidate) {
    if (mainImage != null) {
      mainImage.animateLocalTo(lastGoodMainImage, invalidate);
    }
    cropEditorElement.animateLocalTo(lastGoodUserCrop, invalidate);
  }

  Matrix getLastKnownGoodMainImageMatrix() {
    return new Matrix(lastGoodMainImage);
  }

  @Override
  public String toString() {
    return "InBoundsMemory{" +
            "lastGoodUserCrop=" + lastGoodUserCrop +
            ", lastGoodMainImage=" + lastGoodMainImage +
            '}';
  }
}