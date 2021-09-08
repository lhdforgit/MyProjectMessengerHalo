package com.halo.presentation.search.general.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.hahalolo.cache.entity.search_main.SearchEntity
import com.halo.R
import com.halo.common.utils.ThumbImageUtils
import com.halo.databinding.SearchHistoryItemBinding
import com.halo.presentation.search.general.SearchHistoryListener

class SearchHistoryViewHolder(
    val binding: SearchHistoryItemBinding,
    val requestManager: RequestManager?,
    val listener : SearchHistoryListener?
) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(entity: SearchEntity?) {
        entity?.run {
            binding.nameTv.text = name
            requestManager?.load(ThumbImageUtils.thumbAvatar(avatar))
                ?.error(R.drawable.ic_dummy_personal_circle)
                ?.placeholder(R.drawable.ic_dummy_personal_circle)
                ?.circleCrop()
                ?.into(binding.avatarIv)
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
        ): SearchHistoryViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = DataBindingUtil.inflate<SearchHistoryItemBinding>(
                inflater,
                R.layout.search_history_item,
                parent,
                false
            )
            return SearchHistoryViewHolder(binding, requestManager, listener)
        }
    }
}