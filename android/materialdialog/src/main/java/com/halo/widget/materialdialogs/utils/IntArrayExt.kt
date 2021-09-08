/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.widget.materialdialogs.utils

/**
 * @author ndn
 * Create by ndn on 03/11/2018
 */

internal fun IntArray.appendAll(values: Collection<Int>): IntArray {
    val mutable = this.toMutableList()
    mutable.addAll(values)
    return mutable.toIntArray()
}

internal fun IntArray.removeAll(values: Collection<Int>): IntArray {
    val mutable = this.toMutableList()
    mutable.removeAll { values.contains(it) }
    return mutable.toIntArray()
}
