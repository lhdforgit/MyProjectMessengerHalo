/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.anim;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.view.ViewPropertyAnimator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ViewAnim {

    public static class Builder {
        private long time = 200;
        private boolean alpha = false;
        private boolean show = false;
        @Nullable
        private View view;
        private int translationX = 0;
        private int translationY = 0;

        private int pivotX = 0;
        private int pivotY = 0;
        private float scaleXS = 1;
        private float scaleYS = 1;

        private float scaleXH = 1;
        private float scaleYH = 1;

        private int visibility;

        private Builder(@Nullable View view) {
            this.view = view;
        }

        @NonNull
        public static Builder create(@Nullable View view) {
            return new Builder(view);
        }
        @NonNull
        public static Builder createAl(@Nullable View view) {
            return new Builder(view).alpha(true);
        }
        @NonNull
        public Builder visibility(int visibility){
            this.visibility = visibility;
            return this;
        }
        @NonNull
        public Builder duration(long time) {
            this.time = time;
            return this;
        }
        @NonNull
        public Builder alpha(boolean alpha) {
            this.alpha = alpha;
            return this;
        }
        @NonNull
        public Builder translation(int x, int y) {
            this.translationX = x;
            this.translationY = y;
            return this;
        }
        @NonNull
        public Builder translationX(int x) {
            this.translationX = x;
            return this;
        }
        @NonNull
        public Builder translationY(int y) {
            this.translationY = y;
            return this;
        }
        @NonNull
        public Builder hideDown() {
            this.translationY = view != null ? view.getHeight() : 0;
            return this;
        }
        @NonNull
        public Builder showDown() {
            this.translationY = view != null ? -view.getHeight() : 0;
            return this;
        }
        @NonNull
        public Builder zoomOut() {
            this.pivotX = view != null ? view.getWidth() / 2 : 0;
            this.pivotY = view != null ? view.getHeight() / 2 : 0;
            this.scaleXS = 1f;
            this.scaleYS = 1f;
            this.scaleXH = 0f;
            this.scaleYH = 0f;
            return this;
        }
        @NonNull
        public Builder scale(float pivotX, float pivotY, float scaleFromX, float scaleFromY, float scaleToX, float scaleToY) {
            int width = view != null ? view.getWidth() : 0;
            int height = view != null ? view.getHeight() : 0;
            this.pivotX = (int) (width * pivotX);
            this.pivotY = (int) (height * pivotY);

            this.scaleXS = scaleFromX;
            this.scaleYS = scaleFromY;

            this.scaleXH = scaleToX;
            this.scaleYH = scaleToY;
            return this;
        }

        public void show(boolean show) {
            this.show = show;
            this.build();
        }

        private void build() {
            if (view == null) return;
            if (show) {
                if (view.getVisibility() == View.VISIBLE
                        && view.getTag() instanceof Boolean
                        && (boolean) view.getTag()) return;

                view.setPivotX(pivotX);
                view.setPivotY(pivotY);

                view.setTag(true);
                view.setVisibility(View.VISIBLE);
                view.clearAnimation();
                ViewPropertyAnimator animation = view.animate();
                animation.translationX(0);
                animation.translationY(0);
                animation.scaleX(scaleXS);
                animation.scaleY(scaleYS);
                animation.setDuration(time)
                        .alpha(1f)
                        .alpha(1f)
                        .setListener(null)
                        .start();
            } else {
                int hideType = visibility == View.INVISIBLE || visibility == View.GONE ? visibility : View.GONE;
                view.setTag(false);
                if (view.getVisibility() == hideType) return;
                view.clearAnimation();
                view.setPivotX(pivotX);
                view.setPivotY(pivotY);
                ViewPropertyAnimator animation = view.animate();
                animation.translationX(translationX);
                animation.translationY(translationY);
                animation.scaleX(scaleXH);
                animation.scaleY(scaleYH);
                animation.alpha(alpha ? 0f : 1f)
                        .setDuration(time)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                if (view.getTag() instanceof Boolean && !(boolean) view.getTag()) {
                                    view.setVisibility(hideType);
                                }
                            }
                        });
            }
        }
    }

    private static final long TIME = 200;

    public static void popupTop(View view, boolean show, long time, boolean alpha) {
        if (view == null) return;
        if (show) {
            if (view.getVisibility() == View.VISIBLE
                    && view.getTag() instanceof Boolean
                    && (boolean) view.getTag()) return;
            view.setTag(true);
            view.setVisibility(View.VISIBLE);
            view.clearAnimation();
            view.animate()
                    .translationX(0f)
                    .translationY(0f)
                    .setDuration(time)
                    .alpha(1f)
                    .setListener(null)
                    .start();
        } else {
            view.setTag(false);
            if (view.getVisibility() == View.GONE) return;
            view.clearAnimation();
            view.animate()
                    .translationX(0f)
                    .translationY(view.getHeight())
                    .alpha(alpha ? 0f : 1f)
                    .setDuration(time)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (view.getTag() instanceof Boolean && !(boolean) view.getTag()) {
                                view.setVisibility(View.GONE);
                            }
                        }
                    });
        }
    }

    public static void popupTop(View view, boolean show, long time) {
        popupTop(view, show, time, false);
    }

    public static void popupTop(View view, boolean show) {
        popupTop(view, show, TIME);
    }

    public static void scaleZoom(View view, float x, float y, boolean show, long time) {
        if (view == null) return;

        if (show) {
            if (view.getVisibility() == View.VISIBLE
                    && view.getTag() instanceof Boolean
                    && (boolean) view.getTag()) return;
            view.setTag(true);
            view.setVisibility(View.VISIBLE);
            view.setPivotX(view.getWidth() * x);
            view.setPivotY(view.getHeight() * y);
            view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(time)
                    .setListener(null)
                    .start();
        } else {
            view.setTag(false);
            if (view.getVisibility() == View.GONE) return;
            view.setPivotX(view.getWidth() * x);
            view.setPivotY(view.getHeight() * y);
            view.animate()
                    .scaleX(0f)
                    .scaleY(0f)
                    .setDuration(time)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (view.getTag() instanceof Boolean && !(boolean) view.getTag()) {
                                view.setVisibility(View.GONE);
                            }
                        }
                    });
        }
    }

    public static void scaleZoom(View view, float x, float y, boolean show) {
        scaleZoom(view, x, y, show, TIME);
    }
}
