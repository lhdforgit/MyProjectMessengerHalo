/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.editor.imageeditor;

import androidx.annotation.ColorInt;

/**
 * A renderer that can have its color changed.
 * <p>
 * For example, Lines and Text can change color.
 */
public interface ColorableRenderer extends Renderer {

  @ColorInt
  int getColor();

  void setColor(@ColorInt int color);
}
