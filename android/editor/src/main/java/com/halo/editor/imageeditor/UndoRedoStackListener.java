/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.editor.imageeditor;

public interface UndoRedoStackListener {

  void onAvailabilityChanged(boolean undoAvailable, boolean redoAvailable);
}
