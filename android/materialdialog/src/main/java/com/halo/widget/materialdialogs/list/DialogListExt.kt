/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
@file:Suppress("unused")

package com.halo.widget.materialdialogs.list

import androidx.annotation.ArrayRes
import androidx.annotation.CheckResult
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.halo.widget.materialdialogs.MaterialDialog
import com.halo.widget.materialdialogs.R
import com.halo.widget.materialdialogs.assertOneSet
import com.halo.widget.materialdialogs.internal.list.PlainListDialogAdapter
import com.halo.widget.materialdialogs.utils.getDrawable
import com.halo.widget.materialdialogs.utils.getStringArray
import com.halo.widget.materialdialogs.utils.inflate

/**
 * @author ndn
 * Create by ndn on 03/11/2018
 */

@CheckResult
fun MaterialDialog.getRecyclerView(): RecyclerView? {
    return this.contentRecyclerView
}

@CheckResult
fun MaterialDialog.getListAdapter(): RecyclerView.Adapter<*>? {
    return this.contentRecyclerView?.adapter
}

/**
 * Sets a custom list adapter to render custom list content.
 *
 * Cannot be used in combination with message, input, and some other types of dialogs.
 */
@CheckResult
fun MaterialDialog.customListAdapter(
        adapter: RecyclerView.Adapter<*>
): MaterialDialog {
    addContentRecyclerView()
    if (this.contentRecyclerView!!.adapter != null)
        throw IllegalStateException("An adapter has already been set to this dialog.")
    this.contentRecyclerView!!.adapter = adapter
    return this
}

/**
 * @param res The string array resource to populate the list with.
 * @param items The literal string array to populate the list with.
 * @param waitForPositiveButton When true, the [selection] listener won't be called until an item
 *    is selected and the positive action button is pressed. Defaults to true if the dialog has buttons.
 * @param selection A listener invoked when an item in the list is selected.
 */
@CheckResult
fun MaterialDialog.listItems(
        @ArrayRes res: Int? = null,
        items: List<String>? = null,
        disabledIndices: IntArray? = null,
        waitForPositiveButton: Boolean = true,
        selection: ItemListener = null
): MaterialDialog {
    assertOneSet("listItems", items, res)
    val array = items ?: getStringArray(res)?.toList()
    val adapter = getListAdapter()

    if (adapter is PlainListDialogAdapter) {
        if (array != null) {
            adapter.replaceItems(array, selection)
        }
        if (disabledIndices != null) {
            adapter.disableItems(disabledIndices)
        }
        return this
    }

    return customListAdapter(
            PlainListDialogAdapter(
                    dialog = this,
                    items = array!!,
                    disabledItems = disabledIndices,
                    waitForActionButton = waitForPositiveButton,
                    selection = selection
            )
    )
}

internal fun MaterialDialog.getItemSelector() =
        getDrawable(
                context = context, attr = R.attr.md_item_selector
        )

private fun MaterialDialog.addContentRecyclerView() {
    if (this.contentScrollView != null || this.contentCustomView != null) {
        throw IllegalStateException(
                "Your dialog has already been setup with a different type " +
                        "(e.g. with a message, input field, etc.)"
        )
    }
    if (this.contentRecyclerView != null) {
        return
    }
    this.contentRecyclerView = inflate(
            R.layout.md_dialog_stub_recyclerview, this.view
    )
    this.contentRecyclerView!!.attach(this)
    this.contentRecyclerView!!.layoutManager = LinearLayoutManager(windowContext)
    this.view.addView(this.contentRecyclerView, 1)
}
