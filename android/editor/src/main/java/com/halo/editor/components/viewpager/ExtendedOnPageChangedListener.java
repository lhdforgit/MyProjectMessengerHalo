/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.editor.components.viewpager;


import androidx.viewpager.widget.ViewPager;

public abstract class ExtendedOnPageChangedListener implements ViewPager.OnPageChangeListener {

  private Integer currentPage = null;

  @Override
  public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

  }

  @Override
  public void onPageSelected(int position) {
    if (currentPage != null && currentPage != position) onPageUnselected(currentPage);
    currentPage = position;
  }

  public abstract void onPageUnselected(int position);

  @Override
  public void onPageScrollStateChanged(int state) {

  }


}
