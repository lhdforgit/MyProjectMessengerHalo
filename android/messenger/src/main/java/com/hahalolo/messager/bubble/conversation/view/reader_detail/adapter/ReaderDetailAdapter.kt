package com.hahalolo.messager.bubble.conversation.view.reader_detail.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.hahalolo.messager.bubble.conversation.view.reader_detail.ChatReaderDetailListener
import com.halo.data.room.entity.MemberEntity
import com.hahalolo.messenger.R
import com.halo.widget.ErrorHideEmptyHolder

class ReaderDetailAdapter(val listener: ChatReaderDetailListener?, val requestManager: RequestManager) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var listReader: MutableList<MemberEntity>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == R.layout.chat_reader_detail_item) {
            ReaderDetailViewHolder.createHolder(parent, listener, requestManager)
        } else {
            ErrorHideEmptyHolder.build(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ReaderDetailViewHolder -> {
                holder.bind(listReader?.get(position))
            }
            is ErrorHideEmptyHolder -> {
                holder.bind()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        listReader?.run {
            get(position).run {
                if (getAvatar().isNotEmpty() && memberName().isNotEmpty()) {
                    return R.layout.chat_reader_detail_item
                }
            }
        }
        return R.layout.error_hide_empty_holder
    }

    override fun getItemCount(): Int {
        return listReader?.size ?: 0
    }

    fun updateData(listReader: MutableList<MemberEntity>?) {
        this.listReader = listReader
        notifyDataSetChanged()
    }
}