/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.materialdialogs.internal.list

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.halo.widget.materialdialogs.MaterialDialog
import com.halo.widget.materialdialogs.internal.main.DialogLayout
import com.halo.widget.materialdialogs.utils.invalidateDividers
import com.halo.widget.materialdialogs.utils.waitForLayout

typealias InvalidateDividersDelegate = (scrolledDown: Boolean, atBottom: Boolean) -> Unit

/**
 * A [RecyclerView] which reports whether or not it's scrollable, along with reporting back to a
 * [DialogLayout] to invalidate dividers.
 *
 * @author ndn
 * Create by ndn on 03/11/2018
 */
class DialogRecyclerView(
        context: Context,
        attrs: AttributeSet? = null
) : RecyclerView(context, attrs) {

    private var invalidateDividersDelegate: InvalidateDividersDelegate? = null

    fun attach(dialog: MaterialDialog) {
        this.invalidateDividersDelegate = dialog::invalidateDividers
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        waitForLayout { invalidateDividers() }
        addOnScrollListener(scrollListeners)
    }

    override fun onDetachedFromWindow() {
        removeOnScrollListener(scrollListeners)
        super.onDetachedFromWindow()
    }

    override fun onScrollChanged(
            left: Int,
            top: Int,
            oldl: Int,
            oldt: Int
    ) {
        super.onScrollChanged(left, top, oldl, oldt)
        invalidateDividers()
    }

    private fun isAtTop(): Boolean {
        if (!isScrollable()) {
            return false
        }
        val lm = layoutManager
        return when (lm) {
            is LinearLayoutManager -> lm.findFirstCompletelyVisibleItemPosition() == 0
            is GridLayoutManager -> lm.findFirstCompletelyVisibleItemPosition() == 0
            else -> false
        }
    }

    private fun isAtBottom(): Boolean {
        if (!isScrollable()) {
            return false
        }
        val lastIndex = adapter!!.itemCount - 1
        val lm = layoutManager
        return when (lm) {
            is LinearLayoutManager -> lm.findLastCompletelyVisibleItemPosition() == lastIndex
            is GridLayoutManager -> lm.findLastCompletelyVisibleItemPosition() == lastIndex
            else -> false
        }
    }

    private val scrollListeners = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(
                recyclerView: RecyclerView,
                dx: Int,
                dy: Int
        ) {
            super.onScrolled(recyclerView, dx, dy)
            invalidateDividers()
        }
    }

    private fun invalidateDividers() {
        if (childCount == 0 || measuredHeight == 0) {
            return
        }
        invalidateDividersDelegate?.invoke(!isAtTop(), !isAtBottom())
    }

    private fun isScrollable(): Boolean {
        if (adapter == null) return false
        val lm = layoutManager
        val itemCount = adapter!!.itemCount
        @Suppress("UNREACHABLE_CODE")
        return when (lm) {
            is LinearLayoutManager -> {
                val diff = lm.findLastVisibleItemPosition() - lm.findFirstVisibleItemPosition()
                return itemCount > diff
            }
            is GridLayoutManager -> {
                val diff = lm.findLastVisibleItemPosition() - lm.findFirstVisibleItemPosition()
                return itemCount > diff
            }
            else -> {
                Log.w(
                        "MaterialDialogs",
                        "LayoutManager of type ${lm!!.javaClass.name} is currently unsupported."
                )
                return false
            }
        }
    }
}
