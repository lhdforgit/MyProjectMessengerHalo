/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package androidx.recyclerview.widget

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder

internal object RecyclerViewUtils {

    // Client must be careful when creating View for ViewHolder.
    // It is suggested to use 'LayoutInflater.inflate(int, ViewGroup, boolean)' with notnull ViewGroup.
    fun accepts(
        recyclerView: RecyclerView,
        params: RecyclerView.LayoutParams?
    ): Boolean {
        if (params == null) return false
        return params.mViewHolder == null || params.mViewHolder.mOwnerRecyclerView === recyclerView
    }

    fun fetchItemViewParams(target: View): RecyclerView.LayoutParams? {
        return try {
            var params = target.layoutParams
            var parent = target.parent
            while (params != null && params !is RecyclerView.LayoutParams) {
                params = (parent as? View)?.layoutParams
                parent = parent.parent
            }
            params as RecyclerView.LayoutParams?
        } catch (e: Exception) {
            null
        }
    }

    fun fetchViewHolder(target: View): ViewHolder? {
        val params = fetchItemViewParams(target)
        return if (params is RecyclerView.LayoutParams) params.mViewHolder else null
    }
}
