/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.player.core

import androidx.annotation.CallSuper
import com.hahalolo.player.logInfo

abstract class AbstractBridge<RENDERER : Any> : Bridge<RENDERER> {

  protected val eventListeners = PlayerEventListeners()
  protected val errorListeners = ErrorListeners()
  protected val volumeListeners = VolumeChangedListeners()

  override fun addEventListener(listener: PlayerEventListener) {
    this.eventListeners.add(listener)
  }

  override fun removeEventListener(listener: PlayerEventListener?) {
    this.eventListeners.remove(listener)
  }

  override fun addVolumeChangeListener(listener: VolumeChangedListener) {
    this.volumeListeners.add(listener)
  }

  override fun removeVolumeChangeListener(listener: VolumeChangedListener?) {
    this.volumeListeners.remove(listener)
  }

  override fun addErrorListener(errorListener: ErrorListener) {
    this.errorListeners.add(errorListener)
  }

  override fun removeErrorListener(errorListener: ErrorListener?) {
    this.errorListeners.remove(errorListener)
  }

  @CallSuper
  override fun play() {
    "Bridge#play $this".logInfo()
  }

  @CallSuper
  override fun pause() {
    "Bridge#pause $this".logInfo()
  }

  override var videoSize: VideoSize = VideoSize.ORIGINAL

  // For backward compatibility.
  override var playerParameters: PlayerParameters = PlayerParameters.DEFAULT
}
