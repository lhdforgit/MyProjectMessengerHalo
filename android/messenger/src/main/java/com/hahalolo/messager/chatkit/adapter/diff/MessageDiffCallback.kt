/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.hahalolo.messager.chatkit.adapter.diff

import android.text.TextUtils
import androidx.recyclerview.widget.DiffUtil
import com.hahalolo.messager.bubble.BubbleService
import com.halo.data.room.entity.MessageEntity

class MessageDiffCallback :
    DiffUtil.ItemCallback<MessageEntity>() {
    override fun areItemsTheSame(oldItem: MessageEntity, newItem: MessageEntity): Boolean {
        return TextUtils.equals(oldItem.messageId(), newItem.messageId())
    }

    override fun areContentsTheSame(oldItem: MessageEntity, newItem: MessageEntity): Boolean {
        return (TextUtils.equals(oldItem.messageId(), newItem.messageId())
                && TextUtils.equals(oldItem.messageText(), newItem.messageText())
                && TextUtils.equals(oldItem.messageType(), newItem.messageType())
                && oldItem.messageStatus() == newItem.messageStatus()
                && TextUtils.equals(oldItem.reactionTypes(), newItem.reactionTypes())
                && (!BubbleService.DEBUG || (TextUtils.equals(oldItem.saveType(), newItem.saveType())
                && TextUtils.equals(oldItem.bubbleSave(), newItem.bubbleSave()))))
    }
}