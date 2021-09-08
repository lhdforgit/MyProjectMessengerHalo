/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.hahalolo.messager.bubble.conversation.detail.gallery.adapter

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.ListPreloader.PreloadModelProvider
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.util.ViewPreloadSizeProvider

abstract class AbsPreloadAdapter<T>(
    private val itemCallback: DiffUtil.ItemCallback<T>,
    protected var viewPreloadSizeProvider: ViewPreloadSizeProvider<T>?,
    protected var requestManager: RequestManager
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    PreloadModelProvider<T> {

    protected var listDatas: MutableList<T> = mutableListOf()

    fun submitList(newList: MutableList<T>) {
        val diffCallback = AbsDiffCallback(listDatas, newList, itemCallback)
        val diffResult =
            DiffUtil.calculateDiff(diffCallback)
        listDatas.clear()
        listDatas.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }

    protected fun checkPreloadHolder(viewHolder: RecyclerView.ViewHolder) {
        if (viewHolder is PreloadHolder) {
            (viewHolder as PreloadHolder).getTargets().firstOrNull()?.run{
                viewPreloadSizeProvider?.setView(this )
            }
        }
    }

    override fun getPreloadItems(position: Int): MutableList<T> {
        return try {
            val list = listDatas
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