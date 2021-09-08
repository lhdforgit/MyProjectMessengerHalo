/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.sticker;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.JELLY_BEAN;

/**
 * @author ngannd
 * Create by ngannd on 19/12/2018
 */
public final class Utils {
    static final int DONT_UPDATE_FLAG = -1;

    @TargetApi(JELLY_BEAN)
    public static void removeOnGlobalLayoutListener(final View v, final ViewTreeObserver.OnGlobalLayoutListener listener) {
        if (SDK_INT < JELLY_BEAN) {
            //noinspection deprecation
            v.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
        } else {
            v.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        }
    }

    static int dpToPx(@NonNull final Context context, final float dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }

    public static int screenHeight(@NonNull final Activity context) {
        final Point size = new Point();

        context.getWindowManager().getDefaultDisplay().getSize(size);

        return size.y;
    }

    @NonNull
    static Point locationOnScreen(@NonNull final View view) {
        final int[] location = new int[2];
        view.getLocationOnScreen(location);
        return new Point(location[0], location[1]);
    }

    @NonNull
    public static Rect windowVisibleDisplayFrame(@NonNull final Activity context) {
        final Rect result = new Rect();
        context.getWindow().getDecorView().getWindowVisibleDisplayFrame(result);
        return result;
    }

    public static Activity asActivity(@NonNull final Context context) {
        Context result = context;

        while (result instanceof ContextWrapper) {
            if (result instanceof Activity) {
                return (Activity) result;
            }

            result = ((ContextWrapper) result).getBaseContext();
        }

        throw new IllegalArgumentException("The passed Context is not an Activity.");
    }

    static void fixPopupLocation(@NonNull final PopupWindow popupWindow, @NonNull final Point desiredLocation) {
        popupWindow.getContentView().post(new Runnable() {
            @Override
            public void run() {
                final Point actualLocation = locationOnScreen(popupWindow.getContentView());

                if (!(actualLocation.x == desiredLocation.x && actualLocation.y == desiredLocation.y)) {
                    final int differenceX = actualLocation.x - desiredLocation.x;
                    final int differenceY = actualLocation.y - desiredLocation.y;

                    final int fixedOffsetX;
                    final int fixedOffsetY;

                    if (actualLocation.x > desiredLocation.x) {
                        fixedOffsetX = desiredLocation.x - differenceX;
                    } else {
                        fixedOffsetX = desiredLocation.x + differenceX;
                    }

                    if (actualLocation.y > desiredLocation.y) {
                        fixedOffsetY = desiredLocation.y - differenceY;
                    } else {
                        fixedOffsetY = desiredLocation.y + differenceY;
                    }

                    popupWindow.update(fixedOffsetX, fixedOffsetY, DONT_UPDATE_FLAG, DONT_UPDATE_FLAG);
                }
            }
        });
    }

    private Utils() {
        throw new AssertionError("No instances.");
    }
}

