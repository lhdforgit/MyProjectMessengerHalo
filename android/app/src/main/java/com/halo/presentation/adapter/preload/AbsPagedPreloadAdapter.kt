/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.presentation.adapter.preload

import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.ListPreloader.PreloadModelProvider
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.util.ViewPreloadSizeProvider

abstract class AbsPagedPreloadAdapter<T : Any> :
    PagedListAdapter<T, RecyclerView.ViewHolder>,
    PreloadModelProvider<T> {
    protected var viewPreloadSizeProvider: ViewPreloadSizeProvider<T>?
    protected var requestManager: RequestManager

    constructor(
        diffCallback: DiffUtil.ItemCallback<T>,
        viewPreloadSizeProvider: ViewPreloadSizeProvider<T>?,
        requestManager: RequestManager
    ) : super(diffCallback) {
        this.viewPreloadSizeProvider = viewPreloadSizeProvider
        this.requestManager = requestManager
    }

    constructor(
        config: AsyncDifferConfig<T>,
        viewPreloadSizeProvider: ViewPreloadSizeProvider<T>?,
        requestManager: RequestManager
    ) : super(config) {
        this.viewPreloadSizeProvider = viewPreloadSizeProvider
        this.requestManager = requestManager
    }

    protected fun checkPreloadHolder(viewHolder: RecyclerView.ViewHolder) {
        if (viewHolder is PreloadHolder) {
            (viewHolder as PreloadHolder).getTargets().firstOrNull()?.run {
                viewPreloadSizeProvider?.setView(this)
            }
        }
    }

    override fun getPreloadItems(position: Int): MutableList<T> {
        return try {
            val list = currentList?: mutableListOf<T>()
            if (list.isNotEmpty() && position >= 0 && position < list.size) {
                if (position + getCountPreload() <= list.size) {
                    list.subList(position, position + getCountPreload())
                } else {
                    list.subList(position, list.size)
                }
            } else mutableListOf()
        } catch (e: Exception) {
            mutableListOf()
        }
    }

    protected open fun getCountPreload(): Int{
        return -1
    }

    override fun getPreloadRequestBuilder(item: T): RequestBuilder<*>? {
        return requestManager.asDrawable().load(item)
    }
}