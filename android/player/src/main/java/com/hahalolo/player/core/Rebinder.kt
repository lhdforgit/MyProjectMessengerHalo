/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.player.core

import android.os.Parcelable
import android.view.ViewGroup
import com.hahalolo.player.core.Master.Companion.NO_TAG
import com.hahalolo.player.core.Playback.*
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class Rebinder(val tag: @RawValue Any) : Parcelable {

  init {
    require(tag != NO_TAG) { "Rebinder requires unique tag." }
  }

  class Options {
    var threshold: Float = 0.65F
    var preload: Boolean = false
    var repeatMode: Int = Common.REPEAT_MODE_OFF
    var controller: Controller? = null
    var artworkHintListener: ArtworkHintListener? = null
    var tokenUpdateListener: TokenUpdateListener? = null
    var networkTypeChangeListener: NetworkTypeChangeListener? = null
    val callbacks = mutableSetOf<Callback>()
  }

  @JvmSynthetic
  @PublishedApi
  @IgnoredOnParcel
  internal var options = Options()

  inline fun with(options: Options.() -> Unit): Rebinder {
    this.options.apply(options)
    return this
  }

  fun bind(
    engine: Engine<*>,
    container: ViewGroup,
    callback: ((Playback) -> Unit)? = null
  ) {
    this.bind(engine.master, container, callback)
  }

  private fun bind(
    master: Master,
    container: ViewGroup,
    callback: ((Playback) -> Unit)? = null
  ) {
    val playable = master.playables.asSequence()
        .firstOrNull { it.value == tag /* equals */ }
        ?.key
    master.bind(
        requireNotNull(playable) { "Playable is null for tag $tag" },
        tag,
        container,
        Binder.Options().also {
          it.tag = tag
          it.threshold = options.threshold
          it.preload = options.preload
          it.repeatMode = options.repeatMode
          it.controller = options.controller
          it.artworkHintListener = options.artworkHintListener
          it.tokenUpdateListener = options.tokenUpdateListener
          it.networkTypeChangeListener = options.networkTypeChangeListener
          it.callbacks += options.callbacks
        }, callback
    )
    options = Options() // reset.
  }
}
