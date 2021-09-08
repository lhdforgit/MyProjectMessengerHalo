/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.editor.permissions;


import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.halo.editor.R;
import com.halo.editor.util.ThemeUtil;
import com.halo.editor.util.ViewUtil;

public class RationaleDialog {

  public static AlertDialog.Builder createFor(@NonNull Context context, @NonNull String message, @DrawableRes int... drawables) {
    View view   = LayoutInflater.from(context).inflate(R.layout.permissions_rationale_dialog, null);
    ViewGroup header = view.findViewById(R.id.header_container);
    TextView text   = view.findViewById(R.id.message);

    for (int i=0;i<drawables.length;i++) {
      Drawable drawable = ContextCompat.getDrawable(context, drawables[i]);
      DrawableCompat.setTint(drawable, ContextCompat.getColor(context, R.color.app_background));
      ImageView imageView = new ImageView(context);
      imageView.setImageDrawable(drawable);
      imageView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

      header.addView(imageView);

      if (i != drawables.length - 1) {
        TextView plus = new TextView(context);
        plus.setText("+");
        plus.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40);
        plus.setTextColor(Color.WHITE);

        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(ViewUtil.dpToPx(context, 20), 0, ViewUtil.dpToPx(context, 20), 0);

        plus.setLayoutParams(layoutParams);
        header.addView(plus);
      }
    }

    text.setText(message);

    return new AlertDialog.Builder(context, ThemeUtil.isDarkTheme(context) ? R.style.RationaleDialogDark : R.style.RationaleDialogLight)
                          .setView(view);
  }

}
