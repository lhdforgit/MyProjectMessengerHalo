/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.editor.imageeditor;

import android.graphics.Matrix;
import android.graphics.PointF;

import androidx.annotation.NonNull;

import com.halo.editor.imageeditor.model.EditorElement;
import com.halo.editor.imageeditor.renderers.BezierDrawingRenderer;

/**
 * Passes touch events into a {@link BezierDrawingRenderer}.
 */
class DrawingSession extends ElementEditSession {

  private final BezierDrawingRenderer renderer;

  private DrawingSession(@NonNull EditorElement selected, @NonNull Matrix inverseMatrix, @NonNull BezierDrawingRenderer renderer) {
    super(selected, inverseMatrix);
    this.renderer = renderer;
  }

  public static EditSession start(EditorElement element, BezierDrawingRenderer renderer, Matrix inverseMatrix, PointF point) {
    DrawingSession drawingSession = new DrawingSession(element, inverseMatrix, renderer);
    drawingSession.setScreenStartPoint(0, point);
    renderer.setFirstPoint(drawingSession.startPointElement[0]);
    return drawingSession;
  }

  @Override
  public void movePoint(int p, @NonNull PointF point) {
    if (p != 0) return;
    setScreenEndPoint(p, point);
    renderer.addNewPoint(endPointElement[0]);
  }

  @Override
  public EditSession newPoint(@NonNull Matrix newInverse, @NonNull PointF point, int p) {
    return this;
  }

  @Override
  public EditSession removePoint(@NonNull Matrix newInverse, int p) {
    return this;
  }
}
