/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.verifition;

import android.view.View;

import androidx.annotation.NonNull;

import com.halo.common.utils.ListenableFuture;
import com.halo.common.utils.SettableFuture;

/**
 * @author ndn
 * Created by ndn
 * Created on 3/22/20
 * com.halo.widget.verifition
 */
public class Animation {
    public static ListenableFuture<Boolean> animateOut(final @NonNull View view,
                                                       final @NonNull android.view.animation.Animation animation,
                                                       final int visibility) {
        final SettableFuture future = new SettableFuture();
        if (view.getVisibility() == visibility) {
            future.set(true);
        } else {
            view.clearAnimation();
            animation.reset();
            animation.setStartTime(0);
            animation.setAnimationListener(new android.view.animation.Animation.AnimationListener() {
                @Override
                public void onAnimationStart(android.view.animation.Animation animation) {
                }

                @Override
                public void onAnimationRepeat(android.view.animation.Animation animation) {
                }

                @Override
                public void onAnimationEnd(android.view.animation.Animation animation) {
                    view.setVisibility(visibility);
                    future.set(true);
                }
            });
            view.startAnimation(animation);
        }
        return future;
    }

    public static void animateIn(final @NonNull View view, final @NonNull android.view.animation.Animation animation) {
        if (view.getVisibility() == View.VISIBLE) return;

        view.clearAnimation();
        animation.reset();
        animation.setStartTime(0);
        view.setVisibility(View.VISIBLE);
        view.startAnimation(animation);
    }
}
