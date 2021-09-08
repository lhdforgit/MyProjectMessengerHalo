/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.presentation.mediaviewer.drawee;

import android.view.View;

/**
 * @author ndn
 * Created by ndn
 * Created on 8/14/18
 * Interface definition for a callback to be invoked when the Photo is tapped with a single
 * tap.
 */
public interface OnPhotoTapListener {

    /**
     * A callback to receive where the user taps on a photo. You will only receive a callback if
     * the user taps on the actual photo, tapping on 'whitespace' will be ignored.
     *
     * @param view - View the user tapped.
     * @param x    - where the user tapped from the of the Drawable, as percentage of the
     *             Drawable width.
     * @param y    - where the user tapped from the top of the Drawable, as percentage of the
     *             Drawable height.
     */
    void onPhotoTap(View view, float x, float y);
}
