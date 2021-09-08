package com.halo.presentation.search.general.view.adapter

import android.text.TextUtils
import androidx.recyclerview.widget.DiffUtil
import com.hahalolo.cache.entity.search_main.SearchEntity

class SearchDiffCallback(
    private var oldList: MutableList<SearchEntity>,
    private var newList: MutableList<SearchEntity>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return TextUtils.equals(
            oldList[oldItemPosition].id,
            newList[newItemPosition].id
        )
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return TextUtils.equals(
            oldList[oldItemPosition].id,
            newList[newItemPosition].id
        )
                && TextUtils.equals(
            oldList[oldItemPosition].name,
            newList[newItemPosition].name
        )
    }
}