/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.presentation.mediaviewer.drawee;

import android.view.ViewParent;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.DraweeView;

/**
 * @author ndn
 * Created by ndn
 * Created on 8/14/18
 */
class       NonInterceptableAttacher extends Attacher {

    private OnScaleChangeListener scaleChangeListener;

    public NonInterceptableAttacher(DraweeView<GenericDraweeHierarchy> draweeView) {
        super(draweeView);
    }

    @Override
    public void onDrag(float dx, float dy) {
        DraweeView<GenericDraweeHierarchy> draweeView = getDraweeView();
        if (draweeView != null) {
            getDrawMatrix().postTranslate(dx, dy);
            checkMatrixAndInvalidate();

            ViewParent parent = draweeView.getParent();
            if (parent == null) {
                return;
            }

            if (getScale() == 1.0f) {
                parent.requestDisallowInterceptTouchEvent(false);
            } else {
                parent.requestDisallowInterceptTouchEvent(true);
            }
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    public void setOnScaleChangeListener(OnScaleChangeListener listener) {
        this.scaleChangeListener = listener;
    }

    @Override
    public void onScale(float scaleFactor, float focusX, float focusY) {
        super.onScale(scaleFactor, focusX, focusY);
        if (scaleChangeListener != null) {
            scaleChangeListener.onScaleChange(scaleFactor, focusX, focusY);
        }
    }
}