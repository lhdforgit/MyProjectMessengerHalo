/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.fragnav.tabhistory

import android.os.Bundle

import com.halo.fragnav.FragNavPopController
import com.halo.fragnav.FragNavTransactionOptions

class CurrentTabHistoryController(fragNavPopController: FragNavPopController) : BaseFragNavTabHistoryController(fragNavPopController) {

    @Throws(UnsupportedOperationException::class)
    override fun popFragments(popDepth: Int,
                              transactionOptions: FragNavTransactionOptions?): Boolean {
        return fragNavPopController.tryPopFragments(popDepth, transactionOptions) > 0
    }

    override fun switchTab(index: Int) {}

    override fun onSaveInstanceState(outState: Bundle) {}

    override fun restoreFromBundle(savedInstanceState: Bundle?) {}
}
