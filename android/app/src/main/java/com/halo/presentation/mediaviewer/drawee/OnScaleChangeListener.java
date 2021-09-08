/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.presentation.mediaviewer.drawee;

/**
 * @author ndn
 * Created by ndn
 * Created on 8/14/18
 * Interface definition for callback to be invoked when attached ImageView scale changes
 */
public interface OnScaleChangeListener {
    /**
     * Callback for when the scale changes
     *
     * @param scaleFactor the scale factor
     * @param focusX      focal point X position
     * @param focusY      focal point Y position
     */
    void onScaleChange(float scaleFactor, float focusX, float focusY);
}
