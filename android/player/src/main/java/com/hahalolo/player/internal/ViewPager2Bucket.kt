/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.player.internal

import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.hahalolo.player.core.*

internal class ViewPager2Bucket(
  manager: Manager,
  override val root: ViewPager2,
  strategy: Strategy,
  selector: Selector
) : Bucket(manager, root, strategy, selector) {

  private class SimplePageChangeCallback(val manager: Manager) : ViewPager2.OnPageChangeCallback() {
    override fun onPageScrollStateChanged(state: Int) {
      manager.refresh()
    }

    override fun onPageSelected(position: Int) {
      manager.refresh()
    }
  }

  private val pageChangeCallback = SimplePageChangeCallback(manager)

  override fun onAdded() {
    super.onAdded()
    root.registerOnPageChangeCallback(pageChangeCallback)
  }

  override fun onRemoved() {
    super.onRemoved()
    root.unregisterOnPageChangeCallback(pageChangeCallback)
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
    return selectByOrientation(candidates, orientation = root.orientation)
  }
}
