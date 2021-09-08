/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.player.core

import android.os.Build.VERSION
import android.view.View
import android.view.View.OnAttachStateChangeListener
import android.view.View.OnLayoutChangeListener
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.hahalolo.player.findCoordinatorLayoutDirectChildContainer
import com.hahalolo.player.internal.*
import com.hahalolo.player.media.VolumeInfo
import kotlin.LazyThreadSafetyMode.NONE

typealias Selector = (Collection<Playback>) -> Collection<Playback>

abstract class Bucket constructor(
  val manager: Manager,
  open val root: View,
  strategy: Strategy,
  internal val selector: Selector
) : OnAttachStateChangeListener, OnLayoutChangeListener {

  companion object {
    const val VERTICAL = RecyclerView.VERTICAL
    const val HORIZONTAL = RecyclerView.HORIZONTAL
    const val BOTH_AXIS = -1
    const val NONE_AXIS = -2

    private val playbackComparators = mapOf(
      HORIZONTAL to Playback.HORIZONTAL_COMPARATOR,
      VERTICAL to Playback.VERTICAL_COMPARATOR,
      BOTH_AXIS to Playback.BOTH_AXIS_COMPARATOR,
      NONE_AXIS to Playback.BOTH_AXIS_COMPARATOR
    )

    @JvmStatic
    internal operator fun get(
      manager: Manager,
      root: View,
      strategy: Strategy,
      selector: Selector
    ): Bucket {
      return when (root) {
        is RecyclerView -> RecyclerViewBucket(manager, root, strategy, selector)
        is NestedScrollView -> NestedScrollViewBucket(manager, root, strategy, selector)
        is ViewPager2 -> ViewPager2Bucket(manager, root, strategy, selector)
        is ViewPager -> ViewPagerBucket(manager, root, strategy, selector)
        is ViewGroup -> {
          if (VERSION.SDK_INT >= 23)
            ViewGroupV23Bucket(manager, root, strategy, selector)
          else
            ViewGroupBucket(manager, root, strategy, selector)
        }
        else -> throw IllegalArgumentException("Unsupported: $root")
      }
    }
  }

  internal var lock: Boolean = manager.lock
    get() = field || manager.lock
    set(value) {
      field = value
      manager.playbacks.filter { it.value.bucket === this }
        .forEach { it.value.lock = value }
      manager.refresh()
    }

  private val containers = mutableSetOf<Any>()

  private val behaviorHolder = lazy(NONE) {
    val container = manager.group.activity.window.peekDecorView()
      ?.findCoordinatorLayoutDirectChildContainer(root)
    val params = container?.layoutParams
    return@lazy if (params is CoordinatorLayout.LayoutParams) params else null
  }

  abstract fun accepts(container: ViewGroup): Boolean

  open fun allowToPlay(playback: Playback): Boolean {
    // Default judgement.
    return playback.token
      .shouldPlay()
  }

  abstract fun selectToPlay(candidates: Collection<Playback>): Collection<Playback>

  @CallSuper
  open fun addContainer(container: ViewGroup) {
    if (containers.add(container)) {
      if (container.isAttachedToWindow) {
        this.onViewAttachedToWindow(container)
      }
      container.addOnAttachStateChangeListener(this)
    }
  }

  @CallSuper
  open fun removeContainer(container: ViewGroup) {
    if (containers.remove(container)) {
      container.removeOnAttachStateChangeListener(this)
      container.removeOnLayoutChangeListener(this)
    }
  }

  @CallSuper
  override fun onViewAttachedToWindow(v: View?) {
    manager.onContainerAttachedToWindow(v)
  }

  @CallSuper
  override fun onViewDetachedFromWindow(v: View?) {
    manager.onContainerDetachedFromWindow(v)
  }

  @CallSuper
  override fun onLayoutChange(
    v: View?,
    left: Int,
    top: Int,
    right: Int,
    bottom: Int,
    oldLeft: Int,
    oldTop: Int,
    oldRight: Int,
    oldBottom: Int
  ) {
    if (v != null && (left != oldLeft || right != oldRight || top != oldTop || bottom != oldBottom)) {
      manager.onContainerLayoutChanged(v)
    }
  }

  @CallSuper
  open fun onAdded() {
  }

  @CallSuper
  open fun onAttached() {
    behaviorHolder.value?.let {
      val behavior = it.behavior
      if (behavior != null) {
        val behaviorWrapper = BehaviorWrapper(behavior, manager)
        it.behavior = behaviorWrapper
      }
    }
  }

  @CallSuper
  open fun onDetached() {
    if (behaviorHolder.isInitialized()) {
      behaviorHolder.value?.let {
        val behavior = it.behavior
        if (behavior is BehaviorWrapper) {
          behavior.onDetach()
          it.behavior = behavior.delegate
        }
      }
    }
  }

  @CallSuper
  open fun onRemoved() {
    mutableListOf(containers).onEach {
      manager.onRemoveContainer(it)
    }
      .clear()
  }

  internal val volumeInfo: VolumeInfo
    get() = bucketVolumeInfo

  internal var bucketVolumeInfo: VolumeInfo = VolumeInfo()
    set(value) {
      field = value
      manager.onBucketVolumeInfoUpdated(this, effectiveVolumeInfo(this.volumeInfo))
    }

  private val volumeConstraint: VolumeInfo
    get() = when (strategy) {
      Strategy.MULTI_PLAYER -> VolumeInfo(false, 0F)
      else -> VolumeInfo()
    }

  internal var strategy: Strategy = strategy
    set(value) {
      val from = field
      field = value
      val to = field
      if (from !== to) {
        manager.onBucketVolumeInfoUpdated(this, effectiveVolumeInfo(this.volumeInfo))
        manager.refresh()
      }
    }

  init {
    bucketVolumeInfo = manager.volumeInfo
  }

  internal fun effectiveVolumeInfo(origin: VolumeInfo): VolumeInfo {
    return with(origin) {
      val constraint = volumeConstraint
      VolumeInfo(mute || constraint.mute, volume.coerceAtMost(constraint.volume))
    }
  }

  // This operation should be considered heavy/expensive.
  protected fun selectByOrientation(
    candidates: Collection<Playback>,
    orientation: Int
  ): Collection<Playback> {
    if (lock) return emptyList()
    if (strategy == Strategy.NO_PLAYER) return emptyList()

    val playbackComparator = playbackComparators.getValue(orientation)
    val manualToAutoPlaybackGroups = candidates.sortedWith(playbackComparator)
      .groupBy { it.tag != Master.NO_TAG && it.config.controller != null }
      .withDefault { emptyList() }

    val manualCandidate = with(manualToAutoPlaybackGroups.getValue(true)) {
      val started = find { manager.master.manuallyStartedPlayable.get() === it.playable }
      return@with listOfNotNull(started ?: this@with.firstOrNull())
    }

    return if (manualCandidate.isNotEmpty()) {
      manualCandidate
    } else {
      selector(manualToAutoPlaybackGroups.getValue(false))
    }
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false
    other as Bucket
    if (manager !== other.manager) return false
    if (root !== other.root) return false
    return true
  }

  private var lazyHashCode: Int = -1

  override fun hashCode(): Int {
    if (lazyHashCode == -1) {
      lazyHashCode = manager.hashCode() * 31 + root.hashCode()
    }
    return lazyHashCode
  }
}
