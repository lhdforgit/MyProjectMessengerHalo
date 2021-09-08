/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.editor.util;


import android.view.ViewStub;

import androidx.annotation.NonNull;

public class Stub<T> {

  private ViewStub viewStub;
  private T view;

  public Stub(@NonNull ViewStub viewStub) {
    this.viewStub = viewStub;
  }

  public T get() {
    if (view == null) {
      view = (T)viewStub.inflate();
      viewStub = null;
    }

    return view;
  }

  public boolean resolved() {
    return view != null;
  }

}
