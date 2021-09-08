/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.player.internal

import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnScrollChangedListener
import com.hahalolo.player.core.*

internal open class ViewGroupBucket(
  manager: Manager,
  override val root: ViewGroup,
  strategy: Strategy,
  selector: Selector
) : Bucket(manager, root, strategy, selector) {

  private val globalScrollChangeListener = OnScrollChangedListener { manager.refresh() }

  override fun onAdded() {
    super.onAdded()
    onAddedInternal()
  }

  override fun onRemoved() {
    super.onRemoved()
    onRemovedInternal()
  }

  internal open fun onAddedInternal() {
    root.viewTreeObserver.addOnScrollChangedListener(globalScrollChangeListener)
  }

  internal open fun onRemovedInternal() {
    root.viewTreeObserver.removeOnScrollChangedListener(globalScrollChangeListener)
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
    return selectByOrientation(candidates, orientation = NONE_AXIS)
  }
}
