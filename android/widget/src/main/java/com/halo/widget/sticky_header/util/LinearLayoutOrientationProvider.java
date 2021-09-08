/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.sticky_header.util;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * OrientationProvider for ReyclerViews who use a LinearLayoutManager
 */
public class LinearLayoutOrientationProvider implements OrientationProvider {

  @Override
  public int getOrientation(RecyclerView recyclerView) {
    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
    throwIfNotLinearLayoutManager(layoutManager);
    return ((LinearLayoutManager) layoutManager).getOrientation();
  }

  @Override
  public boolean isReverseLayout(RecyclerView recyclerView) {
    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
    throwIfNotLinearLayoutManager(layoutManager);
    return ((LinearLayoutManager) layoutManager).getReverseLayout();
  }

  private void throwIfNotLinearLayoutManager(RecyclerView.LayoutManager layoutManager){
    if (!(layoutManager instanceof LinearLayoutManager)) {
      throw new IllegalStateException("StickyListHeadersDecoration can only be used with a " +
          "LinearLayoutManager.");
    }
  }
}
