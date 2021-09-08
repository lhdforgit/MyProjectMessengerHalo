/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.imageviewer.common.extensions

import android.util.SparseArray
import java.util.*

internal inline fun <T> SparseArray<T>.forEach(block: (Int, T) -> Unit) {
    val size = this.size()
    for (index in 0 until size) {
        if (size != this.size()) throw ConcurrentModificationException()
        block(this.keyAt(index), this.valueAt(index))
    }
}