package com.hahalolo.messager.chatkit.view.mention.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.hahalolo.messager.chatkit.view.mention.adapter.ChatMentionViewHolder.Companion.create
import com.halo.data.room.entity.MemberEntity

class ChatMentionAdapter(
    private val requestManager: RequestManager,
    private val listener: ChatMentionClickListener
) : RecyclerView.Adapter<ChatMentionViewHolder>() {

    private val list = mutableListOf<MemberEntity>()

    fun updateList(items: MutableList<MemberEntity>) {
        list.clear()
        list.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatMentionViewHolder {
        return create(parent, requestManager, listener)
    }

    override fun onBindViewHolder(holder: ChatMentionViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}