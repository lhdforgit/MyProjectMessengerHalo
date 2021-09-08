/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.imageviewer.listeners;

import com.halo.widget.imageviewer.ImageViewer;

/**
 * Interface definition for a callback to be invoked when
 * {@link ImageViewer} was dismissed.
 * */
//N.B.! This class is written in Java for convenient use of lambdas due to languages compatibility issues.
public interface OnDismissListener {

    void onDismiss();
}
