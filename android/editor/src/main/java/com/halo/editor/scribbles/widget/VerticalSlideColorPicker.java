/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.editor.scribbles.widget;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.halo.editor.R;

public class VerticalSlideColorPicker extends View {

  private static final float INDICATOR_TO_BAR_WIDTH_RATIO = 0.5f;

  private Paint paint;
  private Paint strokePaint;
  private Paint indicatorStrokePaint;
  private Paint indicatorFillPaint;
  private Path path;
  private Bitmap bitmap;
  private Canvas bitmapCanvas;

  private int     viewWidth;
  private int     viewHeight;
  private int     centerX;
  private float   colorPickerRadius;
  private RectF colorPickerBody;

  private OnColorChangeListener onColorChangeListener;

  private int     borderColor;
  private float   borderWidth;
  private float   indicatorRadius;
  private int[]   colors;

  private int     touchY;
  private int     activeColor;

  public VerticalSlideColorPicker(Context context) {
    super(context);
    init();
  }

  public VerticalSlideColorPicker(Context context, AttributeSet attrs) {
    super(context, attrs);

    TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.VerticalSlideColorPicker, 0, 0);

    try {
      int colorsResourceId = a.getResourceId(R.styleable.VerticalSlideColorPicker_pickerColors, R.array.scribble_colors);

      colors          = a.getResources().getIntArray(colorsResourceId);
      borderColor     = a.getColor(R.styleable.VerticalSlideColorPicker_pickerBorderColor, Color.WHITE);
      borderWidth     = a.getDimension(R.styleable.VerticalSlideColorPicker_pickerBorderWidth, 10f);

    } finally {
      a.recycle();
    }

    init();
  }

  public VerticalSlideColorPicker(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public VerticalSlideColorPicker(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init();
  }

  private void init() {
    setWillNotDraw(false);

    paint = new Paint();
    paint.setStyle(Paint.Style.FILL);
    paint.setAntiAlias(true);

    path = new Path();

    strokePaint = new Paint();
    strokePaint.setStyle(Paint.Style.STROKE);
    strokePaint.setColor(borderColor);
    strokePaint.setAntiAlias(true);
    strokePaint.setStrokeWidth(borderWidth);

    indicatorStrokePaint = new Paint(strokePaint);
    indicatorStrokePaint.setStrokeWidth(borderWidth / 2);

    indicatorFillPaint = new Paint();
    indicatorFillPaint.setStyle(Paint.Style.FILL);
    indicatorFillPaint.setAntiAlias(true);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    path.addCircle(centerX, borderWidth + colorPickerRadius + indicatorRadius, colorPickerRadius, Path.Direction.CW);
    path.addRect(colorPickerBody, Path.Direction.CW);
    path.addCircle(centerX, viewHeight - (borderWidth + colorPickerRadius + indicatorRadius), colorPickerRadius, Path.Direction.CW);

    bitmapCanvas.drawColor(Color.TRANSPARENT);

    bitmapCanvas.drawPath(path, strokePaint);
    bitmapCanvas.drawPath(path, paint);

    canvas.drawBitmap(bitmap, 0, 0, null);

    touchY = Math.max((int) colorPickerBody.top, touchY);

    indicatorFillPaint.setColor(activeColor);
    canvas.drawCircle(centerX, touchY, indicatorRadius, indicatorFillPaint);
    canvas.drawCircle(centerX, touchY, indicatorRadius, indicatorStrokePaint);
  }

  @SuppressLint("ClickableViewAccessibility")
  @Override
  public boolean onTouchEvent(MotionEvent event) {
    touchY = (int) Math.min(event.getY(), colorPickerBody.bottom);
    touchY = (int) Math.max(colorPickerBody.top, touchY);

    activeColor = bitmap.getPixel(viewWidth/2, touchY);

    if (onColorChangeListener != null) {
      onColorChangeListener.onColorChange(activeColor);
    }

    invalidate();

    return true;
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);

    viewWidth  = w;
    viewHeight = h;

    if (viewWidth <= 0 || viewHeight <= 0) return;

    int barWidth = (int) (viewWidth * INDICATOR_TO_BAR_WIDTH_RATIO);

    centerX           = viewWidth / 2;
    indicatorRadius   = (viewWidth / 2) - borderWidth;
    colorPickerRadius = (barWidth / 2) - borderWidth;

    colorPickerBody   = new RectF(centerX - colorPickerRadius,
                                  borderWidth + colorPickerRadius + indicatorRadius,
                                  centerX + colorPickerRadius,
                                  viewHeight - (borderWidth + colorPickerRadius + indicatorRadius));

    LinearGradient gradient = new LinearGradient(0, colorPickerBody.top, 0, colorPickerBody.bottom, colors, null, Shader.TileMode.CLAMP);
    paint.setShader(gradient);

    if (bitmap != null) {
      bitmap.recycle();
    }

    bitmap       = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_8888);
    bitmapCanvas = new Canvas(bitmap);
  }

  public void setBorderColor(int borderColor) {
    this.borderColor = borderColor;
    invalidate();
  }

  public void setBorderWidth(float borderWidth) {
    this.borderWidth = borderWidth;
    invalidate();
  }

  public void setColors(int[] colors) {
    this.colors = colors;
    invalidate();
  }

  public void setActiveColor(int color) {
    activeColor = color;

    if (colorPickerBody != null) {
      touchY = (int) colorPickerBody.top;
    }

    if (onColorChangeListener != null) {
      onColorChangeListener.onColorChange(color);
    }

    invalidate();
  }

  public int getActiveColor() {
    return activeColor;
  }

  public void setOnColorChangeListener(OnColorChangeListener onColorChangeListener) {
    this.onColorChangeListener = onColorChangeListener;
  }

  public interface OnColorChangeListener {
    void onColorChange(int selectedColor);
  }
}