/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.common.utils;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * @author ndn
 * Created by ndn
 * Created on 10/4/18
 * <p>
 * This class implements a hack to change the layout padding on bottom if the keyboard is shown
 * to allow long lists with editTextViews
 * Basic idea for this solution found here: http://stackoverflow.com/a/9108219/325479
 */
public class KeyboardUtilBottomSheet {
    private View decorView;
    private View contentView;

    public KeyboardUtilBottomSheet(Activity act, View contentView) {
        this.decorView = act.getWindow().getDecorView();
        this.contentView = contentView;

        //only required on newer android versions. it was working on API level 19
        if (Build.VERSION.SDK_INT >= 19) {
            decorView.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
        }
    }

    public void enable() {
        if (Build.VERSION.SDK_INT >= 19) {
            decorView.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
        }
    }

    public void disable() {
        if (Build.VERSION.SDK_INT >= 19) {
            decorView.getViewTreeObserver().removeOnGlobalLayoutListener(onGlobalLayoutListener);
        }
    }

    //a small helper to allow showing the editText focus
    ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            Rect r = new Rect();
            //r will be populated with the coordinates of your view that area still visible.
            decorView.getWindowVisibleDisplayFrame(r);

            //get screen height and calculate the difference with the useable area from the r
            int height = decorView.getContext().getResources().getDisplayMetrics().heightPixels;
            int diff = height - r.bottom;

            //if it could be a keyboard add the padding to the view
            if (diff != 0) {
                enablePadding(diff);
            } else {
                resetPadding();
            }
        }
    };

    public void enablePadding(int diff) {
        // if the use-able screen height differs from the total screen height we assume that it shows a keyboard now
        //check if the padding is 0 (if yes set the padding for the keyboard)
        if (contentView.getPaddingBottom() != diff) {
            //set the padding of the contentView for the keyboard
            contentView.setPadding(0, 0, 0, diff);
        }
    }

    public void resetPadding() {
        //check if the padding is != 0 (if yes reset the padding)
        if (contentView.getPaddingBottom() != 0) {
            //reset the padding of the contentView
            contentView.setPadding(0, 0, 0, 0);
        }
    }
}
