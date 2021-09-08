/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.fragnav

interface FragNavPopController {
    @Throws(UnsupportedOperationException::class)
    fun tryPopFragments(popDepth: Int, transactionOptions: FragNavTransactionOptions?): Int
}
