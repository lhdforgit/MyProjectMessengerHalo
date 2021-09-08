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
 * Interface definition for a callback to be invoked when the ImageView is tapped with a single
 * tap.
 */
public interface OnViewTapListener {
    /**
     * A callback to receive where the user taps on a ImageView. You will receive a callback if
     * the user taps anywhere on the view, tapping on 'whitespace' will not be ignored.
     *
     * @param view - View the user tapped.
     * @param x    - where the user tapped from the left of the View.
     * @param y    - where the user tapped from the top of the View.
     */
    void onViewTap(View view, float x, float y);
}