/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.photoview;

/**
 * Interface definition for a callback to be invoked when the photo is experiencing a drag event
 */
public interface OnViewDragListener {

    /**
     * Callback for when the photo is experiencing a drag event. This cannot be invoked when the
     * user is scaling.
     *
     * @param dx The change of the coordinates in the x-direction
     * @param dy The change of the coordinates in the y-direction
     */
    void onDrag(float dx, float dy);
}
