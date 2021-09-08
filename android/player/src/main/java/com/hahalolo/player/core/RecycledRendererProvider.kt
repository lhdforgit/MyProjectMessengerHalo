/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.player.core

import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.collection.SparseArrayCompat
import androidx.collection.forEach
import androidx.core.util.Pools.SimplePool
import com.hahalolo.player.media.Media
import com.hahalolo.player.onEachAcquired

abstract class RecycledRendererProvider @JvmOverloads constructor(
  private val poolSize: Int = 2
) : RendererProvider {

  private val pools = SparseArrayCompat<SimplePool<Any>>(2)

  @CallSuper
  override fun acquireRenderer(
    playback: Playback,
    media: Media
  ): Any {
    val rendererType = getRendererType(playback.container, media)
    val pool = pools.get(rendererType)
    return pool?.acquire() ?: createRenderer(playback, rendererType)
  }

  // Test: releaseRenderer(any(), any(), null) must return true.
  @CallSuper
  override fun releaseRenderer(
    playback: Playback,
    media: Media,
    renderer: Any?
  ): Boolean {
    if (renderer == null) return true
    val rendererType = getRendererType(playback.container, media)
    val pool = pools.get(rendererType) ?: SimplePool<Any>(poolSize).also {
      pools.put(rendererType, it)
    }
    return pool.release(renderer)
  }

  @CallSuper
  override fun clear() {
    pools.forEach { _, value ->
      value.onEachAcquired(::onClear)
    }
  }

  protected open fun getRendererType(
    container: ViewGroup,
    media: Media
  ): Int = 0

  // Must always create new Renderer.
  protected abstract fun createRenderer(
    playback: Playback,
    rendererType: Int
  ): Any

  protected open fun onClear(renderer: Any) = Unit
}
