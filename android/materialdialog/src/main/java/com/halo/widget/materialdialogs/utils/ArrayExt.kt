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
internal inline fun <reified T> List<T>.pullIndices(indices: IntArray): List<T> {
    val result = mutableListOf<T>()
    for (index in indices) {
        result.add(this[index])
    }
    return result
}
