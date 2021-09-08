/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.res.ResourcesCompat;

import com.halo.common.utils.SizeUtils;
import com.halo.widget.R;

/**
 * @author BuiSon
 * Created by BuiSon
 * Created on 10/23/2018.
 */
public class ShapeImageView extends AppCompatImageView {
    private final static int BODER_WIDTH_DEFAULT = 2;
    private int ovalRadius;
    private float ratioRadius = -1;
    private ShapeType shapeType = ShapeType.RECTANGE;
    private int drawableRes;
    private int boderWidth = BODER_WIDTH_DEFAULT;
    private boolean boder = false;
    private boolean shadow = false;
    private int colorBorder = Color.GRAY;

    public void setShapeType(@NonNull ShapeType shapeType) {
        this.shapeType = shapeType;
        invalidate();
    }

    public void setDrawableRes(int drawableRes) {
        this.drawableRes = drawableRes;
        invalidate();
    }

    public void setOvalRadius(int ovalRadius) {
        this.ovalRadius = ovalRadius;
        invalidate();
    }

    public void setRatioRadius(float ratioRadius) {
        this.ratioRadius = ratioRadius;
        invalidate();
    }

    public ShapeImageView(Context context) {
        super(context);
        initView();
    }

    public ShapeImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttributeSet(attrs);
        initView();
    }

    public ShapeImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttributeSet(attrs);
        initView();
    }

    private void initView() {
        init();
    }

    private void initAttributeSet(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.ShapeImageView, 0, 0);
            try {
                int type = ta.getInt(R.styleable.ShapeImageView_shape_type, 0);
                switch (type) {
                    case 0:
                        shapeType = ShapeType.RECTANGE;
                        break;
                    case 1:
                        shapeType = ShapeType.OVAL;
                        break;
                    case 2:
                        shapeType = ShapeType.CIRCLE;
                        break;
                    case 3:
                        shapeType = ShapeType.DRAWABLE;
                    case 4:
                        shapeType = ShapeType.AVATAR;
                        break;
                }
                ovalRadius = ta.getInt(R.styleable.ShapeImageView_oval_radius, 0);
                ratioRadius = ta.getFloat(R.styleable.ShapeImageView_ratio_radius, -1);
                drawableRes = ta.getResourceId(R.styleable.ShapeImageView_drawable_res, -1);
                boder = ta.getBoolean(R.styleable.ShapeImageView_boder, false);
                shadow = ta.getBoolean(R.styleable.ShapeImageView_shadow, false);
                colorBorder = ta.getColor(R.styleable.ShapeImageView_boder_color, Color.GRAY);
                boderWidth = ta.getInteger(R.styleable.ShapeImageView_boder_width, BODER_WIDTH_DEFAULT);
                if (shapeType == ShapeType.AVATAR) {
                    boder = true;
                    drawableRes = R.drawable.avatar_dwarable;
                }

            } finally {
                ta.recycle();
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        int width = MeasureSpec.getSize(widthMeasureSpec);
//        int height = MeasureSpec.getSize(heightMeasureSpec);
    }

    private Paint paint, maskPaint;

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        maskPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        setWillNotDraw(false);
    }

    @Override
    public void draw(Canvas canvas) {
        if (canvas != null && canvas.getWidth() > 0 && canvas.getHeight() > 0) {
            Bitmap offscreenBitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas offscreenCanvas = new Canvas(offscreenBitmap);
            super.draw(offscreenCanvas);
            switch (shapeType) {
                case DRAWABLE:
                case CIRCLE:
                case OVAL:
                case AVATAR:
                    Bitmap maskBitmap = createMask(canvas.getWidth(), canvas.getHeight());
                    offscreenCanvas.drawBitmap(maskBitmap, 0f, 0, maskPaint);
                    break;
            }
            if (boder && boderWidth > 0) {
                if (shapeType == ShapeType.AVATAR) {
                    drawBoderAvatar(offscreenBitmap, canvas);
                } else {
                    drawShadow(offscreenBitmap, canvas);
                }
            } else {
                paint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
                canvas.drawBitmap(offscreenBitmap, 0f, 0f, paint);
            }
        }
    }

    private void drawBoderAvatar(Bitmap offscreenBitmap, Canvas canvas) {
        if (canvas != null && offscreenBitmap != null) {
            Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.avatar_dwarable, null);
            if (drawable != null) {
                drawable.setBounds(0, 0, offscreenBitmap.getWidth(), offscreenBitmap.getHeight());
                drawable.draw(canvas);
            }
            int width = SizeUtils.dp2px(1.5f);
            paint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
            canvas.drawBitmap(offscreenBitmap, null,
                    new Rect(width, width,
                            offscreenBitmap.getWidth() - width,
                            offscreenBitmap.getHeight() - width),
                    this.paint);
        }
    }

    private void drawShadow(Bitmap offscreenBitmap, Canvas canvas) {

        if (canvas != null && offscreenBitmap != null) {
            Bitmap mask = Bitmap.createBitmap(offscreenBitmap.getWidth(), offscreenBitmap.getHeight(), Bitmap.Config.ALPHA_8);
            Canvas canvasBm = new Canvas(mask);

            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

            int shadowWidth = boderWidth / 2;
            paint.reset();
            paint.setAntiAlias(true);
            paint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
            if (shadow) {
                BlurMaskFilter filter = new BlurMaskFilter(shadowWidth, BlurMaskFilter.Blur.NORMAL);
                paint.setMaskFilter(filter);
            }
            paint.setFilterBitmap(true);
            paint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
            canvasBm.drawBitmap(offscreenBitmap, null,
                    new Rect(shadowWidth,
                            shadowWidth,
                            mask.getWidth() - shadowWidth,
                            mask.getHeight() - shadowWidth),
                    paint);
            paint.reset();
            paint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
            paint.setColor(colorBorder);
            canvas.drawBitmap(mask, 0f, 0f, paint);
            this.paint.reset();
            paint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
            this.paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
            canvas.drawBitmap(offscreenBitmap, null,
                    new Rect(boderWidth,
                            boderWidth,
                            mask.getWidth() - boderWidth,
                            mask.getHeight() - boderWidth), this.paint);
            this.paint.reset();
        }
    }

    private Bitmap createMask(int width, int height) {
        Bitmap mask = Bitmap.createBitmap(width, height, Bitmap.Config.ALPHA_8);
        Canvas canvas = new Canvas(mask);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        paint.setColor(Color.WHITE);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        canvas.drawRect(0, 0, width, height, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        int radius;
        if (width > height) {
            radius = height / 2;
        } else {
            radius = width / 2;
        }
        switch (shapeType) {
            case OVAL:
                if (ratioRadius >= 0 || ratioRadius <= 1) {
                    canvas.drawRoundRect(new RectF(0, 0, width, height), ratioRadius * radius, ratioRadius * radius, paint);
                } else {
                    canvas.drawRoundRect(new RectF(0, 0, width, height), ovalRadius, ovalRadius, paint);
                }
                break;
            case CIRCLE:
                canvas.drawCircle(width / 2, height / 2, radius, paint);
                break;
            case DRAWABLE:
            case AVATAR:
                createDrawable(canvas, paint, width, height);
                break;
        }
        return mask;
    }

    private void createDrawable(Canvas canvas, Paint paint, int width, int height) {
        Bitmap mask = Bitmap.createBitmap(width, height, Bitmap.Config.ALPHA_8);
        Canvas canvasMask = new Canvas(mask);
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), drawableRes, null);
        if (drawable != null) {
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvasMask);
        } else {
            canvasMask.drawColor(Color.WHITE);
        }
        paint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(mask, 0, 0, paint);
    }
}
