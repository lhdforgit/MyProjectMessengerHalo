/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.sticky_header.caching;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Implemented by objects that provide header views for decoration
 */
public interface HeaderProvider {

  /**
   * Will provide a header view for a given position in the RecyclerView
   *
   * @param recyclerView that will display the header
   * @param position     that will be headed by the header
   * @return a header view for the given position and list
   */
  public View getHeader(RecyclerView recyclerView, int position);

  /**
   * TODO: describe this functionality and its necessity
   */
  void invalidate();
}
