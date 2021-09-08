/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.fragnav.tabhistory

import com.halo.fragnav.FragNavSwitchController

sealed class NavigationStrategy

class CurrentTabStrategy : NavigationStrategy()

class UnlimitedTabHistoryStrategy(val fragNavSwitchController: FragNavSwitchController) : NavigationStrategy()

class UniqueTabHistoryStrategy(val fragNavSwitchController: FragNavSwitchController) : NavigationStrategy()