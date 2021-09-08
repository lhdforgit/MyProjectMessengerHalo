/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.imageviewer.common.gestures.direction

internal enum class SwipeDirection {
    NOT_DETECTED,
    UP,
    DOWN,
    LEFT,
    RIGHT;

    companion object {
        fun fromAngle(angle: Double): SwipeDirection {
            return when (angle.toInt()) {
                in 0..45 -> RIGHT
                in 45..135 -> UP
                in 135..225 -> LEFT
                in 225..315 -> DOWN
                in 315..360 -> RIGHT
                else -> NOT_DETECTED
            }
        }
    }
}