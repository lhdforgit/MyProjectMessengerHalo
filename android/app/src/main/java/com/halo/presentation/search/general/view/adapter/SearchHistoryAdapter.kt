package com.halo.presentation.search.general.view.adapter

import android.text.TextUtils
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.hahalolo.cache.entity.search_main.SearchEntity
import com.halo.R
import com.halo.presentation.search.general.SearchHistoryListener
import com.halo.widget.ErrorHideEmptyHolder

class SearchHistoryAdapter(
    val requestManager: RequestManager?,
    val listener: SearchHistoryListener?
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var listData = mutableListOf<SearchEntity>()

    fun setData(newListData: MutableList<SearchEntity>) {
        val diffCallback = SearchDiffCallback(this.listData, newListData)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.listData.clear()
        this.listData.addAll(newListData)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.search_history_item -> SearchHistoryViewHolder.createHolder(
                parent,
                requestManager,
                listener
            )
            R.layout.search_history_text_item -> SearchHistoryTextViewHolder.createHolder(
                parent,
                requestManager,
                listener
            )
            else -> ErrorHideEmptyHolder.build(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        listData[position].let {
            when (holder) {
                is SearchHistoryViewHolder -> holder.bind(it)
                is SearchHistoryTextViewHolder -> holder.bind(it)
                is ErrorHideEmptyHolder -> holder.bind()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        listData[position].let {
            if (!TextUtils.isEmpty(it.name) && !TextUtils.isEmpty(it.id)) {
                R.layout.search_history_text_item

            }
        }
        return R.layout.error_hide_empty_holder
    }

    override fun getItemCount(): Int {
        return listData.size
    }
}