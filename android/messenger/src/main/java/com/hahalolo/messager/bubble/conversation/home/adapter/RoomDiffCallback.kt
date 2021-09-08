/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.bubble.conversation.home.adapter

import android.text.TextUtils
import androidx.recyclerview.widget.DiffUtil
import com.hahalolo.messager.bubble.BubbleService
import com.halo.data.room.entity.ChannelEntity

open class RoomDiffCallback(private val userIdToken:String) : DiffUtil.ItemCallback<ChannelEntity>() {

    override fun areItemsTheSame(oldItem: ChannelEntity, newItem: ChannelEntity): Boolean {
        return TextUtils.equals(oldItem.channelId(), newItem.channelId())
    }

    override fun areContentsTheSame(oldItem: ChannelEntity, newItem: ChannelEntity): Boolean {
        return TextUtils.equals(oldItem.channelId(), newItem.channelId())
                && TextUtils.equals(oldItem.channelName(), newItem.channelName())
                && oldItem.countNewMessage() == newItem.countNewMessage()
                && TextUtils.equals(oldItem.contentLastMsg(), newItem.contentLastMsg())
                && (!BubbleService.DEBUG || (TextUtils.equals(oldItem.roomSave(), newItem.roomSave())
                && TextUtils.equals(oldItem.bubbleSave(), newItem.bubbleSave())))
    }
}