/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.sticky_header.util;


import androidx.recyclerview.widget.RecyclerView;

/**
 * Interface for getting the orientation of a RecyclerView from its LayoutManager
 */
public interface OrientationProvider {

  public int getOrientation(RecyclerView recyclerView);

  public boolean isReverseLayout(RecyclerView recyclerView);
}
