/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.editor.camera;

import android.view.Surface;

final class SurfaceRotation {
    /**
     * Get the int value degree of a rotation from the {@link Surface} constants.
     *
     * <p>Valid values for the relative rotation are {@link Surface#ROTATION_0}, {@link
     *      * Surface#ROTATION_90}, {@link Surface#ROTATION_180}, {@link Surface#ROTATION_270}.
     */
    static int rotationDegreesFromSurfaceRotation(int rotationConstant) {
        switch (rotationConstant) {
            case Surface.ROTATION_0:
                return 0;
            case Surface.ROTATION_90:
                return 90;
            case Surface.ROTATION_180:
                return 180;
            case Surface.ROTATION_270:
                return 270;
            default:
                throw new UnsupportedOperationException(
                        "Unsupported surface rotation constant: " + rotationConstant);
        }
    }

    /** Prevents construction */
    private SurfaceRotation() {}
}
