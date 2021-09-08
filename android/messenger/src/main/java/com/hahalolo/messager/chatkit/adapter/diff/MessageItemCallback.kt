/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.hahalolo.messager.chatkit.adapter.diff

import android.text.TextUtils
import androidx.recyclerview.widget.DiffUtil
import com.halo.data.entities.message.Message

class MessageItemCallback :
    DiffUtil.ItemCallback<Message>() {
    override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
        return TextUtils.equals(oldItem.messageId, newItem.messageId)
    }

    override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
        return TextUtils.equals(oldItem.messageId, newItem.messageId)
                && TextUtils.equals(oldItem.content, newItem.content)
    }
}