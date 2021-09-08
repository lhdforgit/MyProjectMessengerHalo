/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.fragnav.tabhistory

import com.halo.fragnav.FragNavPopController
import com.halo.fragnav.FragNavSwitchController
import java.util.*

class UniqueTabHistoryController(fragNavPopController: FragNavPopController,
                                 fragNavSwitchController: FragNavSwitchController) : CollectionFragNavTabHistoryController(fragNavPopController, fragNavSwitchController) {
    private val tabHistory = LinkedHashSet<Int>()

    override val collectionSize: Int
        get() = tabHistory.size

    override val andRemoveIndex: Int
        get() {
            val tabList = history
            val currentPage = tabList[tabHistory.size - 1]
            val targetPage = tabList[tabHistory.size - 2]
            tabHistory.remove(currentPage)
            tabHistory.remove(targetPage)
            return targetPage
        }

    override var history: ArrayList<Int>
        get() = ArrayList(tabHistory)
        set(history) {
            tabHistory.clear()
            tabHistory.addAll(history)
        }

    override fun switchTab(index: Int) {
        tabHistory.remove(index)
        tabHistory.add(index)
    }
}
