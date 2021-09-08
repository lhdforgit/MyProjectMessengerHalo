/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.editor.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.halo.editor.R;

public class OutlinedThumbnailView extends ThumbnailView {

    private CornerMask cornerMask;
    private Outliner outliner;

    public OutlinedThumbnailView(Context context) {
        super(context);
        init(null);
    }

    public OutlinedThumbnailView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        cornerMask = new CornerMask(this);
        outliner = new Outliner();

        int radius = 0;
        int color = Color.parseColor("#E0E0E0");

        try {
            if (attrs != null) {
                TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.OutlinedThumbnailView, 0, 0);
                radius = typedArray.getDimensionPixelOffset(R.styleable.OutlinedThumbnailView_otv_cornerRadius, 0);
                color = typedArray.getColor(R.styleable.OutlinedThumbnailView_otv_borderColor, Color.parseColor("#E0E0E0"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        outliner.setColor(color);

        setRadius(radius);
        setCorners(radius, radius, radius, radius);

        setWillNotDraw(false);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        cornerMask.mask(canvas);
        outliner.draw(canvas);
    }

    public void setCorners(int topLeft, int topRight, int bottomRight, int bottomLeft) {
        cornerMask.setRadii(topLeft, topRight, bottomRight, bottomLeft);
        outliner.setRadii(topLeft, topRight, bottomRight, bottomLeft);
        postInvalidate();
    }
}
