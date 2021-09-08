/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
@file:Suppress("unused")

package com.halo.widget.materialdialogs.list

import androidx.annotation.ArrayRes
import androidx.annotation.CheckResult
import com.halo.widget.materialdialogs.MaterialDialog
import com.halo.widget.materialdialogs.WhichButton.POSITIVE
import com.halo.widget.materialdialogs.actions.setActionButtonEnabled
import com.halo.widget.materialdialogs.assertOneSet
import com.halo.widget.materialdialogs.internal.list.DialogAdapter
import com.halo.widget.materialdialogs.internal.list.SingleChoiceDialogAdapter
import com.halo.widget.materialdialogs.utils.getStringArray

/**
 * @author ndn
 * Create by ndn on 03/11/2018
 *
 * @param res The string array resource to populate the list with.
 * @param items The literal string array to populate the list with.
 * @param initialSelection The initially selected item's index.
 * @param waitForPositiveButton When true, the [selection] listener won't be called until
 *    the positive action button is pressed. Defaults to true if the dialog has buttons.
 * @param selection A listener invoked when an item in the list is selected.
 */
@CheckResult
fun MaterialDialog.listItemsSingleChoice(
        @ArrayRes res: Int? = null,
        items: List<String>? = null,
        disabledIndices: IntArray? = null,
        initialSelection: Int = -1,
        waitForPositiveButton: Boolean = true,
        selection: SingleChoiceListener = null
): MaterialDialog {
    val array = items ?: getStringArray(res)?.toList()
    val adapter = getListAdapter()

    if (adapter is SingleChoiceDialogAdapter) {
        if (array != null) {
            adapter.replaceItems(array, selection)
        }
        if (disabledIndices != null) {
            adapter.disableItems(disabledIndices)
        }
        return this
    }

    assertOneSet("listItemsSingleChoice", items, res)
    setActionButtonEnabled(POSITIVE, initialSelection > -1)
    return customListAdapter(
            SingleChoiceDialogAdapter(
                    dialog = this,
                    items = array!!,
                    disabledItems = disabledIndices,
                    initialSelection = initialSelection,
                    waitForActionButton = waitForPositiveButton,
                    selection = selection
            )
    )
}

/** Checks a single or multiple choice list item. */
fun MaterialDialog.checkItem(index: Int) {
    val adapter = getListAdapter()
    if (adapter is DialogAdapter<*, *>) {
        adapter.checkItems(intArrayOf(index))
        return
    }
    throw UnsupportedOperationException(
            "Can't check item on adapter: ${adapter?.javaClass?.name ?: "null"}"
    )
}

/** Unchecks a single or multiple choice list item. */
fun MaterialDialog.uncheckItem(index: Int) {
    val adapter = getListAdapter()
    if (adapter is DialogAdapter<*, *>) {
        adapter.uncheckItems(intArrayOf(index))
        return
    }
    throw UnsupportedOperationException(
            "Can't uncheck item on adapter: ${adapter?.javaClass?.name ?: "null"}"
    )
}

/** Checks or unchecks a single or multiple choice list item. */
fun MaterialDialog.toggleItemChecked(index: Int) {
    val adapter = getListAdapter()
    if (adapter is DialogAdapter<*, *>) {
        adapter.toggleItems(intArrayOf(index))
        return
    }
    throw UnsupportedOperationException(
            "Can't toggle checked item on adapter: ${adapter?.javaClass?.name ?: "null"}"
    )
}

/** Returns true if a single or multiple list item is checked. */
fun MaterialDialog.isItemChecked(index: Int): Boolean {
    val adapter = getListAdapter()
    if (adapter is DialogAdapter<*, *>) {
        return adapter.isItemChecked(index)
    }
    throw UnsupportedOperationException(
            "Can't check if item is checked on adapter: ${adapter?.javaClass?.name ?: "null"}"
    )
}
