package com.halo.presentation.search.general.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.hahalolo.cache.entity.search_main.SearchEntity
import com.halo.R
import com.halo.databinding.SearchHistoryTextItemBinding
import com.halo.presentation.search.general.SearchHistoryListener

class SearchHistoryTextViewHolder(
    val binding: SearchHistoryTextItemBinding,
    val requestManager: RequestManager?,
    val listener : SearchHistoryListener?
) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(entity: SearchEntity?) {
        entity?.run {
            binding.nameTv.text = name
            binding.removeBt.setOnClickListener { listener?.deleteHistoryById(id, name)}
            binding.root.setOnLongClickListener {
                listener?.deleteHistoryById(id, name)
                true
            }
            binding.root.setOnClickListener {
                listener?.onClickItem(name, type, id)
            }
        }
    }

    companion object {
        @JvmStatic
        fun createHolder(
            parent: ViewGroup,
            requestManager: RequestManager?,
            listener: SearchHistoryListener?
        ): SearchHistoryTextViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = DataBindingUtil.inflate<SearchHistoryTextItemBinding>(
                inflater,
                R.layout.search_history_text_item,
                parent,
                false
            )
            return SearchHistoryTextViewHolder(binding, requestManager, listener)
        }
    }
}