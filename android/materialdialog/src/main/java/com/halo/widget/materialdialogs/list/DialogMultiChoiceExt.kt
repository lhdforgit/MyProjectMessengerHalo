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
import com.halo.widget.materialdialogs.internal.list.MultiChoiceDialogAdapter
import com.halo.widget.materialdialogs.utils.getStringArray

/**
 * @author ndn
 * Create by ndn on 03/11/2018
 *
 * @param res The string array resource to populate the list with.
 * @param items The literal string array to populate the list with.
 * @param initialSelection The initially selected item indices.
 * @param waitForPositiveButton When true, the [selection] listener won't be called until
 *    the positive action button is pressed.
 * @param allowEmptySelection When true, the dialog allows to select 0 items as well
 *    otherwise at least one item must be selected
 * @param selection A listener invoked when an item in the list is selected.
 */
@CheckResult
fun MaterialDialog.listItemsMultiChoice(
        @ArrayRes res: Int? = null,
        items: List<String>? = null,
        disabledIndices: IntArray? = null,
        initialSelection: IntArray = IntArray(0),
        waitForPositiveButton: Boolean = true,
        allowEmptySelection: Boolean = false,
        selection: MultiChoiceListener = null
): MaterialDialog {
    val array = items ?: getStringArray(res)?.toList()
    val adapter = getListAdapter()

    if (adapter is MultiChoiceDialogAdapter) {
        if (array != null) {
            adapter.replaceItems(array, selection)
        }
        if (disabledIndices != null) {
            adapter.disableItems(disabledIndices)
        }
        return this
    }

    assertOneSet("listItemsMultiChoice", items, res)
    setActionButtonEnabled(POSITIVE, allowEmptySelection || initialSelection.isNotEmpty())
    return customListAdapter(
            MultiChoiceDialogAdapter(
                    dialog = this,
                    items = array!!,
                    disabledItems = disabledIndices,
                    initialSelection = initialSelection,
                    waitForActionButton = waitForPositiveButton,
                    allowEmptySelection = allowEmptySelection,
                    selection = selection
            )
    )
}

/** Checks a set of multiple choice list items. */
fun MaterialDialog.checkItems(indices: IntArray) {
    val adapter = getListAdapter()
    if (adapter is DialogAdapter<*, *>) {
        adapter.checkItems(indices)
        return
    }
    throw UnsupportedOperationException(
            "Can't check items on adapter: ${adapter?.javaClass?.name ?: "null"}"
    )
}

/** Unchecks a set of multiple choice list items. */
fun MaterialDialog.uncheckItems(indices: IntArray) {
    val adapter = getListAdapter()
    if (adapter is DialogAdapter<*, *>) {
        adapter.uncheckItems(indices)
        return
    }
    throw UnsupportedOperationException(
            "Can't uncheck items on adapter: ${adapter?.javaClass?.name ?: "null"}"
    )
}

/** Toggles the checked state of a set of multiple choice list items. */
fun MaterialDialog.toggleItemsChecked(indices: IntArray) {
    val adapter = getListAdapter()
    if (adapter is DialogAdapter<*, *>) {
        adapter.toggleItems(indices)
        return
    }
    throw UnsupportedOperationException(
            "Can't toggle checked items on adapter: ${adapter?.javaClass?.name ?: "null"}"
    )
}

/** Checks all multiple choice list items. */
fun MaterialDialog.checkAllItems() {
    val adapter = getListAdapter()
    if (adapter is DialogAdapter<*, *>) {
        adapter.checkAllItems()
        return
    }
    throw UnsupportedOperationException(
            "Can't check all items on adapter: ${adapter?.javaClass?.name ?: "null"}"
    )
}

/** Unchecks all single or multiple choice list items. */
fun MaterialDialog.uncheckAllItems() {
    val adapter = getListAdapter()
    if (adapter is DialogAdapter<*, *>) {
        adapter.uncheckAllItems()
        return
    }
    throw UnsupportedOperationException(
            "Can't uncheck all items on adapter: ${adapter?.javaClass?.name ?: "null"}"
    )
}

/** Toggles the checked state of all multiple choice list items. */
fun MaterialDialog.toggleAllItemsChecked() {
    val adapter = getListAdapter()
    if (adapter is DialogAdapter<*, *>) {
        adapter.toggleAllChecked()
        return
    }
    throw UnsupportedOperationException(
            "Can't uncheck all items on adapter: ${adapter?.javaClass?.name ?: "null"}"
    )
}
