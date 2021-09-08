/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.imageviewer.common.gestures.detector

import android.view.GestureDetector
import android.view.MotionEvent

internal class SimpleOnGestureListener(
    private val onSingleTap: ((MotionEvent) -> Boolean)? = null,
    private val onDoubleTap: ((MotionEvent) -> Boolean)? = null
) : GestureDetector.SimpleOnGestureListener() {

    override fun onSingleTapConfirmed(event: MotionEvent): Boolean =
        onSingleTap?.invoke(event) ?: false

    override fun onDoubleTap(event: MotionEvent): Boolean =
        onDoubleTap?.invoke(event) ?: false
}