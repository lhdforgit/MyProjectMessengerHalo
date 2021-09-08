package com.hahalolo.messager.presentation.adapter.preload

import androidx.recyclerview.widget.DiffUtil

class AbsDiffCallback<T>(
    val oldList: MutableList<T>,
    val newList: MutableList<T>,
    val itemCallback: DiffUtil.ItemCallback<T>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int
    ): Boolean {
        return itemCallback.areItemsTheSame(oldList.get(oldItemPosition), newList.get(newItemPosition))
    }

    override fun areContentsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int
    ): Boolean {
        return itemCallback.areContentsTheSame(oldList.get(oldItemPosition), newList.get(newItemPosition))
    }
}