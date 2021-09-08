/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.editor.util;

import android.content.Context;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;

import androidx.annotation.ArrayRes;
import androidx.annotation.AttrRes;
import androidx.annotation.DimenRes;
import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;

public class ResUtil {

  public static int getColor(Context context, @AttrRes int attr) {
    final TypedArray styledAttributes = context.obtainStyledAttributes(new int[]{attr});
    final int        result           = styledAttributes.getColor(0, -1);
    styledAttributes.recycle();
    return result;
  }

  public static int getDrawableRes(Context c, @AttrRes int attr) {
    return getDrawableRes(c.getTheme(), attr);
  }

  public static int getDrawableRes(Theme theme, @AttrRes int attr) {
    final TypedValue out = new TypedValue();
    theme.resolveAttribute(attr, out, true);
    return out.resourceId;
  }

  public static Drawable getDrawable(Context c, @AttrRes int attr) {
    return AppCompatResources.getDrawable(c, getDrawableRes(c, attr));
  }

  public static int[] getResourceIds(Context c, @ArrayRes int array) {
    final TypedArray typedArray  = c.getResources().obtainTypedArray(array);
    final int[]      resourceIds = new int[typedArray.length()];
    for (int i = 0; i < typedArray.length(); i++) {
      resourceIds[i] = typedArray.getResourceId(i, 0);
    }
    typedArray.recycle();
    return resourceIds;
  }

  public static float getFloat(@NonNull Context context, @DimenRes int resId) {
    TypedValue value = new TypedValue();
    context.getResources().getValue(resId, value, true);
    return value.getFloat();
  }
}
