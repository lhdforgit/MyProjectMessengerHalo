/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.widget.photoview;

import android.annotation.TargetApi;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.View;

class Compat {

    private static final int SIXTY_FPS_INTERVAL = 1000 / 60;

    public static void postOnAnimation(View view, Runnable runnable) {
        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
            postOnAnimationJellyBean(view, runnable);
        } else {
            view.postDelayed(runnable, SIXTY_FPS_INTERVAL);
        }
    }

    @TargetApi(16)
    private static void postOnAnimationJellyBean(View view, Runnable runnable) {
        view.postOnAnimation(runnable);
    }
}
