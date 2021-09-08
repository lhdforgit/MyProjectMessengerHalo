/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.editor.imageeditor.renderers;

import androidx.annotation.NonNull;

import com.halo.editor.imageeditor.Renderer;
import com.halo.editor.imageeditor.RendererContext;

import java.lang.ref.WeakReference;

/**
 * Maintains a weak reference to the an invalidate callback allowing future invalidation without memory leak risk.
 */
abstract class InvalidateableRenderer implements Renderer {

  private WeakReference<RendererContext.Invalidate> invalidate = new WeakReference<>(null);

  @Override
  public void render(@NonNull RendererContext rendererContext) {
    setInvalidate(rendererContext.invalidate);
  }

  private void setInvalidate(RendererContext.Invalidate invalidate) {
    if (invalidate != this.invalidate.get()) {
      this.invalidate = new WeakReference<>(invalidate);
    }
  }

  protected void invalidate() {
    RendererContext.Invalidate invalidate = this.invalidate.get();
    if (invalidate != null) {
      invalidate.onInvalidate(this);
    }
  }
}
