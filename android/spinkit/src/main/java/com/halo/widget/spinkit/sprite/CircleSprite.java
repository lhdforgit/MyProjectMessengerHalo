/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.spinkit.sprite;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Create by ndn
 */
public class CircleSprite extends ShapeSprite {

    @Override
    public ValueAnimator onCreateAnimation() {
        return null;
    }

    @Override
    public void drawShape(Canvas canvas, Paint paint) {
        if (getDrawBounds() != null) {
            int radius = Math.min(getDrawBounds().width(), getDrawBounds().height()) / 2;
            canvas.drawCircle(getDrawBounds().centerX(),
                    getDrawBounds().centerY(),
                    radius, paint);
        }
    }
}
