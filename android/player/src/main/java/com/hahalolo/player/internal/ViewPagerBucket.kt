/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.player.internal

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.hahalolo.player.core.*

internal class ViewPagerBucket(
  manager: Manager,
  override val root: ViewPager,
  strategy: Strategy,
  selector: Selector
) : Bucket(manager, root, strategy, selector), OnPageChangeListener {

  override fun onAdded() {
    super.onAdded()
    root.addOnPageChangeListener(this)
  }

  override fun onRemoved() {
    super.onRemoved()
    root.removeOnPageChangeListener(this)
  }

  override fun onPageScrollStateChanged(state: Int) {
    manager.refresh()
  }

  override fun onPageScrolled(
    position: Int,
    positionOffset: Float,
    positionOffsetPixels: Int
  ) {
    // Do nothing
  }

  override fun onPageSelected(position: Int) {
    manager.refresh()
  }

  override fun accepts(container: ViewGroup): Boolean {
    var view = container as View
    var parent = view.parent
    while (parent != null && parent !== this.root && parent is View) {
      view = parent
      parent = view.parent
    }
    return parent === this.root
  }

  override fun selectToPlay(candidates: Collection<Playback>): Collection<Playback> {
    return selectByOrientation(candidates, orientation = HORIZONTAL)
  }
}
