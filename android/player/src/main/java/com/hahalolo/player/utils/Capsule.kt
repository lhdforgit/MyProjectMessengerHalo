/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.player.utils

import androidx.annotation.RestrictTo
import androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX
import com.hahalolo.player.core.Engine

/**
 * Singleton Holder
 */
@RestrictTo(LIBRARY_GROUP_PREFIX)
open class Capsule<T : Any, in A>(
  creator: (A) -> T,
  onCreate: ((T) -> Unit) = { if (it is Engine<*>) it.master.registerEngine(it) }
) {
  @Volatile private var instance: T? = null

  private var creator: ((A) -> T)? = creator
  private var onCreate: ((T) -> Unit)? = onCreate

  protected fun getInstance(arg: A): T {
    val check = instance
    if (check != null) {
      return check
    }

    return synchronized(this) {
      val doubleCheck = instance
      if (doubleCheck != null) {
        doubleCheck
      } else {
        val created = requireNotNull(creator)(arg)
        requireNotNull(onCreate)(created)
        instance = created
        creator = null
        onCreate = null
        created
      }
    }
  }

  fun get(arg: A): T = getInstance(arg)
}
