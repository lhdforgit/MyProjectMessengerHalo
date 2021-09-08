/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.player.internal

import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.core.widget.NestedScrollView.OnScrollChangeListener
import com.hahalolo.player.core.*

internal class NestedScrollViewBucket(
  manager: Manager,
  override val root: NestedScrollView,
  strategy: Strategy,
  selector: Selector
) : Bucket(manager, root, strategy, selector), OnScrollChangeListener {

  override fun onScrollChange(
    v: NestedScrollView?,
    scrollX: Int,
    scrollY: Int,
    oldScrollX: Int,
    oldScrollY: Int
  ) {
    manager.refresh()
  }

  override fun onAdded() {
    super.onAdded()
    root.setOnScrollChangeListener(this)
  }

  override fun onRemoved() {
    super.onRemoved()
    root.setOnScrollChangeListener(null as OnScrollChangeListener?)
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
    return selectByOrientation(candidates, orientation = VERTICAL)
  }
}
