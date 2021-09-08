/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.presentation.mediaviewer.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * @author ndn
 * Created by ndn
 * Created on 8/14/18
 */
public final class AnimationUtils {
    private AnimationUtils() {
        throw new AssertionError();
    }

    public static void animateVisibility(final View view) {
        final boolean isVisible = view.getVisibility() == View.VISIBLE;
        float from = isVisible ? 1.0f : 0.0f,
                to = isVisible ? 0.0f : 1.0f;

        ObjectAnimator animation = ObjectAnimator.ofFloat(view, "alpha", from, to);
        animation.setDuration(ViewConfiguration.getDoubleTapTimeout());

        if (isVisible) {
            animation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    view.setVisibility(View.GONE);
                }
            });
        } else view.setVisibility(View.VISIBLE);

        animation.start();
    }

}
