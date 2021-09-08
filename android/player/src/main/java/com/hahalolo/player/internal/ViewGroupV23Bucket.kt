/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.player.internal

import android.os.Build.VERSION_CODES
import android.view.View
import android.view.View.OnScrollChangeListener
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.hahalolo.player.core.Manager
import com.hahalolo.player.core.Selector
import com.hahalolo.player.core.Strategy

@RequiresApi(VERSION_CODES.M)
internal class ViewGroupV23Bucket(
  manager: Manager,
  root: ViewGroup,
  strategy: Strategy,
  selector: Selector
) : ViewGroupBucket(manager, root, strategy, selector), OnScrollChangeListener {

  override fun onScrollChange(
    v: View?,
    scrollX: Int,
    scrollY: Int,
    oldScrollX: Int,
    oldScrollY: Int
  ) {
    manager.refresh()
  }

  override fun onAddedInternal() {
    root.setOnScrollChangeListener(this)
  }

  override fun onRemovedInternal() {
    root.setOnScrollChangeListener(null as OnScrollChangeListener?)
  }
}
