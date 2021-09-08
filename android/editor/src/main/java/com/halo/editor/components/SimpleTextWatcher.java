/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.editor.components;

import android.text.Editable;
import android.text.TextWatcher;

public abstract class SimpleTextWatcher implements TextWatcher {

  @Override
  public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

  @Override
  public void onTextChanged(CharSequence s, int start, int before, int count) {
    onTextChanged(s.toString());
  }

  @Override
  public void afterTextChanged(Editable s) { }

  public abstract void onTextChanged(String text);
}