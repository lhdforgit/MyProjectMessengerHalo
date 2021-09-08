/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.player.internal

import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.doOnLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import androidx.recyclerview.widget.RecyclerViewUtils
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.hahalolo.player.core.*

internal class RecyclerViewBucket(
    manager: Manager,
    override val root: RecyclerView,
    strategy: Strategy,
    selector: Selector
) : Bucket(manager, root, strategy, selector), RecyclerView.OnChildAttachStateChangeListener {

    companion object {
        internal fun RecyclerView.fetchOrientation(): Int {
            val layout = this.layoutManager ?: return NONE_AXIS
            return when (layout) {
                is LinearLayoutManager -> layout.orientation
                is StaggeredGridLayoutManager -> layout.orientation
                else -> {
                    return if (layout.canScrollVertically()) {
                        if (layout.canScrollHorizontally()) BOTH_AXIS
                        else VERTICAL
                    } else {
                        if (layout.canScrollHorizontally()) HORIZONTAL
                        else NONE_AXIS
                    }
                }
            }
        }
    }

    private val scrollListener = object : OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            manager.refresh()
        }
    }

    override fun onAdded() {
        super.onAdded()
        root.addOnScrollListener(scrollListener)
        root.addOnChildAttachStateChangeListener(this)
    }

    override fun onAttached() {
        super.onAttached()
        root.doOnLayout {
            if (ViewCompat.isAttachedToWindow(it)
                && it is RecyclerView
                && it.scrollState == RecyclerView.SCROLL_STATE_IDLE
            ) {
                manager.refresh()
            }
        }
    }

    override fun onRemoved() {
        super.onRemoved()
        root.removeOnScrollListener(scrollListener)
        root.removeOnChildAttachStateChangeListener(this)
    }

    override fun accepts(container: ViewGroup): Boolean {
        if (!ViewCompat.isAttachedToWindow(container)) return false
        val params = RecyclerViewUtils.fetchItemViewParams(container)
        return RecyclerViewUtils.accepts(root, params)
    }

    override fun allowToPlay(playback: Playback): Boolean {
        val container = playback.container
        return root.findContainingViewHolder(container) != null && super.allowToPlay(playback)
    }

    override fun selectToPlay(candidates: Collection<Playback>): Collection<Playback> {
        return selectByOrientation(candidates, orientation = root.fetchOrientation())
    }

    override fun onChildViewAttachedToWindow(view: View) {
        val holder = root.findContainingViewHolder(view)
        manager.master.requests.filter {
            RecyclerViewUtils.fetchViewHolder(it.key) === holder
        }.forEach {
            it.value.bucket = this
        }
    }

    override fun onChildViewDetachedFromWindow(view: View) {
        // do nothing
    }
}
