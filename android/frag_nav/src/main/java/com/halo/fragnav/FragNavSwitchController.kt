/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.fragnav

interface FragNavSwitchController {
    fun switchTab(@FragNavController.TabIndex index: Int, transactionOptions: FragNavTransactionOptions?)
}
