package com.hahalolo.messager.chatkit.adapter.diff

import androidx.recyclerview.widget.DiffUtil
import com.halo.data.room.entity.MessageEntity

class ChatMessageDiffCallback(
    val oldList: MutableList<MessageEntity>,
    val newList: MutableList<MessageEntity>,
    val itemCallback: DiffUtil.ItemCallback<MessageEntity>
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