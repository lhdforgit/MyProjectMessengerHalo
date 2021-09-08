/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.materialdialogs.internal.list

/**
 * @author ndn
 * Create by ndn on 03/11/2018
 */
interface DialogAdapter<IT, SL> {

    fun replaceItems(
            items: List<IT>,
            listener: SL
    )

    fun disableItems(indices: IntArray)

    fun checkItems(indices: IntArray)

    fun uncheckItems(indices: IntArray)

    fun toggleItems(indices: IntArray)

    fun checkAllItems()

    fun uncheckAllItems()

    fun toggleAllChecked()

    fun isItemChecked(index: Int): Boolean

    fun getItemCount(): Int

    fun positiveButtonClicked()
}
