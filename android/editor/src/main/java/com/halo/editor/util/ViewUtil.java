/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.editor.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import com.halo.common.utils.SettableFuture;

public class ViewUtil {
    @SuppressWarnings("deprecation")
    public static void setBackground(final @NonNull View v, final @Nullable Drawable drawable) {
        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
            v.setBackground(drawable);
        } else {
            v.setBackgroundDrawable(drawable);
        }
    }

    public static void setY(final @NonNull View v, final int y) {
        if (VERSION.SDK_INT >= 11) {
            ViewCompat.setY(v, y);
        } else {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            params.topMargin = y;
            v.setLayoutParams(params);
        }
    }

    public static float getY(final @NonNull View v) {
        if (VERSION.SDK_INT >= 11) {
            return ViewCompat.getY(v);
        } else {
            return ((ViewGroup.MarginLayoutParams) v.getLayoutParams()).topMargin;
        }
    }

    public static void setX(final @NonNull View v, final int x) {
        if (VERSION.SDK_INT >= 11) {
            ViewCompat.setX(v, x);
        } else {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            params.leftMargin = x;
            v.setLayoutParams(params);
        }
    }

    public static float getX(final @NonNull View v) {
        if (VERSION.SDK_INT >= 11) {
            return ViewCompat.getX(v);
        } else {
            return ((LayoutParams) v.getLayoutParams()).leftMargin;
        }
    }

    public static void swapChildInPlace(ViewGroup parent, View toRemove, View toAdd, int defaultIndex) {
        int childIndex = parent.indexOfChild(toRemove);
        if (childIndex > -1) parent.removeView(toRemove);
        parent.addView(toAdd, childIndex > -1 ? childIndex : defaultIndex);
    }

    @SuppressWarnings("unchecked")
    public static <T extends View> T inflateStub(@NonNull View parent, @IdRes int stubId) {
        return (T) ((ViewStub) parent.findViewById(stubId)).inflate();
    }

    @SuppressWarnings("unchecked")
    public static <T extends View> T findById(@NonNull View parent, @IdRes int resId) {
        return (T) parent.findViewById(resId);
    }

    @SuppressWarnings("unchecked")
    public static <T extends View> T findById(@NonNull Activity parent, @IdRes int resId) {
        return (T) parent.findViewById(resId);
    }

    public static <T extends View> Stub<T> findStubById(@NonNull Activity parent, @IdRes int resId) {
        return new Stub<T>((ViewStub) parent.findViewById(resId));
    }

    private static Animation getAlphaAnimation(float from, float to, int duration) {
        final Animation anim = new AlphaAnimation(from, to);
        anim.setInterpolator(new FastOutSlowInInterpolator());
        anim.setDuration(duration);
        return anim;
    }

    public static void fadeIn(final @NonNull View view, final int duration) {
        animateIn(view, getAlphaAnimation(0f, 1f, duration));
    }

    public static com.halo.common.utils.ListenableFuture<Boolean> fadeOut(final @NonNull View view, final int duration) {
        return fadeOut(view, duration, View.GONE);
    }

    public static com.halo.common.utils.ListenableFuture<Boolean> fadeOut(@NonNull View view, int duration, int visibility) {
        return animateOut(view, getAlphaAnimation(1f, 0f, duration), visibility);
    }

    public static com.halo.common.utils.ListenableFuture<Boolean> animateOut(final @NonNull View view, final @NonNull Animation animation, final int visibility) {
        final SettableFuture future = new SettableFuture();
        if (view.getVisibility() == visibility) {
            future.set(true);
        } else {
            view.clearAnimation();
            animation.reset();
            animation.setStartTime(0);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    view.setVisibility(visibility);
                    future.set(true);
                }
            });
            view.startAnimation(animation);
        }
        return future;
    }

    public static void animateIn(final @NonNull View view, final @NonNull Animation animation) {
        if (view.getVisibility() == View.VISIBLE) return;

        view.clearAnimation();
        animation.reset();
        animation.setStartTime(0);
        view.setVisibility(View.VISIBLE);
        view.startAnimation(animation);
    }

    @SuppressWarnings("unchecked")
    public static <T extends View> T inflate(@NonNull LayoutInflater inflater,
                                             @NonNull ViewGroup parent,
                                             @LayoutRes int layoutResId) {
        return (T) (inflater.inflate(layoutResId, parent, false));
    }

    @SuppressLint("RtlHardcoded")
    public static void setTextViewGravityStart(final @NonNull TextView textView, @NonNull Context context) {
        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR1) {
            textView.setGravity(Gravity.LEFT);
        }
    }

    public static void mirrorIfRtl(View view, Context context) {
        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR1) {
            view.setScaleX(-1.0f);
        }
    }

    public static int dpToPx(Context context, int dp) {
        return (int) ((dp * context.getResources().getDisplayMetrics().density) + 0.5);
    }

    public static int dpToPx(int dp) {
        return Math.round(dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int dpToSp(int dp) {
        return (int) (dpToPx(dp) / Resources.getSystem().getDisplayMetrics().scaledDensity);
    }

    public static int spToPx(float sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, Resources.getSystem().getDisplayMetrics());
    }

    public static void updateLayoutParams(@NonNull View view, int width, int height) {
        view.getLayoutParams().width = width;
        view.getLayoutParams().height = height;
        view.requestLayout();
    }

    public static int getLeftMargin(@NonNull View view) {
        if (ViewCompat.getLayoutDirection(view) == ViewCompat.LAYOUT_DIRECTION_LTR) {
            return ((ViewGroup.MarginLayoutParams) view.getLayoutParams()).leftMargin;
        }
        return ((ViewGroup.MarginLayoutParams) view.getLayoutParams()).rightMargin;
    }

    public static int getRightMargin(@NonNull View view) {
        if (ViewCompat.getLayoutDirection(view) == ViewCompat.LAYOUT_DIRECTION_LTR) {
            return ((ViewGroup.MarginLayoutParams) view.getLayoutParams()).rightMargin;
        }
        return ((ViewGroup.MarginLayoutParams) view.getLayoutParams()).leftMargin;
    }

    public static void setLeftMargin(@NonNull View view, int margin) {
        if (ViewCompat.getLayoutDirection(view) == ViewCompat.LAYOUT_DIRECTION_LTR) {
            ((ViewGroup.MarginLayoutParams) view.getLayoutParams()).leftMargin = margin;
        } else {
            ((ViewGroup.MarginLayoutParams) view.getLayoutParams()).rightMargin = margin;
        }
        view.forceLayout();
        view.requestLayout();
    }

    public static void setRightMargin(@NonNull View view, int margin) {
        if (ViewCompat.getLayoutDirection(view) == ViewCompat.LAYOUT_DIRECTION_LTR) {
            ((ViewGroup.MarginLayoutParams) view.getLayoutParams()).rightMargin = margin;
        } else {
            ((ViewGroup.MarginLayoutParams) view.getLayoutParams()).leftMargin = margin;
        }
        view.forceLayout();
        view.requestLayout();
    }

    public static void setTopMargin(@NonNull View view, int margin) {
        ((ViewGroup.MarginLayoutParams) view.getLayoutParams()).topMargin = margin;
        view.requestLayout();
    }

    public static void setPaddingTop(@NonNull View view, int padding) {
        view.setPadding(view.getPaddingLeft(), padding, view.getPaddingRight(), view.getPaddingBottom());
    }

    public static void setPaddingBottom(@NonNull View view, int padding) {
        view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), padding);
    }

    public static void setPadding(@NonNull View view, int padding) {
        view.setPadding(padding, padding, padding, padding);
    }

    public static boolean isPointInsideView(@NonNull View view, float x, float y) {
        int[] location = new int[2];

        view.getLocationOnScreen(location);

        int viewX = location[0];
        int viewY = location[1];

        return x > viewX && x < viewX + view.getWidth() &&
                y > viewY && y < viewY + view.getHeight();
    }

    public static int getStatusBarHeight(@NonNull View view) {
        int result = 0;
        int resourceId = view.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = view.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
