/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.player.internal

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentManager.FragmentLifecycleCallbacks
import androidx.fragment.app.commitNow
import androidx.lifecycle.Lifecycle.Event
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.hahalolo.player.Experiment
import com.hahalolo.player.core.Bucket
import com.hahalolo.player.core.Manager
import com.hahalolo.player.core.Master
import com.hahalolo.player.core.Playback

@Experiment
internal class DynamicFragmentRendererPlayback(
  manager: Manager,
  bucket: Bucket,
  container: ViewGroup,
  config: Config
) : Playback(manager, bucket, container, config) {

  init {
    check(tag != Master.NO_TAG) {
      "Using Fragment as Renderer requires a unique tag when setting up the Playable."
    }
  }

  private val fragmentManager: FragmentManager =
    when (manager.host) {
      is Fragment -> manager.host.childFragmentManager
      is FragmentActivity -> manager.host.supportFragmentManager
      else -> throw IllegalArgumentException("Need ${manager.host} to have a FragmentManager")
    }

  override fun onPlay() {
    playable?.setupRenderer(this)
    super.onPlay()
  }

  override fun onPause() {
    super.onPause()
    playable?.teardownRenderer(this)
  }

  override fun onAttachRenderer(renderer: Any?): Boolean {
    if (renderer == null) return false
    require(renderer is Fragment)
    // Learn from ViewPager2 FragmentPagerAdapter implementation.
    val view = renderer.view
    if (!renderer.isAdded && view != null) throw IllegalStateException("Bad state of Fragment.")
    if (renderer.isAdded) {
      return if (view == null) {
        scheduleAttachFragment(container, renderer)
        true
      } else {
        if (view.parent != null) {
          if (view.parent !== container) {
            addViewToContainer(view, container)
          }
          true
        } else {
          addViewToContainer(view, container)
          true
        }
      }
    } else {
      if (!fragmentManager.isStateSaved) {
        scheduleAttachFragment(container, renderer)
        fragmentManager.commitNow { add(renderer, tag.toString()) }
        return true
      } else {
        if (fragmentManager.isDestroyed) return false
        manager.lifecycleOwner.lifecycle.addObserver(object : LifecycleEventObserver {
          override fun onStateChanged(
            source: LifecycleOwner,
            event: Event
          ) {
            if (fragmentManager.isStateSaved) return
            source.lifecycle.removeObserver(this)
            if (ViewCompat.isAttachedToWindow(container)) {
              onAttachRenderer(renderer)
            }
          }
        })
        return true
      }
    }
  }

  override fun onDetachRenderer(renderer: Any?): Boolean {
    if (renderer == null) return false
    require(renderer is Fragment)
    val view = renderer.view
    if (view != null) {
      val parent = view.parent
      if (parent != null && parent is ViewGroup) {
        parent.removeAllViews()
      }
    }

    if (!renderer.isAdded) return true
    if (fragmentManager.isStateSaved) return true
    fragmentManager.commitNow { remove(renderer) }
    return true
  }

  @Suppress("MemberVisibilityCanBePrivate")
  internal fun scheduleAttachFragment(
    container: ViewGroup,
    fragment: Fragment
  ) {
    fragmentManager.registerFragmentLifecycleCallbacks(object : FragmentLifecycleCallbacks() {
      override fun onFragmentViewCreated(
        fm: FragmentManager,
        f: Fragment,
        v: View,
        savedInstanceState: Bundle?
      ) {
        if (f === fragment) {
          fm.unregisterFragmentLifecycleCallbacks(this)
          addViewToContainer(v, container)
        }
      }
    }, false)
  }

  internal fun addViewToContainer(
    view: View,
    container: ViewGroup
  ) {
    if (container.childCount > 1) {
      throw IllegalStateException("Container must not have more than one children.")
    }

    if (view.parent === container) return
    if (container.childCount > 0) container.removeAllViews()

    (view.parent as? ViewGroup)?.removeView(view)

    container.addView(view)
  }
}
