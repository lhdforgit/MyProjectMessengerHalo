/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.editor.imageeditor;

import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.halo.editor.imageeditor.model.EditorElement;

/**
 * Responsible for rendering a single {@link EditorElement} to the canvas.
 * <p>
 * Because it knows the most about the whereabouts of the image it is also responsible for hit detection.
 */
public interface Renderer extends Parcelable {

  /**
   * Draw self to the context.
   *
   * @param rendererContext The context to draw to.
   */
  void render(@NonNull RendererContext rendererContext);

  /**
   * @param x Local coordinate X
   * @param y Local coordinate Y
   * @return true iff hit.
   */
  boolean hitTest(float x, float y);
}
