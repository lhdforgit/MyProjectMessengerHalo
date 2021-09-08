/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.presentation.mediaviewer.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

/**
 * @author ndn
 * Created by ndn
 * Created on 8/14/18
 */
public class MultiTouchViewPager extends ViewPager {

    private boolean isDisallowIntercept, isScrolled = true;

    public MultiTouchViewPager(Context context) {
        super(context);
        setScrollStateListener();
    }

    public MultiTouchViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        setScrollStateListener();
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        try {
            isDisallowIntercept = disallowIntercept;
            super.requestDisallowInterceptTouchEvent(disallowIntercept);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent ev) {
        try {
            if (ev.getPointerCount() > 1 && isDisallowIntercept) {
                requestDisallowInterceptTouchEvent(false);
                boolean handled = super.dispatchTouchEvent(ev);
                requestDisallowInterceptTouchEvent(true);
                return handled;
            } else {
                return super.dispatchTouchEvent(ev);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            if (ev.getPointerCount() > 1) {
                return false;
            } else {
                return super.onInterceptTouchEvent(ev);
            }
        } catch (Exception ex) {
            return false;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            return super.onTouchEvent(ev);
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean isScrolled() {
        return isScrolled;
    }

    private void setScrollStateListener() {
        addOnPageChangeListener(new SimpleOnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int state) {
                isScrolled = state == SCROLL_STATE_IDLE;
            }
        });
    }
}