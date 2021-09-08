/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.fragnav.tabhistory

import com.halo.fragnav.FragNavPopController
import com.halo.fragnav.FragNavSwitchController
import java.util.*

class UnlimitedTabHistoryController(fragNavPopController: FragNavPopController,
                                    fragNavSwitchController: FragNavSwitchController) : CollectionFragNavTabHistoryController(fragNavPopController, fragNavSwitchController) {
    private val tabHistory = Stack<Int>()

    override val collectionSize: Int
        get() = tabHistory.size

    override val andRemoveIndex: Int
        get() {
            tabHistory.pop()
            return tabHistory.pop()
        }

    override var history: ArrayList<Int>
        get() = ArrayList(tabHistory)
        set(history) {
            tabHistory.clear()
            tabHistory.addAll(history)
        }

    override fun switchTab(index: Int) {
        tabHistory.push(index)
    }
}
