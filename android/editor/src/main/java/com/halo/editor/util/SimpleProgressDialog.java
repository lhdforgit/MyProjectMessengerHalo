/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.editor.util;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.halo.editor.R;

/**
 * Helper class to show a fullscreen blocking indeterminate progress dialog.
 */
public final class SimpleProgressDialog {

  private SimpleProgressDialog() {}

  public static @NonNull
  AlertDialog show(@NonNull Context context) {
    AlertDialog dialog = new AlertDialog.Builder(context)
                                        .setView(R.layout.progress_dialog)
                                        .setCancelable(false)
                                        .create();
    dialog.show();
    dialog.getWindow().setLayout(context.getResources().getDimensionPixelSize(R.dimen.progress_dialog_size),
                                 context.getResources().getDimensionPixelSize(R.dimen.progress_dialog_size));

    return dialog;
  }
}
