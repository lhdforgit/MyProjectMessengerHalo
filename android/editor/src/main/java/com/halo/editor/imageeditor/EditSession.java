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

/**
 * Represents an underway edit of the image.
 * <p>
 * Accepts new touch positions, new touch points, released touch points and when complete can commit the edit.
 * <p>
 * Examples of edit session implementations are, Drag, Draw, Resize:
 * <p>
 * {@link ElementDragEditSession} for dragging with a single finger.
 * {@link ElementScaleEditSession} for resize/dragging with two fingers.
 * {@link DrawingSession} for drawing with a single finger.
 */
interface EditSession {

  void movePoint(int p, @NonNull PointF point);

  EditorElement getSelected();

  EditSession newPoint(@NonNull Matrix newInverse, @NonNull PointF point, int p);

  EditSession removePoint(@NonNull Matrix newInverse, int p);

  void commit();
}
